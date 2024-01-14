package mobg.g58093.weather_app.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import android.Manifest
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Singleton Repository for handling location permissions and GPS status.
 */
object LocationPermissionsAndGPSRepository {

    // status of location permissions.
    private val _permissions = MutableStateFlow(false)

    // status of GPS availability.
    private val _gps = MutableStateFlow(false)

    val permissions: StateFlow<Boolean> =
        _permissions.asStateFlow()

    val gps : StateFlow<Boolean> =
        _gps.asStateFlow()

    /**
     * Refreshes the checks for location permissions and GPS status.
     */
    fun refreshChecks(context: Context) {
        _permissions.value = hasLocationPermission(context)
        _gps.value = checkIsGPSEnabled(context)
    }
}


/**
 * Checks if the app has the necessary location permissions.
 */
fun hasLocationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

/**
 * Retrieves the current location using the fused location provider.
 *
 */
@SuppressLint("MissingPermission")
fun getCurrentLocation(context: Context, callback: (Double, Double) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1)
        .setWaitForAccurateLocation(false)
        .setMinUpdateIntervalMillis(1)
        .build()

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation
            Log.d("getCurrentLocation", "$location")
            if(location != null) {
                val lat = location.latitude
                val long = location.longitude
                callback(lat, long)
                fusedLocationClient.removeLocationUpdates(this) // unsubscribe from location updates
            }

        }
    }

    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
}

/**
 * Checks if the GPS is enabled on the device.
 */
fun checkIsGPSEnabled(context: Context) : Boolean {
    val mLocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}


