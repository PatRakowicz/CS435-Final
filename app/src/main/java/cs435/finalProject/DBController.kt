package cs435.finalProject

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DBController(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "weather.db"
        private const val DATABASE_VERSION = 1
        private const val TAG = "DBController"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = """
            CREATE TABLE WeatherData (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                date TEXT NOT NULL,
                temperature REAL NOT NULL,
                humidity REAL NOT NULL,
                uvi REAL NOT NULL,
                windspeed REAL NOT NULL
            );
        """.trimIndent()

        val createQuarterTable = """
            CREATE TABLE QuarterlyWeatherData (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                quarter TEXT NOT NULL,
                avg_temperature REAL NOT NULL,
                avg_humidity REAL NOT NULL,
                avg_uvi REAL NOT NULL,
                avg_windspeed REAL NOT NULL
            );
        """.trimIndent()

        db?.execSQL(createTable)
        db?.execSQL(createQuarterTable)
//        Log.d(TAG, "Tables created: WeatherData and HourlyWeatherData.")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS WeatherData")
        db?.execSQL("DROP TABLE IF EXISTS QuarterlyWeatherData")
        onCreate(db)
        Log.d(TAG, "Database upgraded. V:$newVersion")
    }

    fun fetchWeatherData(deviceIp: String) {
        val db = writableDatabase
        val apiUrl = "http://$deviceIp/api/weather"
        var httpURLConnection: HttpURLConnection? = null

        try {
//            Log.d(TAG, "Attempting to fetch weather data from $apiUrl")
            val url = URL(apiUrl)
            httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = "GET"

            if (httpURLConnection.responseCode == HttpURLConnection.HTTP_OK) {
                val bufferedReader =
                    BufferedReader(InputStreamReader(httpURLConnection.inputStream))
                val result = bufferedReader.readText()
                bufferedReader.close()

                val jsonObject = JSONObject(result)
                val temperature = jsonObject.getDouble("temperature")
                val humidity = jsonObject.getDouble("humidity")
                val timestamp = jsonObject.getString("timestamp")
                val uvi = jsonObject.getDouble("uvi")
                val windspeed = jsonObject.getDouble("wind_speed")


                val contentValue = ContentValues().apply {
                    put("date", timestamp)
                    put("temperature", temperature)
                    put("humidity", humidity)
                    put("uvi", uvi)
                    put("windspeed", windspeed)
                }
                db.insert("WeatherData", null, contentValue)
                Log.d(TAG, "Data inserted successfully: $contentValue")
            } else {
                Log.e(
                    TAG,
                    "Failed to fetch weather data. Response Code: ${httpURLConnection.responseCode}"
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching weather data: ${e.message}", e)
        } finally {
            httpURLConnection?.disconnect()
            db.close()
        }
    }

    fun getLatestWeatherEntry(): Map<String, Any>? {
        val db = readableDatabase
        val cursor = db.query(
            "WeatherData",
            arrayOf("date", "temperature", "humidity", "uvi", "windspeed"),
            null, null, null, null,
            "date DESC", "1"
        )

        if (cursor.moveToFirst()) {
            val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
            val temperature = cursor.getDouble(cursor.getColumnIndexOrThrow("temperature"))
            val humidity = cursor.getDouble(cursor.getColumnIndexOrThrow("humidity"))
            val uvi = cursor.getDouble(cursor.getColumnIndexOrThrow("uvi"))
            val windspeed = cursor.getDouble(cursor.getColumnIndexOrThrow("windspeed"))

            val result = mapOf(
                "date" to date,
                "temperature" to temperature,
                "humidity" to humidity,
                "uvi" to uvi,
                "windspeed" to windspeed
            )

//            Log.d(TAG, "Latest weather data retrieved: $result")
            cursor.close()
            return result
        } else {
            Log.w(TAG, "No data found in WeatherData table.")
            cursor.close()
            return null
        }
    }

    fun quarterAverage() {
        val db = writableDatabase

        val cursor = db.query(
            "WeatherData",
            arrayOf("COUNT(*) AS count"),
            null, null, null, null, null
        )
        val rowCount = if (cursor.moveToFirst()) {
            cursor.getInt(cursor.getColumnIndexOrThrow("count"))
        } else {
            0
        }
        cursor.close()
        Log.d(TAG, "Current number of entries in WeatherData: $rowCount")

        if (rowCount >= 15) {
            val currentQuarter =
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))

            val averagesCursor = db.query(
                "WeatherData",
                arrayOf(
                    "AVG(temperature) AS avg_temperature",
                    "AVG(humidity) AS avg_humidity",
                    "AVG(uvi) AS avg_uvi",
                    "AVG(windspeed) AS avg_windspeed"
                ),
                "_id IN (SELECT _id FROM WeatherData ORDER BY _id DESC LIMIT 15)",
                null, null, null, null
            )

            if (averagesCursor.moveToFirst()) {
                val avgTemperature =
                    averagesCursor.getDouble(averagesCursor.getColumnIndexOrThrow("avg_temperature"))
                val avgHumidity =
                    averagesCursor.getDouble(averagesCursor.getColumnIndexOrThrow("avg_humidity"))
                val avgUvi =
                    averagesCursor.getDouble(averagesCursor.getColumnIndexOrThrow("avg_uvi"))
                val avgWindspeed =
                    averagesCursor.getDouble(averagesCursor.getColumnIndexOrThrow("avg_windspeed"))

                val contentValues = ContentValues().apply {
                    put("quarter", currentQuarter)
                    put("avg_temperature", avgTemperature)
                    put("avg_humidity", avgHumidity)
                    put("avg_uvi", avgUvi)
                    put("avg_windspeed", avgWindspeed)
                }

                val inserted = db.insert("QuarterlyWeatherData", null, contentValues)


                if (inserted != -1L) {
                    Log.d(TAG, "Data pushed to QuarterlyWeatherData: $contentValues")
                    db.execSQL("DELETE FROM WeatherData WHERE _id IN (SELECT _id FROM WeatherData ORDER BY _id DESC LIMIT 15)")
                    Log.d(TAG, "Deleted 15 most recent entries from WeatherData.")
                } else {
                    Log.e(TAG, "Failed to insert quarter average.")
                }
            } else {
                Log.w(TAG, "No data found for quarter average calculation.")
            }
            averagesCursor.close()
        } else {
            Log.w(TAG, "Not enough entries in WeatherData to calculate average. Worker on standby.")
        }
    }

    fun getQuarterData(): Cursor {
        val db = readableDatabase
        return db.query(
            "QuarterlyWeatherData",
            null, null, null, null, null,
            "quarter DESC"
        )
    }
}