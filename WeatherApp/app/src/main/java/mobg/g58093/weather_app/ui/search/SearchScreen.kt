package mobg.g58093.weather_app.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import mobg.g58093.weather_app.util.AppViewModelProvider
import mobg.g58093.weather_app.network.responses.LocationWeatherResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    searchViewModel: SearchViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateToLocations : () -> Unit,
) {
    var searchQuery by remember { mutableStateOf("") }
    val searchState by searchViewModel.searchState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
                    .background(color = Color.LightGray, shape = AbsoluteRoundedCornerShape(8.dp)),
                placeholder = { Text(text = "Search...") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                       searchViewModel.searchLocation(searchQuery)
                    }
                ),
            )
        }
        when(val currentState = searchState) {
            is SearchApiState.Loading -> {
                Text("Searching...",
                    textAlign = TextAlign.Left,
                    style = TextStyle(color = Color.Gray))
            }
            is SearchApiState.Success -> {
                LocationsListResult(
                    modifier = modifier,
                    searchViewModel = searchViewModel,
                    searchResults = currentState.data,
                    navigateToLocations = navigateToLocations)
            }

            else -> {
                val errorMessage = (searchState as SearchApiState.Error).message
                Text("Unexpected error: $errorMessage")
            }
        }


    }
}

@Composable
fun LocationsListResult(
    modifier: Modifier = Modifier,
    searchViewModel: SearchViewModel,
    searchResults: List<LocationWeatherResponse>,
    navigateToLocations: () -> Unit
) {
    LazyColumn(modifier = modifier) {
        if (searchResults.isEmpty()) {
            item {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    text = "No locations found...",
                    textAlign = TextAlign.Left,
                    style = TextStyle(color = Color.Gray)
                )
            }
        } else {
            items(items = searchResults, key = null) { item ->
                Row(
                    modifier = Modifier
                        .clickable {
                            searchViewModel.addLocationToFavorites(item)
                            navigateToLocations()
                        }
                        .padding(15.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        textAlign = TextAlign.Left,
                        text = "${item.name} - ${item.state} - ${item.country}"
                    )
                }
            }
        }
    }
}