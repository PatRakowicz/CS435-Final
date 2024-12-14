package cs435.finalProject

import android.app.Activity
import android.os.Bundle
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    private lateinit var tempSwitch: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tempSwitch = findViewById(R.id.tempSwitch)
        tempSwitch.isChecked = AppSettings.isFahrenheit

        tempSwitch.setOnCheckedChangeListener { _, isChecked ->
            AppSettings.isFahrenheit = isChecked
            setResult(Activity.RESULT_OK)
        }
    }
}