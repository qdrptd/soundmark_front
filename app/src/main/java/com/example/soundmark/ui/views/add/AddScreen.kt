package com.example.soundmark.ui.views.add

import android.Manifest
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
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
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import com.example.soundmark.R
import com.example.soundmark.ui.theme.BackgroundDark
import com.example.soundmark.ui.theme.BackgroundDarker
import com.example.soundmark.ui.theme.SurfaceVariantDark
import com.example.soundmark.util.bold


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
        containerColor = BackgroundDarker,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDarker,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
                title = { Text("사운드 마크 추가", style = MaterialTheme.typography.titleLarge.bold()) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Box(modifier = Modifier.background(color = BackgroundDarker)) {
                Button(
                    onClick = { viewModel.postSoundMark() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    enabled = uiState.selectedTrack != null && uiState.selectedPlace != null && !uiState.isPosting,
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    if (uiState.isPosting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("사운드 마크 심기", style = MaterialTheme.typography.titleMedium.bold())
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 노래 선택 섹션
            SelectionItem(
                label = uiState.selectedTrack?.title ?: "노래 선택",
                isSelected = uiState.selectedTrack != null,
                icon = ImageVector.vectorResource(R.drawable.ic_music),
                onClick = { showSongBottomSheet = true }
            )
            
            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // 장소 선택 섹션
            SelectionItem(
                label = uiState.selectedPlace?.placeName ?: "장소 선택",
                isSelected = uiState.selectedPlace != null,
                icon = ImageVector.vectorResource(R.drawable.ic_location),
                onClick = { showBottomSheet = true }
            )

            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            val nearbyListState = rememberLazyListState()

            val displayPlaces = remember(uiState.nearbyPlaces, uiState.selectedPlace) {
                val list = uiState.nearbyPlaces.toMutableList()
                uiState.selectedPlace?.let { selected ->
                    list.removeAll { it.placeId == selected.placeId || (it.latitude == selected.latitude && it.longitude == selected.longitude) }
                    list.add(0, selected)
                }
                list.take(6)
            }

            LaunchedEffect(uiState.selectedPlace) {
                if (displayPlaces.isNotEmpty()) {
                    nearbyListState.animateScrollToItem(0)
                }
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                if (displayPlaces.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "주변 추천 장소",
                            style = MaterialTheme.typography.titleMedium.bold()
                        )
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    }

                    LazyRow(
                        state = nearbyListState,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 0.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(displayPlaces) { place ->
                            val isSelected = uiState.selectedPlace == place
                            val distance = uiState.currentLocation?.let { current ->
                                val results = FloatArray(1)
                                Location.distanceBetween(
                                    current.latitude, current.longitude,
                                    place.latitude, place.longitude,
                                    results
                                )
                                val dist = results[0]
                                if (dist >= 1000) "%.1fkm".format(dist / 1000f) else "${dist.toInt()}m"
                            }

                            Surface(
                                onClick = { viewModel.onPlaceSelected(place) },
                                shape = MaterialTheme.shapes.medium,
                                color = if (isSelected) 
                                    androidx.compose.ui.graphics.Color(0xFF01FF9C).copy(alpha = 0.2f)
                                else 
                                    SurfaceVariantDark,
                                border = androidx.compose.foundation.BorderStroke(
                                    width = 1.dp,
                                    color = if (isSelected) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                ),
                                modifier = Modifier.wrapContentWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text(
                                        text = place.placeName ?: "Unknown",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = if (isSelected) 
                                            MaterialTheme.colorScheme.onPrimaryContainer 
                                        else 
                                            MaterialTheme.colorScheme.onSurface
                                    )
                                    distance?.let {
                                        Text(
                                            text = it,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else if (!uiState.isLoading) {
                    Text(
                        text = "주변 장소를 찾을 수 없습니다.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                val keyboardController = LocalSoftwareKeyboardController.current
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("한줄 코멘트", style = MaterialTheme.typography.titleMedium.bold())

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "${uiState.message.length}/100",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                OutlinedTextField(
                    value = uiState.message,
                    onValueChange = { 
                        if (it.length <= 100) {
                            viewModel.onMessageChanged(it)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("이 순간의 느낌을 남겨보세요..", style = MaterialTheme.typography.bodyMedium) },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { keyboardController?.hide() }
                    ),
                    minLines = 3,
                    maxLines = 5,
                    shape = MaterialTheme.shapes.medium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceVariantDark,
                        unfocusedContainerColor = SurfaceVariantDark,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                )
            }
        }
    }

    if (showSongBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSongBottomSheet = false },
            sheetState = songSheetState,
            containerColor = BackgroundDarker,
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
            containerColor = BackgroundDarker,
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

@Composable
fun SelectionItem(
    label: String,
    isSelected: Boolean,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = BackgroundDarker
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
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
            style = MaterialTheme.typography.titleLarge.bold(),
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
            shape = MaterialTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceVariantDark,
                unfocusedContainerColor = SurfaceVariantDark
            )
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
                    headlineContent = { Text(track.title, style = MaterialTheme.typography.bodyLarge.bold()) },
                    supportingContent = { Text(track.artist, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline) },
                    leadingContent = { 
                        AsyncImage(
                            model = track.albumCoverUrl,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                    },
                    colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent),
                    modifier = Modifier.fillMaxWidth().clickable { onTrackSelected(track) }
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
            style = MaterialTheme.typography.titleLarge.bold(),
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Text("현재 장소", style = MaterialTheme.typography.titleMedium.bold())

        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark),
            onClick = { uiState.currentLocation?.let { onPlaceSelected(it) } }
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = uiState.currentLocation?.placeName ?: "위치를 선택하세요", style = MaterialTheme.typography.bodyLarge.bold())
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("주변 장소", style = MaterialTheme.typography.titleMedium.bold())

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
                    val distance = uiState.currentLocation?.let { current ->
                        val results = FloatArray(1)
                        Location.distanceBetween(
                            current.latitude, current.longitude,
                            place.latitude, place.longitude,
                            results
                        )
                        val dist = results[0]
                        if (dist >= 1000) "%.1fkm".format(dist / 1000f) else "${dist.toInt()}m"
                    }

                    ListItem(
                        headlineContent = { Text(place.placeName ?: "Unknown", style = MaterialTheme.typography.bodyLarge.bold()) },
                        supportingContent = { Text(place.address ?: "${place.latitude}, ${place.longitude}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline) },
                        leadingContent = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                        trailingContent = {
                            distance?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        },
                        colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent),
                        modifier = Modifier.fillMaxWidth().clickable { onPlaceSelected(place) }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}
