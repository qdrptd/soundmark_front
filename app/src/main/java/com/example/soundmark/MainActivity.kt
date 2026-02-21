package com.example.soundmark

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.soundmark.ui.theme.SoundMarkTheme
import com.example.soundmark.ui.views.add.AddScreen
import dagger.hilt.android.AndroidEntryPoint
import com.example.soundmark.ui.views.home.HomeScreen
import com.example.soundmark.ui.views.home.HomeViewModel
import com.example.soundmark.ui.views.musicDetail.MusicDetailScreen
import com.example.soundmark.ui.views.musicDetail.MusicDetailViewModel
import com.example.soundmark.ui.views.onboard.LoginState
import com.example.soundmark.ui.views.onboard.OnboardScreen
import com.example.soundmark.ui.views.onboard.OnboardViewModel
import com.example.soundmark.ui.views.profile.ProfileRoute
import com.example.soundmark.ui.views.songlist.SongListScreen

private object NoIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): Modifier.Node {
        return object : Modifier.Node() {}
    }

    override fun hashCode(): Int = System.identityHashCode(this)
    override fun equals(other: Any?): Boolean = other === this
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val onboardViewModel: OnboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleIntent(intent)
        setContent {
            val locationPermissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                if (!granted) {
                    finish() // 권한 거부 시 앱 종료
                }
            }

            LaunchedEffect(Unit) {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }

            SoundMarkTheme {
                SoundMarkApp(onboardViewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        intent?.data?.let { uri ->
            onboardViewModel.handleAuthRedirect(uri)
        }
    }
}

@Composable
fun SoundMarkApp(onboardViewModel: OnboardViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Collect login state from ViewModel
    val loginState by onboardViewModel.loginState.collectAsState()

    val startDestination = if (loginState is LoginState.Success) {
        NavigationDestination.HOME.name
    } else {
        NavigationDestination.ONBOARD.name
    }

    val bottomBarDestinations = NavigationDestination.entries.filter { it.showInBottomBar }
    val showBottomBar = bottomBarDestinations.any { it.route == currentDestination?.route }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                // Show bottom bar with animation to prevent layout jumps
                AnimatedVisibility(
                    visible = showBottomBar,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column {
                        HorizontalDivider(
                            color = androidx.compose.ui.graphics.Color.Black,
                            thickness = 1.dp
                        )
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.onBackground,
                            tonalElevation = 0.dp
                        ) {
                            CompositionLocalProvider(LocalIndication provides NoIndication) {
                                bottomBarDestinations.forEach { screen ->
                                    NavigationBarItem(
                                        icon = {
                                            if (screen.icon != null) {
                                                Icon(screen.icon, contentDescription = screen.label)
                                            } else if (screen.iconResId != null) {
                                                Icon(
                                                    painter = painterResource(screen.iconResId),
                                                    contentDescription = screen.label
                                                )
                                            }
                                        },
                                        label = { Text(screen.label) },
                                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = MaterialTheme.colorScheme.primary,
                                            selectedTextColor = MaterialTheme.colorScheme.primary,
                                            unselectedIconColor = MaterialTheme.colorScheme.outline,
                                            unselectedTextColor = MaterialTheme.colorScheme.outline,
                                            indicatorColor = androidx.compose.ui.graphics.Color.Transparent
                                        ),
                                        onClick = {
                                            val targetRoute = if (screen == NavigationDestination.PROFILE) {
                                                "${NavigationDestination.PROFILE.name}/me"
                                            } else {
                                                screen.name
                                            }
                                            navController.navigate(targetRoute) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            composable(NavigationDestination.ONBOARD.name) {
                OnboardScreen(viewModel = onboardViewModel)
            }

            composable(NavigationDestination.HOME.name) {
                val homeViewModel: HomeViewModel = hiltViewModel()
                HomeScreen(viewModel = homeViewModel,
                    onNavigateToSongDetail = { soundMarkId ->
                        navController.navigate("${NavigationDestination.SONG_DETAIL.name}/$soundMarkId")
                    },
                    onNavigateToAdd = { navController.navigate(NavigationDestination.SONG_ADD.name) }
                )
            }

            dialog(
                route = NavigationDestination.SONG_DETAIL.route,
                arguments = listOf(navArgument("soundMarkId") { type = NavType.StringType }),
                dialogProperties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true,
                    usePlatformDefaultWidth = false // 가로 길이를 시스템 기본값이 아닌 커스텀하게 사용 가능
                )
            ) { backStackEntry ->
                val soundMarkId = backStackEntry.arguments?.getString("soundMarkId") ?: ""
                val detailViewModel: MusicDetailViewModel = hiltViewModel()

                MusicDetailScreen(
                    soundMarkId = soundMarkId,
                    viewModel = detailViewModel,
                    onBackClick = { navController.popBackStack() },
                    onProfileClick = { userId -> /* TODO: 프로필 이동 로직 */ }
                )
            }

            composable(NavigationDestination.SONG_LIST.name) {
                SongListScreen()
            }
            composable(NavigationDestination.SONG_ADD.name) {
                AddScreen(onBack = { navController.popBackStack() })
            }
            composable(
                route = NavigationDestination.PROFILE.route,
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: "me"
                ProfileRoute(userId = userId)
            }
        }
    }

    // Effect to handle navigation when login state changes
    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            navController.navigate(NavigationDestination.HOME.name) {
                popUpTo(NavigationDestination.ONBOARD.name) { inclusive = true }
            }
        } else if (loginState is LoginState.Idle) {
            // Handle session expiration/logout: navigate back to Onboard
            if (currentDestination?.route != NavigationDestination.ONBOARD.name) {
                navController.navigate(NavigationDestination.ONBOARD.name) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }
}
