package cs435.finalProject

import android.database.Cursor
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import android.widget.Toast

class WeatherListFragment : Fragment() {
    private lateinit var db: DBController
    private lateinit var listView: ListView

    companion object {
        private const val TAG = "WeatherListFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_weather_list, container, false)
        db = DBController(requireContext())
        listView = view.findViewById(R.id.listView)
        populateListView()
        return view
    }

    private fun populateListView() {
        val cursor: Cursor = db.getHourlyWeather()
        try {
            if (cursor.count == 0) {
                Toast.makeText(requireContext(), "No weather data available.", Toast.LENGTH_SHORT).show()
                Log.w(TAG, "populateListView: No data found.")
            } else {
                Log.d(TAG, "populateListView: Cursor loaded with ${cursor.count} rows.")
                val adapter = SimpleCursorAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_2,
                    cursor,
                    arrayOf("hour", "avg_temperature"),
                    intArrayOf(android.R.id.text1, android.R.id.text2),
                    0
                )
                listView.adapter = adapter
                Log.d(TAG, "populateListView: ListView adapter set.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "populateListView: Error loading data - ${e.message}", e)
        } finally {
            cursor.close()
        }
    }
}