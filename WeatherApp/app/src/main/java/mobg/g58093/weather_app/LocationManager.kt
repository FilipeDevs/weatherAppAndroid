package mobg.g58093.weather_app

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import android.Manifest
import android.widget.Toast

/**
 * Manages all location related tasks for the app.
 */
//A callback for receiving notifications from the FusedLocationProviderClient.
lateinit var locationCallback: LocationCallback
//The main entry point for interacting with the Fused Location Provider
lateinit var locationProvider: FusedLocationProviderClient

var LOCATION_TAG = "getUserLocation()"

@SuppressLint("MissingPermission")
fun getUserLocation(context: Context, callback: (LocationState) -> Unit) {
    val locationProvider = LocationServices.getFusedLocationProviderClient(context)

    if (hasPermissions(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    ) {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.locations.firstOrNull()
                if (location != null) {
                    val locationState = LocationState(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        isLocationPermissionGranted = true
                    )
                    callback(locationState)
                }
            }
        }
        locationUpdate(locationProvider, locationCallback)
    } else {
        // Permission not granted
        val locationState = LocationState(isLocationPermissionGranted = false)
        callback(locationState)
    }
}

fun stopLocationUpdate() {
    try {
        //Removes all location updates for the given callback.
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
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    locationProvider.requestLocationUpdates(
        locationRequest,
        object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                // Process the first location result
                val location = result.locations.firstOrNull()
                if (location != null) {
                    locationCallback.onLocationResult(LocationResult.create(listOf(location)))
                    // Remove updates after the first location
                    locationProvider.removeLocationUpdates(this)
                }
            }
        },
        Looper.getMainLooper()
    )
}

data class LocationState(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val selectedLocation: String = "",
    val isLocationPermissionGranted: Boolean = false // New flag
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



