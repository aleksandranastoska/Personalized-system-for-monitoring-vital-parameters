import random
import time
from datetime import datetime
import neurokit2 as nk

import influxdb_client, os, time
from influxdb_client import InfluxDBClient, Point, WritePrecision
from influxdb_client.client.write_api import SYNCHRONOUS

token = "LgIF73GNfozf_d6rAgPqP2myBjxoCS81NOUUBhDamHG0yp_K8ANxmgJooNExZPGvvQKn4O18V16w1o93ZSdwNA=="
org = "FINKI"
url = "http://localhost:8086"

write_client = influxdb_client.InfluxDBClient(url=url, token=token, org=org)

previous_values = {}

from influxdb_client import InfluxDBClient, Point, WritePrecision
from influxdb_client.client.write_api import SYNCHRONOUS
bucket = "proekt"

# write_api = client.write_api(write_options=SYNCHRONOUS)
write_api_2 = influxdb_client.WriteApi(write_options=SYNCHRONOUS, influxdb_client=write_client)
for value in range(5):
    point = (
        Point("measurement1")
        .tag("tagname1", "tagvalue1")
        .field("field1", value)
    )
    write_api_2.write(bucket=bucket, org="FINKI", record=point)
    time.sleep(1)  # separate points by 1 second

    query_api = write_client.query_api()

    query = """from(bucket: "proekt")
     |> range(start: -10m)
     |> filter(fn: (r) => r._measurement == "measurement1")"""
    tables = query_api.query(query, org="FINKI")

    for table in tables:
        for record in table.records:
            print(record)
