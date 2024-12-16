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

        val quarter = intent.getStringExtra("quarter")
        val avgTemperature = intent.getStringExtra("avg_temperature")

        quarterTextView.text = "Quarter: $quarter"
        avgTemperatureTextView.text = "Average Temperature: $avgTemperature"
    }
}