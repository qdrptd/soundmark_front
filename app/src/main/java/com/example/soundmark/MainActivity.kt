package com.example.soundmark

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.soundmark.ui.theme.SoundMarkTheme
import com.example.soundmark.ui.views.home.HomeScreen
import com.example.soundmark.ui.views.onboard.LoginState
import com.example.soundmark.ui.views.onboard.OnboardScreen
import com.example.soundmark.ui.views.onboard.OnboardViewModel
import com.example.soundmark.ui.views.profile.ProfileScreen
import com.example.soundmark.ui.views.songlist.SongListScreen

class MainActivity : ComponentActivity() {
    private val onboardViewModel: OnboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleIntent(intent)
        setContent {
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

    val bottomBarDestinations = NavigationDestination.entries.filter { it.showInBottomBar }
    val showBottomBar = bottomBarDestinations.any { it.name == currentDestination?.route }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Show bottom bar only for main destinations
            if (showBottomBar) {
                NavigationBar {
                    bottomBarDestinations.forEach { screen ->
                        NavigationBarItem(
                            icon = {
                                screen.icon?.let {
                                    Icon(it, contentDescription = screen.label)
                                }
                            },
                            label = { Text(screen.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.name } == true,
                            onClick = {
                                navController.navigate(screen.name) {
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
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavigationDestination.HOME.name, //NavigationDestination.ONBOARD.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavigationDestination.ONBOARD.name) {
                OnboardScreen(viewModel = onboardViewModel)
            }
            composable(NavigationDestination.HOME.name) {
                HomeScreen()
            }
            composable(NavigationDestination.SONG_LIST.name) {
                SongListScreen()
            }
            composable(NavigationDestination.PROFILE.name) {
                ProfileScreen()
            }
        }
    }

    // Effect to handle navigation when login state changes
    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            navController.navigate(NavigationDestination.HOME.name) {
                popUpTo(NavigationDestination.ONBOARD.name) { inclusive = true }
            }
        }
    }
}
