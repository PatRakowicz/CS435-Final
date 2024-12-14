package cs435.finalProject

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var db : DBController

    private lateinit var temperature: TextView
    private lateinit var humidity: TextView
    private lateinit var uvi: TextView
    private lateinit var windspeed : TextView
    private lateinit var timestamp: TextView


    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        db = DBController(this)

        temperature = findViewById(R.id.temperature)
        humidity = findViewById(R.id.humidity)
        uvi = findViewById(R.id.uvi)
        windspeed = findViewById(R.id.windspeed)
        timestamp = findViewById(R.id.timestamp)

        startWeatherFetch()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.history_action -> {
                // History action -> Push into history view fragment
                true
            }

            R.id.settings_action -> {
                // Setttings action -> Push into settings fragment
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun startWeatherFetch() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                try {
                    Log.d(TAG, "Fetching weather data from API...")
                    db.fetchWeatherData("10.40.20.11:8000")
                    Log.d(TAG, "Weather data fetched and inserted.")

                    updateWeatherDisplay()

                    Log.d("WeatherFetch", "Weather data fetched and displayed")
                } catch (e: Exception) {
                    Log.e(TAG, "Error during weather data fetch: ${e.message}", e)
                }
                delay(60_000L)
            }
        }
    }

    private fun updateWeatherDisplay() {
        val latestWeather = db.getLatestWeatherEntry()

        runOnUiThread {
            if (latestWeather != null) {
                temperature.text = "Temperature: ${latestWeather["temperature"]}°C"
                humidity.text = "Humidity: ${latestWeather["humidity"]}%"
                uvi.text = "UV Index: ${latestWeather["uvi"]}"
                windspeed.text = "Wind Speed: ${latestWeather["windspeed"]} m/s"
                timestamp.text = "Last Update: ${latestWeather["date"]}"
                Log.d(TAG, "UI updated successfully with latest weather data.")
            } else {
                timestamp.text = "No Data Available"
                Log.w(TAG, "UI update failed: No data found in database.")
            }
        }
    }
}