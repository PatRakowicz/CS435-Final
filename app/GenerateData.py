# Documents
# https://www.geeksforgeeks.org/python-pytz/
# https://flask.palletsprojects.com/en/stable/quickstart/
# https://www.w3schools.com/python/ref_random_uniform.asp
# https://www.geeksforgeeks.org/use-jsonify-instead-of-json-dumps-in-flask/#

from flask import Flask, jsonify
from random import uniform
from datetime import datetime, timedelta
import pytz

app = Flask(__name__)

mountain_tz = pytz.timezone("America/Denver")
simulated_time = datetime.now(mountain_tz)

def GenData():
    return {
        "temperature": round(uniform(20.0, 35.0), 2),
        "humidity": round(uniform(40.0, 90.0), 2),
        "uvi": round(uniform(0.0, 11.0), 2),
        "wind_speed": round(uniform(0.0, 20.0), 2)
    }

# API Route
@app.route('/api/weather', methods=['GET'])
def get_weather():
    global simulated_time
    weather_data = GenData()

    weather_data["timestamp"] = simulated_time.strftime("%Y-%m-%dT%H:%M:%S")
    print(f"Generated Weather Data: {weather_data}")
    simulated_time += timedelta(minutes=1)

    return jsonify(weather_data)

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8000, debug=True)
