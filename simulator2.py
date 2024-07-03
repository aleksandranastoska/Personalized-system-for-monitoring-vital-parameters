import sys
import random
import time
from datetime import datetime
import neurokit2 as nk
from influxdb_client import InfluxDBClient, Point, WritePrecision
from influxdb_client.client.write_api import SYNCHRONOUS

# Global dictionary to store previous values for each patient
previous_values = {}


def write_to_influx(patient_id, vitals):
    with InfluxDBClient(url="http://influxdb.local:8086", token="your-token", org="FINKI") as client:
        write_api = client.write_api(write_options=SYNCHRONOUS)
        point = Point.from_dict(vitals, WritePrecision.NS)
        write_api.write("proekt", "FINKI", point)
        print(f"Wrote data for {patient_id}")


def initialize_values(age):
    return {
        "temperature": initialize_temperature(),
        "pulse": initialize_pulse(age),
        "respiration_rate": initialize_respiration_rate(age),
        "blood_pressure": initialize_blood_pressure(),
        "ecg": initialize_ecg()
    }


def initialize_temperature():
    if random.random() < 0.8:
        body_temperature = round(random.uniform(36.1, 37.5), 1)
    else:
        body_temperature = round(random.uniform(35.1, 40.0), 1)
    return body_temperature


def initialize_pulse(age):
    if random.random() < 0.8:
        if age >= 18:
            pulse_rate = random.randint(60, 100)
        elif 6 <= age < 17:
            pulse_rate = random.randint(70, 100)
        elif 2 <= age < 6:
            pulse_rate = random.randint(80, 130)
        else:
            pulse_rate = random.randint(80, 160)
    else:
        if age >= 18:
            pulse_rate = random.randint(30, 200)
        elif 6 <= age < 17:
            pulse_rate = random.randint(40, 170)
        elif 2 <= age < 6:
            pulse_rate = random.randint(50, 220)
        else:
            pulse_rate = random.randint(50, 240)
    return pulse_rate


def initialize_respiration_rate(age):
    if random.random() < 0.8:
        if age >= 18:
            respiration_rate = random.randint(12, 20)
        elif 6 <= age < 17:
            respiration_rate = random.randint(18, 30)
        else:
            respiration_rate = random.randint(20, 40)
    else:
        if age >= 18:
            respiration_rate = random.randint(6, 40)
        elif 6 <= age < 17:
            respiration_rate = random.randint(8, 50)
        else:
            respiration_rate = random.randint(10, 60)
    return respiration_rate


def initialize_blood_pressure():
    if random.random() < 0.8:
        systolic = random.randint(90, 120)
        diastolic = random.randint(60, 80)
    else:
        systolic = random.randint(60, 220)
        diastolic = random.randint(40, 120)
    return systolic, diastolic


def initialize_ecg():
    ecg = nk.ecg_simulate(duration=8, sampling_rate=200, heart_rate=80)
    return ecg


def update_temperature(previous_temp):
    delta = random.uniform(-0.2, 0.2)
    new_temp = round(previous_temp + delta, 1)
    return new_temp


def update_pulse(previous_pulse, age):
    delta = random.randint(-5, 5)
    new_pulse = previous_pulse + delta
    if age >= 18:
        new_pulse = max(60, min(new_pulse, 100))
    elif 6 <= age < 17:
        new_pulse = max(70, min(new_pulse, 100))
    elif 2 <= age < 6:
        new_pulse = max(80, min(new_pulse, 130))
    else:
        new_pulse = max(80, min(new_pulse, 160))
    return new_pulse


def update_respiration_rate(previous_rate, age):
    delta = random.randint(-2, 2)
    new_rate = previous_rate + delta
    if age >= 18:
        new_rate = max(12, min(new_rate, 20))
    elif 6 <= age < 17:
        new_rate = max(18, min(new_rate, 30))
    else:
        new_rate = max(20, min(new_rate, 40))
    return new_rate


def update_blood_pressure(previous_bp):
    delta_systolic = random.randint(-10, 10)
    delta_diastolic = random.randint(-5, 5)
    new_systolic = max(90, min(previous_bp[0] + delta_systolic, 120))
    new_diastolic = max(60, min(previous_bp[1] + delta_diastolic, 80))
    return new_systolic, new_diastolic


def update_ecg(pulse):
    ecg = nk.ecg_simulate(duration=8, sampling_rate=200, heart_rate=pulse)
    return ecg


def generate_vitals(patient_id, age):
    global previous_values
    if patient_id not in previous_values:
        previous_values[patient_id] = initialize_values(age)
    else:
        previous_values[patient_id]["temperature"] = update_temperature(previous_values[patient_id]["temperature"])
        previous_values[patient_id]["pulse"] = update_pulse(previous_values[patient_id]["pulse"], age)
        previous_values[patient_id]["respiration_rate"] = update_respiration_rate(previous_values[patient_id]["respiration_rate"], age)
        previous_values[patient_id]["blood_pressure"] = update_blood_pressure(previous_values[patient_id]["blood_pressure"])
        previous_values[patient_id]["ecg"] = update_ecg(previous_values[patient_id]["pulse"])

    ecg_string = ','.join(map(str, previous_values[patient_id]["ecg"]))
    return {
        "measurement": patient_id,
        "fields": {
            'temperature': float(previous_values[patient_id]["temperature"]),
            'pulse': int(previous_values[patient_id]["pulse"]),
            'respiration_rate': int(previous_values[patient_id]["respiration_rate"]),
            'systolic': int(previous_values[patient_id]["blood_pressure"][0]),
            'diastolic': int(previous_values[patient_id]["blood_pressure"][1]),
            'ecg_string': ecg_string  # This is a string and should not be used in numeric operations
        },
        'time': datetime.utcnow()
    }


def main(patient_id, age):
    previous_values[patient_id] = initialize_values(age)
    while True:
        vitals = generate_vitals(patient_id, age)
        write_to_influx(patient_id, vitals)
        time.sleep(10)


if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: python script.py <patient_id> <age>")
        sys.exit(1)
    patient_id = sys.argv[1]
    age = int(sys.argv[2])
    main(patient_id, age)
