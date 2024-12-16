package cs435.finalProject

import android.content.Intent
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

        val cursor: Cursor = db.getQuarterData()
        try {
            if (cursor.count == 0) {
                Toast.makeText(requireContext(), "No weather data available.", Toast.LENGTH_SHORT).show()
//                Log.w(TAG, "populateListView: No data found.")
            } else {
//                Log.d(TAG, "populateListView: Cursor loaded with ${cursor.count} rows.")
                val adapter = SimpleCursorAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_2,
                    cursor,
                    arrayOf("quarter", "avg_temperature"),
                    intArrayOf(android.R.id.text1, android.R.id.text2),
                    0
                )
                listView.adapter = adapter

                listView.setOnItemClickListener { parent, view, position, id ->
                    if (cursor.moveToPosition(position)) {
                        val quarter = cursor.getString(cursor.getColumnIndexOrThrow("quarter"))
                        val avgTemperature = cursor.getString(cursor.getColumnIndexOrThrow("avg_temperature"))

                        val intent = Intent(requireContext(), WeatherDetailActivity::class.java)
                        intent.putExtra("quarter", quarter)
                        intent.putExtra("avg_temperature", avgTemperature)
                        startActivity(intent)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "populateListView: Error loading data - ${e.message}", e)
        }

        return view
    }
}