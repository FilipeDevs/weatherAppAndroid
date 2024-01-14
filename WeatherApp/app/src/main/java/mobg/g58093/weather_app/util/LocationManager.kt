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
import kotlinx.coroutines.flow.update

object LocationPermissionsAndGPSRepository {

    private val _permissions = MutableStateFlow(false)
    private val _gps = MutableStateFlow(false)

    val permissions: StateFlow<Boolean> =
        _permissions.asStateFlow()

    val gps : StateFlow<Boolean> =
        _gps.asStateFlow()

    fun refreshChecks(context: Context) {
        _permissions.value = hasLocationPermission(context)
        _gps.value = checkIsGPSEnabled(context)
    }
}



fun hasLocationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

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
                fusedLocationClient.removeLocationUpdates(this)
            }

        }
    }

    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
}

fun checkIsGPSEnabled(context: Context) : Boolean {
    val mLocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}


