# CS435-Final
This is the final project for android development. The idea is to have a "personal" weather station
that is hosted on a ESP32 (micro controller) that has a wifi component. It will grab data from sensors
then push that data to a http webapp hosted on the controller. Given the controller has a wifi component,
it will allow the app to connect to the controller and use `GET` requests to fetch the give data.

## How to run the Android app
There is nothing special on running the app, just copy the repo to a given place, open it with android studio
then allow for the gradle to build the app. Then hit the run button, everything should be configured to work off
the bat. Read How to run Data Generator for more information. If the application is not showing any data, you might
have to change what IP the android app is listening on. This can be done on line 102 on `MainActivity`

### Process of the application
Given this application and the use is for a personal weather station that can be viewed off an app on the phone,
the base process of the app is every request will happen each minute, populating the given text on the `MainActivity`.
Then every 15 minutes, it will grab the average from all of the minute entries into one 15 minute entry, then it will
populate a list view with the data, data being the time and date with the average temperature.

Local Data Generator (Python)
------
With this project the idea is to have a micro controller that will have wifi access
to an api request. With this in mind there is a python file `./app/GenerateData.py`.
This allows for an example of what type of data would be sent to the app, with api requests
and a json response for the application to use.

Side note, make sure to run the python app first or the data will 

### How to run Data Generator
It is very simple to get this data generator up and running. All is needed is to `cd` into the `/app` directory,
then proceed to run `python GenerateData.py`. This will start the python flask that will host a simple json request
form to allow for the app to grab that data. To find what types of IPs the application it is running on, once you
start the python app, it will display different IPs its using. For example `127.0.0.1:8000` (local host). Or if
you are on a private network `10.#.#.#:8000`

### How it works
The Python application runs a Flask server on port `8000`, exposing an API endpoint at `/api/weather`. Each API request 
triggers the generation of randomized weather data, including temperature, humidity, UV index, and wind speed. 
The simulated data also includes a timestamp that increments by one minute with each request.