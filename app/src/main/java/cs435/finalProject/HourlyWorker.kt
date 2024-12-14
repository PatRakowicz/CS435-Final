package cs435.finalProject

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class HourlyWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {
    companion object {
        private const val TAG = "HourlyWorker"
        private var isProcessing = false
    }

    override fun doWork(): Result {
        return if (isProcessing) {
            Log.w(TAG, "Worker already processing, skipping execution.")
            Result.success()
        } else {
            isProcessing = true
            try {
                val db = DBController(applicationContext)
                db.quarterAverage()
                Result.success()
            } catch (e: Exception) {
                Log.e(TAG, "Error processing 15-minute data: ${e.message}", e)
                Result.retry()
            } finally {
                isProcessing = false
            }
        }
    }
}