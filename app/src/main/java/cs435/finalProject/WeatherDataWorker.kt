package cs435.finalProject

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class WeatherDataWorker(context: Context, parameters: WorkerParameters) : Worker(context, parameters) {
    private val db = DBController(context)

    override fun doWork(): Result {
        Log.d("WeatherDataWorker", "Worker is executing hourlyAverage()")
        db.hourlyAverage()
        Log.d("WeatherDataWorker", "Worker completed successfully.")
        return Result.success()
    }
}