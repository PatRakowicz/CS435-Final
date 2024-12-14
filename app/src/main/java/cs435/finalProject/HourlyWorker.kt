package cs435.finalProject

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class HourlyWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {
    companion object {
        private const val TAG = "HourlyWorker"
    }
    override fun doWork(): Result {
        val db = DBController(applicationContext)

        return try {
            db.quarterAverage()
            db.delOldMinData()
            Log.d(TAG, "15-minute average data processed.")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error processing 15-minute data: ${e.message}", e)
            Result.retry()
        }
    }
}