package mobg.g58093.weather_app.util

import java.util.Locale

/**
 * Retrieves the country name from the given country code.
 */
fun getCountryFromCode(countryCode: String): String {
    Locale.setDefault(Locale.ENGLISH)
    return Locale("", countryCode).displayCountry
}