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
import kotlinx.coroutines.delay
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
import coil.compose.AsyncImage
import com.example.soundmark.data.model.GeoLocation
import com.example.soundmark.data.model.Track
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    viewModel: AddViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showSongBottomSheet by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    
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

    LaunchedEffect(uiState.isPostSuccess) {
        if (uiState.isPostSuccess) {
            onBack()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("사운드 마크 추가") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = { viewModel.postSoundMark() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                enabled = uiState.selectedTrack != null && uiState.selectedPlace != null && !uiState.isPosting,
                shape = MaterialTheme.shapes.medium
            ) {
                if (uiState.isPosting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("사운드 마크 심기")
                }
            }
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

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.message,
                onValueChange = { viewModel.onMessageChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("메시지 (선택 사항)") },
                placeholder = { Text("이 노래에 대한 추억을 적어주세요.") },
                minLines = 3,
                maxLines = 5,
                shape = MaterialTheme.shapes.medium
            )
            
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

    if (showSongBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSongBottomSheet = false },
            sheetState = songSheetState,
            dragHandle = {
                BottomSheetDefaults.DragHandle(
                    modifier = Modifier.clickable { showSongBottomSheet = false }
                )
            }
        ) {
            SongSelectionContent(
                uiState = uiState,
                onSearch = { viewModel.onSearchQueryChanged(it) },
                onTrackSelected = { track ->
                    viewModel.onTrackSelected(track)
                    showSongBottomSheet = false
                }
            )
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            dragHandle = {
                BottomSheetDefaults.DragHandle(
                    modifier = Modifier.clickable { showBottomSheet = false }
                )
            }
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongSelectionContent(
    uiState: AddUiState,
    onSearch: (String) -> Unit,
    onTrackSelected: (Track) -> Unit
) {
    val listState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current

    val nestedScrollConnection = remember(listState) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                return if (available.y > 0 && !listState.canScrollBackward) available else Offset.Zero
            }
            override suspend fun onPreFling(available: Velocity): Velocity {
                return if (available.y > 0 && !listState.canScrollBackward) available else Velocity.Zero
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

        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { onSearch(it) },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            placeholder = { Text("곡 제목, 아티스트 검색") },
            trailingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearch("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                    if (uiState.isSearching) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                    } else {
                        IconButton(onClick = { 
                            onSearch(uiState.searchQuery)
                            keyboardController?.hide()
                        }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearch(uiState.searchQuery)
                    keyboardController?.hide()
                }
            ),
            singleLine = true,
            shape = MaterialTheme.shapes.medium
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth().weight(1f).nestedScroll(nestedScrollConnection),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            if (uiState.searchedTracks.isEmpty() && !uiState.isSearching && uiState.searchQuery.isNotEmpty()) {
                item {
                    Text(
                        text = "검색 결과가 없습니다.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            items(uiState.searchedTracks) { track ->
                ListItem(
                    headlineContent = { Text(track.title) },
                    supportingContent = { Text(track.artist, color = MaterialTheme.colorScheme.outline) },
                    leadingContent = { 
                        AsyncImage(
                            model = track.albumCoverUrl,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth().clickable { onTrackSelected(track) }.padding(vertical = 4.dp)
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

    val nestedScrollConnection = remember(listState) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                return if (available.y > 0 && !listState.canScrollBackward) available else Offset.Zero
            }
            override suspend fun onPreFling(available: Velocity): Velocity {
                return if (available.y > 0 && !listState.canScrollBackward) available else Velocity.Zero
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
            text = "장소 추가",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Text("현재 장소", style = MaterialTheme.typography.titleMedium)

        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            onClick = { uiState.currentLocation?.let { onPlaceSelected(it) } }
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = uiState.currentLocation?.placeName ?: "장소를 찾는 중...", style = MaterialTheme.typography.bodyLarge)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("주변 장소", style = MaterialTheme.typography.titleMedium)

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxWidth().weight(1f).nestedScroll(nestedScrollConnection),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(uiState.nearbyPlaces) { place ->
                    ListItem(
                        headlineContent = { Text(place.placeName ?: "Unknown") },
                        supportingContent = { Text(place.address ?: "${place.latitude}, ${place.longitude}") },
                        leadingContent = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().clickable { onPlaceSelected(place) }.padding(vertical = 4.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}
