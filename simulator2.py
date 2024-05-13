import random
import time
from datetime import datetime
import neurokit2 as nk


previous_values = {}

from influxdb_client import InfluxDBClient, Point, WritePrecision
from influxdb_client.client.write_api import SYNCHRONOUS

def write_to_influx(vitals):
    with InfluxDBClient(url="http://localhost:8086", token="LgIF73GNfozf_d6rAgPqP2myBjxoCS81NOUUBhDamHG0yp_K8ANxmgJooNExZPGvvQKn4O18V16w1o93ZSdwNA==", org="FINKI") as client:
        write_api = client.write_api(write_options=SYNCHRONOUS)
        point = Point("vitals").tag("patient", "patient1")
        for key, value in vitals.items():
            if key != "time":
                point = point.field(key, value)
        point.time(vitals['time'], WritePrecision.NS)
        write_api.write("proekt", "FINKI", point)
        print("HI")

def simulate_vitals():
    while True:
        vitals = generate_vitals(20)
        write_to_influx(vitals)
        time.sleep(1)

def initialize_values(age):
    # Initialize values
    global previous_values
    previous_values = {
        "temperature": initialize_temperature(),
        "pulse": initialize_pulse(age),
        "respiration_rate": initialize_respiration_rate(age),
        "blood_pressure": initialize_blood_pressure(),
        "ecg": initialize_ecg()
    }


# Functions for initializing values when first instancing a patient
def initialize_temperature():
    # Normal human body temperature in Celsius
    if random.random() < 0.8:
        body_temperature = round(random.uniform(36.1, 37.5), 1)
    else:
        body_temperature = round(random.uniform(35.1, 40.0), 1)
    return body_temperature


def initialize_pulse(age):
    # Normal resting pulse for adults ranges from 60 to 100 beats per minute
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
    # Normal respiration rate for adults ranges from 12 to 16 breaths per minute
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
    # Normal systolic/diastolic pressure in mmHg
    if random.random() < 0.8:
        systolic = random.randint(90, 120)
        diastolic = random.randint(60, 80)
    else:
        systolic = random.randint(60, 220)
        diastolic = random.randint(40, 120)
    return systolic


def initialize_ecg():
    # ECG value generator which takes into the current heart rate
    ecg = nk.ecg_simulate(duration=8, sampling_rate=200, heart_rate=80)
    return 1


def get_age_based_value(age, adult_min, adult_max, non_adult_min, non_adult_max):
    if age >= 18:
        return random.randint(adult_min, adult_max)
    else:
        return random.randint(non_adult_min, non_adult_max)


# Functions for updating values based on previous values
def update_temperature(previous_temp):
    # Use the previous temperature to slightly alter the new one
    delta = random.uniform(-0.2, 0.2)  # Small change to simulate real temperature fluctuation
    new_temp = round(previous_temp + delta, 1)
    return new_temp


def update_pulse(previous_pulse, age):
    # Pulse change is small unless there is a significant event
    delta = random.randint(-5, 5)
    new_pulse = previous_pulse + delta
    # Keep pulse within reasonable boundaries based on age
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
    # Adjust rates for age groups
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
    # new_systolic = max(90, min(previous_bp[0] + delta_systolic, 120))
    # new_diastolic = max(60, min(previous_bp[1] + delta_diastolic, 80))
    return 1


def update_ecg(pulse):
    ecg = nk.ecg_simulate(duration=8, sampling_rate=200, heart_rate=pulse)
    return 1


def generate_vitals(age):
    global previous_values
    if not previous_values:
        initialize_values(age)
    else:
        previous_values["temperature"] = update_temperature(previous_values["temperature"])
        previous_values["pulse"] = update_pulse(previous_values["pulse"], age)
        previous_values["respiration_rate"] = update_respiration_rate(previous_values["respiration_rate"], age)
        previous_values["blood_pressure"] = update_blood_pressure(previous_values["blood_pressure"])
        previous_values["ecg"] = update_ecg(previous_values["pulse"])

    return {
        'time': datetime.utcnow(),
        'temperature': previous_values["temperature"],
        'pulse': previous_values["pulse"],
        'respiration_rate': previous_values["respiration_rate"],
        'blood_pressure': previous_values["blood_pressure"],
        'ecg': previous_values["ecg"]
    }


# def simulate_vitals(age):
#     while True:
#         vitals = generate_vitals(age)
#         print(vitals)
#         # Integrate storing this data to InfluxDB
#         time.sleep(1)


if __name__ == "__main__":
    age = 20
    simulate_vitals()
    print("HI")
