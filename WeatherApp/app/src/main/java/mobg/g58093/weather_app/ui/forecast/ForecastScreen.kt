package mobg.g58093.weather_app.ui.forecast

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import mobg.g58093.weather_app.util.AppViewModelProvider
import mobg.g58093.weather_app.R
import mobg.g58093.weather_app.data.ForecastEntry
import mobg.g58093.weather_app.util.getDynamicResourceId

@Composable
fun ForecastScreen(
    modifier: Modifier = Modifier,
    forecastViewModel: ForecastViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val forecastState by forecastViewModel.forecastState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        when (val currentState = forecastState) {
            is ForecastApiState.Loading -> {
                Text(text = stringResource(R.string.loading))
            }

            is ForecastApiState.Success -> {
                ForecastList(
                    forecastList = currentState.data,
                    modifier = modifier
                )
            }

            else -> { // Error
                Text((forecastState as ForecastApiState.Error).message)
            }
        }
    }
}

@Composable
private fun ForecastList(forecastList: List<ForecastEntry>, modifier: Modifier) {
    LazyColumn(modifier = modifier) {
        items(items = forecastList, key = { it.id }) { item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier.padding(horizontal = 20.dp)
            ) {
                // Date
                Text(
                    text = item.date,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight(400),
                        color = MaterialTheme.colorScheme.secondary,
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painter = painterResource(id = R.drawable.water),
                    contentDescription = "water icon"
                )
                Spacer(modifier = Modifier.weight(1f))
                // Humidity
                Text(
                    text = "${item.humidity}%",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight(400),
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                // Weather Icon
                Image(
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp),
                    painter = painterResource(id = getDynamicResourceId(item.icon)),
                    contentDescription = "weather icon",
                )
                Spacer(modifier = Modifier.weight(1f))
                // Forecast temperature
                Text(
                    text = "${item.temp}°C",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight(400),
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                )
            }
        }
    }
}



