package mobg.g58093.weather_app.util

import java.util.Locale

fun getCountryFromCode(countryCode: String): String {
    Locale.setDefault(Locale.ENGLISH)
    return Locale("", countryCode).displayCountry
}