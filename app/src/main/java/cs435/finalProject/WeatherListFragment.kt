package cs435.finalProject

import android.database.Cursor
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.SimpleCursorAdapter

class WeatherListFragment : Fragment() {
    private lateinit var db: DBController
    private lateinit var listView: ListView

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
        val adapter = SimpleCursorAdapter(
            requireContext(),
            android.R.layout.simple_list_item_2,
            cursor,
            arrayOf("hour", "avg_temperature"),
            intArrayOf(android.R.id.text1, android.R.id.text2),
            0
        )
        listView.adapter = adapter
    }
}