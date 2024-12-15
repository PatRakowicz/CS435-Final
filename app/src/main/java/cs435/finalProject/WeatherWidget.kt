package cs435.finalProject

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews

// https://developer.android.com/develop/ui/views/appwidgets
class WeatherWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val db = DBController(context)
    val cursor = db.getQuarterData()

    val views = RemoteViews(context.packageName, R.layout.weather_widget)

    if (cursor.moveToFirst()) {
        val latestQuarter = cursor.getString(cursor.getColumnIndexOrThrow("quarter"))
        val avgTemp = cursor.getDouble(cursor.getColumnIndexOrThrow("avg_temperature"))

        views.setTextViewText(R.id.appwidget_text, latestQuarter)
        views.setTextViewText(R.id.appwidget_subtext, "Avg Temp: %.2f°C".format(avgTemp))
    } else {
        views.setTextViewText(R.id.appwidget_text, "No Data Available")
        views.setTextViewText(R.id.appwidget_subtext, "")
    }
    cursor.close()

    appWidgetManager.updateAppWidget(appWidgetId, views)
}