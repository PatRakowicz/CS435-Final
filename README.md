# CS435-Final
This is the final project for android development. The idea is to have a "personal" weather station
that is hosted on a ESP32 (micro controller) that has a wifi component. It will grab data from sensors
then push that data to a http webapp hosted on the controller. Given the controller has a wifi component,
it will allow the app to connect to the controller and use GET requests to fetch the give data.


Local Data Generator (Python)
------
With this project the idea is to have a micro controller that will have wifi access
to an api request. With this in mind there is a python file `./app/GenerateData.py`.
This allows for an example of what type of data would be sent to the app, with api requests
and a json response for the application to use.

## How to run Data Generator
It is very simple to get this data generator up and running. All is needed is to cd into the `/app` directory,
then proceed to run `python GenerateData.py`. This will start the python flask that will host a simple json request
form to allow for the app to grab that data.

### How it works
The idea is to have the python application running in the background that will have an exposed port `8000`,
then allow for every request happening to the python application it will randomly grab a value from a given range.