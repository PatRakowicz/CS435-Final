package cs435.finalProject

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class Worker(context: Context, parameters: WorkerParameters) : Worker(context, parameters) {
    private val db = DBController(context)

    override fun doWork(): Result {
        db.hourlyAverage()
        return Result.success()
    }
}