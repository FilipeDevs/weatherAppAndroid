package mobg.g58093.weather_app

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.util.concurrent.TimeUnit
import android.Manifest

/**
 * Manages all location related tasks for the app.
 */
//A callback for receiving notifications from the FusedLocationProviderClient.
lateinit var locationCallback: LocationCallback
//The main entry point for interacting with the Fused Location Provider
lateinit var locationProvider: FusedLocationProviderClient

var LOCATION_TAG = "getUserLocation()"

interface LocationResultCallback {
    fun onLocationResult(location: LatandLong)
}

@SuppressLint("MissingPermission")
fun getUserLocation(context: Context, callback: (LatandLong) -> Unit) {
    val locationProvider = LocationServices.getFusedLocationProviderClient(context)
    var currentUserLocation: LatandLong = LatandLong()

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            for (location in result.locations) {
                currentUserLocation = LatandLong(location.latitude, location.longitude)
                Log.d(LOCATION_TAG, "${location.latitude},${location.longitude}")
            }
            callback(currentUserLocation)
        }
    }

    if (hasPermissions(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    ) {
        locationUpdate(locationProvider, locationCallback)
    }
}

fun stopLocationUpdate(locationProvider: FusedLocationProviderClient, locationCallback: LocationCallback) {
    try {
        val removeTask = locationProvider.removeLocationUpdates(locationCallback)
        removeTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(LOCATION_TAG, "Location Callback removed.")
            } else {
                Log.d(LOCATION_TAG, "Failed to remove Location Callback.")
            }
        }
    } catch (se: SecurityException) {
        Log.e(LOCATION_TAG, "Failed to remove Location Callback.. $se")
    }
}

@SuppressLint("MissingPermission")
fun locationUpdate(locationProvider: FusedLocationProviderClient, locationCallback: LocationCallback) {
    val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = TimeUnit.SECONDS.toMillis(60)
        fastestInterval = TimeUnit.SECONDS.toMillis(30)
        maxWaitTime = TimeUnit.MINUTES.toMillis(2)
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    locationProvider.requestLocationUpdates(
        locationRequest,
        locationCallback,
        Looper.getMainLooper()
    )
}

data class LatandLong(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

fun hasPermissions(context: Context, vararg permissions: String): Boolean {
    for (permission in permissions) {
        if (ContextCompat.checkSelfPermission(context, permission)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
    }
    return true
}

fun askPermissions(
    activity: Context
) {
    //ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
        //Manifest.permission.ACCESS_COARSE_LOCATION), locationPermissionCode)
}


