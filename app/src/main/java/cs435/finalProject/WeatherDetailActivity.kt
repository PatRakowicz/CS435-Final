package cs435.finalProject

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class WeatherDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_weather_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val quarterTextView: TextView = findViewById(R.id.quarterTextView)
        val avgTemperatureTextView: TextView = findViewById(R.id.avgTemperatureTextView)
        val humidityTextView: TextView = findViewById(R.id.humidityTextView)
        val uviTextView: TextView = findViewById(R.id.uviTextView)
        val windSpeedTextView: TextView = findViewById(R.id.windSpeedTextView)

        val quarter = intent.getStringExtra("quarter") ?: "Not Available"
        val avgTemperature = intent.getStringExtra("temp")?.toDoubleOrNull() ?: 0.0
        val humidity = intent.getStringExtra("humidity")?.toDoubleOrNull() ?: 0.0
        val uvi = intent.getStringExtra("uvi")?.toDoubleOrNull() ?: 0.0
        val windSpeed = intent.getStringExtra("windSpeed")?.toDoubleOrNull() ?: 0.0

        quarterTextView.text = "Quarter: $quarter"
        avgTemperatureTextView.text = "Temperature: %.2f°C".format(avgTemperature)
        humidityTextView.text = "Humidity: %.2f%%".format(humidity)
        uviTextView.text = "UV Index: %.2f".format(uvi)
        windSpeedTextView.text = "Wind Speed: %.2f km/h".format(windSpeed)
    }
}