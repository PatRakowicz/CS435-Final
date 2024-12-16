package cs435.finalProject

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class QuarterWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {
    companion object {
        private const val TAG = "HourlyWorker"
    }

    override fun doWork(): Result {
        return try {
            val db = DBController(applicationContext)
            db.quarterAverage()
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error processing 15-minute data: ${e.message}", e)
            Result.retry()
        }
    }
}