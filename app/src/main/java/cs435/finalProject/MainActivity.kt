package cs435.finalProject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

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
        WorkManager.getInstance(this).cancelAllWorkByTag("WeatherWorker")
        scheduleQuarterWorker()
    }

    override fun onResume() {
        super.onResume()
//        Log.d(TAG, "MainActivity resumed - refreshing weather display.")
        updateWeatherDisplay()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.history_action -> {
                // History action -> Attach Weather List to main
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main, WeatherListFragment())
                    .addToBackStack(null)
                    .commit()
                true
            }

            R.id.settings_action -> {
                // Setttings action -> Push into settings activity
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun startWeatherFetch() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                try {
//                    Log.d(TAG, "Fetching weather data from API...")
                    db.fetchWeatherData("10.40.20.11:8000")
//                    Log.d(TAG, "Weather data fetched and inserted.")

                    updateWeatherDisplay()

//                    Log.d("WeatherFetch", "Weather data fetched and displayed")
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
                val tempVal = latestWeather["temperature"] as Double
                val windSpeedVal = latestWeather["windspeed"] as Double

                val displayTemp = if (AppSettings.isFahrenheit) (tempVal * 9 / 5) + 32 else tempVal
                val tempUnit = if (AppSettings.isFahrenheit) "°F" else "°C"

                val displayWindSpeed = if (AppSettings.isMilesPerHour) windSpeedVal * 2.23694 else windSpeedVal
                val windSpeedUnit = if (AppSettings.isMilesPerHour) "mph" else "m/s"

                temperature.text = "Temperature: %.2f%s".format(displayTemp, tempUnit)
                humidity.text = "Humidity: %.2f%%".format(latestWeather["humidity"] as Double)
                uvi.text = "UV Index: %.2f".format(latestWeather["uvi"] as Double)
                windspeed.text = "Wind Speed: %.2f %s".format(displayWindSpeed, windSpeedUnit)
                timestamp.text = "Last Update: ${latestWeather["date"]}"
//                Log.d(TAG, "UI updated with latest weather data.")


                // Not working
                val textAlignment =
                    if (AppSettings.isCenteredText) View.TEXT_ALIGNMENT_CENTER
                    else View.TEXT_ALIGNMENT_TEXT_START
                temperature.textAlignment = textAlignment
                humidity.textAlignment = textAlignment
                uvi.textAlignment = textAlignment
                windspeed.textAlignment = textAlignment
                timestamp.textAlignment = textAlignment
            } else {
                timestamp.text = "No Data Available"
                Log.w(TAG, "UI update failed: No data found.")
            }
        }
    }

    private fun scheduleQuarterWorker() {
        //https://developer.android.com/develop/background-work/background-tasks/persistent/getting-started/define-work#tag_work
        //https://developer.android.com/develop/background-work/background-tasks/persistent/how-to/manage-work
        val workRequest = PeriodicWorkRequestBuilder<QuarterWorker>(15, TimeUnit.MINUTES)
            .addTag("WeatherWorker")
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "UniqueWeatherWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}