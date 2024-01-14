package mobg.g58093.weather_app

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mobg.g58093.weather_app.data.WeatherRepository
import mobg.g58093.weather_app.data.WeatherEntry
import mobg.g58093.weather_app.network.RetroApi
import mobg.g58093.weather_app.network.isOnline
import mobg.g58093.weather_app.network.responses.WeatherResponse
import mobg.g58093.weather_app.util.PropertiesManager
import mobg.g58093.weather_app.util.getDynamicResourceId

/**
 * Implementation of App Widget functionality.
 */
class WeatherWidget : AppWidgetProvider() {


    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        if (intent?.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            context?.let {
                val appWidgetManager = AppWidgetManager.getInstance(it)
                val appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)

                if (appWidgetIds != null) {
                    for (appWidgetId in appWidgetIds) {
                        updateAppWidget(it, appWidgetManager, appWidgetId)
                    }
                }
            }
        }
    }

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

    CoroutineScope(Dispatchers.IO).launch {
        // Try to load current location
        var weatherEntry : WeatherEntry? = WeatherRepository.getWeatherEntryCurrentLocation()
        if(weatherEntry == null) { // if no data found for current location load another location
            weatherEntry = WeatherRepository.getFirstNonCurrentLocationEntry()
        }

        val response = weatherEntry?.let { getWeather(it.latitude, it.longitude, context) }

        val views = RemoteViews(context.packageName, R.layout.weather_widget)


        // Set up the onClickPendingIntent for the Refresh Button
        val updateIntent = Intent(context, WeatherWidget::class.java)
        updateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(appWidgetId))

        val pendingUpdateIntent = PendingIntent.getBroadcast(
            context,
            appWidgetId,
            updateIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        views.setOnClickPendingIntent(R.id.reload, pendingUpdateIntent)

        response?.let {
            views.setTextViewText(R.id.location, it.name)
            views.setTextViewText(R.id.temp, "${it.main.temp.toInt()}°C")
            views.setImageViewResource(R.id.weather, getDynamicResourceId(it.weather[0].icon))
        }
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}

private suspend fun getWeather(lat : Double, long : Double, context: Context) : WeatherResponse? {

    val apiKey = PropertiesManager.getApiKey(context)
    val units = PropertiesManager.getUnits(context)

    var response : WeatherResponse? = null
    try {
        if (isOnline(context)) {
            // Weather API call to OpenWeather
            response = RetroApi.weatherService.getWeatherByCoordinates(
                lat, long, units, apiKey
            )
        }
    } catch (e: Exception) {
        return null
    }

    return response
}