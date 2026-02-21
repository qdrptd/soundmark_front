package com.example.soundmark.ui.views.add

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.soundmark.data.model.GeoLocation
import com.example.soundmark.data.model.Track
import com.example.soundmark.data.mock.MockDataSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    viewModel: AddViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showSongBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val songSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.all { it }
        if (granted) {
            viewModel.fetchNearbyPlacesWithGPS()
        } else {
            viewModel.onPermissionDenied()
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("사운드 마크 추가") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // "Select Song" Banner/Button
            OutlinedButton(
                onClick = {
                    showSongBottomSheet = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = MaterialTheme.shapes.medium,
                contentPadding = PaddingValues(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = uiState.selectedTrack?.title ?: "노래 선택",
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (uiState.selectedTrack != null) 
                                MaterialTheme.colorScheme.onSurface 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = if (uiState.selectedTrack != null) "변경" else "추가",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // "Select Place" Banner/Button
            OutlinedButton(
                onClick = {
                    showBottomSheet = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = MaterialTheme.shapes.medium,
                contentPadding = PaddingValues(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = uiState.selectedPlace?.placeName ?: "장소 선택",
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (uiState.selectedPlace != null) 
                                MaterialTheme.colorScheme.onSurface 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = if (uiState.selectedPlace != null) "변경" else "추가",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Other fields for adding SoundMark could go here
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "주변 추천 장소",
                    style = MaterialTheme.typography.titleMedium
                )
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.nearbyPlaces.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(uiState.nearbyPlaces.take(5)) { place ->
                        AssistChip(
                            onClick = { viewModel.onPlaceSelected(place) },
                            label = { Text(place.placeName ?: "Unknown") },
                            leadingIcon = {
                                if (uiState.selectedPlace == place) {
                                    Icon(
                                        Icons.Default.Check, 
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.LocationOn, 
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (uiState.selectedPlace == place) 
                                    MaterialTheme.colorScheme.primaryContainer 
                                else 
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            } else if (!uiState.isLoading) {
                Text(
                    text = "주변 장소를 찾을 수 없습니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            PlaceSelectionContent(
                uiState = uiState,
                onPlaceSelected = { place ->
                    viewModel.onPlaceSelected(place)
                    showBottomSheet = false
                },
                onRetry = { viewModel.fetchNearbyPlacesWithGPS() },
                onPermissionRetry = {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            )
        }
    }


    if (showSongBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSongBottomSheet = false },
            sheetState = songSheetState
        ) {
            SongSelectionContent(
                onTrackSelected = { track ->
                    viewModel.onTrackSelected(track)
                    showSongBottomSheet = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongSelectionContent(
    onTrackSelected: (Track) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val tracks = remember { MockDataSource.mockTracks }
    val listState = rememberLazyListState()

    // Precise connection to prevent sheet drag only when necessary
    val nestedScrollConnection = remember(listState) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val isAtTop = !listState.canScrollBackward
                return if (available.y > 0 && isAtTop) available else Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                val isAtTop = !listState.canScrollBackward
                return if (available.y > 0 && isAtTop) available else Velocity.Zero
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f)
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp)
    ) {
        Text(
            text = "노래 선택",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            placeholder = { Text("곡 제목, 아티스트 검색") },
            leadingIcon = { Icon(Icons.Default.PlayArrow, contentDescription = null) }, // Using PlayArrow for music context
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Check, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = MaterialTheme.shapes.medium
        )

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .nestedScroll(nestedScrollConnection),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            val filteredTracks = if (searchQuery.isEmpty()) tracks else {
                tracks.filter { 
                    it.title.contains(searchQuery, ignoreCase = true) || 
                    it.artist.contains(searchQuery, ignoreCase = true) 
                }
            }

            items(filteredTracks) { track ->
                ListItem(
                    headlineContent = { Text(track.title) },
                    supportingContent = { Text(track.artist) },
                    leadingContent = { Icon(Icons.Default.PlayArrow, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTrackSelected(track) }
                        .padding(vertical = 4.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceSelectionContent(
    uiState: AddUiState,
    onPlaceSelected: (GeoLocation) -> Unit,
    onRetry: () -> Unit,
    onPermissionRetry: () -> Unit
) {
    val listState = rememberLazyListState()

    // Intercept scroll/fling events so they don't reach the bottom sheet and trigger dismissal
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // When pulling down (y > 0) at the very top of the list,
                // we consume the delta here to prevent the sheet from starting to drag.
                val isAtTop = listState.firstVisibleItemIndex == 0 &&
                             listState.firstVisibleItemScrollOffset == 0
                return if (available.y > 0 && isAtTop) {
                    available
                } else {
                    Offset.Zero
                }
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                // If we have scroll delta available after the list has consumed its part (available.y != 0),
                // we consume it here to prevent the BottomSheet from moving/closing.
                return if (available.y > 0) available else Offset.Zero
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                // Consume all remaining downward fling velocity to prevent sheet dismissal
                return if (available.y > 0) available else Velocity.Zero
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f) // Set a stable height for the sheet content
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp)
    ) {
        Text(
            text = "장소 추가",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Text("현재 장소", style = MaterialTheme.typography.titleMedium)

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            onClick = { uiState.currentLocation?.let { onPlaceSelected(it) } }
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = uiState.currentLocation?.placeName ?: "장소를 찾는 중...",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("주변 장소", style = MaterialTheme.typography.titleMedium)

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.permissionDenied) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "위치 권한이 거부되었습니다.")
                Button(onClick = onPermissionRetry, modifier = Modifier.padding(top = 8.dp)) {
                    Text("권한 다시 요청")
                }
            }
        } else if (uiState.error != null) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "에러: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                Button(onClick = { onRetry() }, modifier = Modifier.padding(top = 8.dp)) {
                    Text("다시 시도")
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .nestedScroll(nestedScrollConnection), // Apply the nested scroll connection
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(uiState.nearbyPlaces) { place ->
                    ListItem(
                        headlineContent = { Text(place.placeName ?: "Unknown") },
                        supportingContent = { Text("${place.latitude}, ${place.longitude}") },
                        leadingContent = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .clickable { onPlaceSelected(place) }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}
