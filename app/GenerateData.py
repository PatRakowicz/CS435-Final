from flask import Flask, jsonify
from random import uniform
from datetime import datetime, timedelta

app = Flask(__name__)

simulated_time = datetime.utcnow()

def generate_weather_data():
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

    weather_data = generate_weather_data()
    weather_data["timestamp"] = simulated_time.strftime("%Y-%m-%dT%H:%M:%SZ")

    print(f"Generated Weather Data: {weather_data}")

    simulated_time += timedelta(minutes=1)

    return jsonify(weather_data)

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8000, debug=True)
