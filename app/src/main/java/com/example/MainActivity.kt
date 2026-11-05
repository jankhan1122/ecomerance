package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.ui.components.TaimoorBottomNav
import com.example.ui.components.TaimoorTopBar
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.Screen
import com.example.viewmodel.ShopViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ShopViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                TaimoorShopApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun TaimoorShopApp(viewModel: ShopViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val notification by viewModel.notification.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(notification) {
        notification?.let {
            snackbarHostState.showSnackbar(
                message = it.message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearNotification()
        }
    }

    BackHandler(enabled = currentScreen != Screen.HOME) {
        if (!viewModel.navigateBack()) {
            viewModel.navigateTo(Screen.HOME)
        }
    }

    // Determine if current screen uses primary chrome (TopBar + BottomNav)
    val showMainChrome = currentScreen in listOf(
        Screen.HOME,
        Screen.PRODUCTS_CATALOG,
        Screen.CART,
        Screen.WISHLIST,
        Screen.PROFILE
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("main_app_scaffold"),
        topBar = {
            if (showMainChrome) {
                TaimoorTopBar(viewModel = viewModel)
            }
        },
        bottomBar = {
            if (showMainChrome) {
                TaimoorBottomNav(viewModel = viewModel)
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { targetScreen ->
                when (targetScreen) {
                    Screen.HOME -> HomeScreen(viewModel = viewModel)
                    Screen.PRODUCTS_CATALOG -> ProductsScreen(viewModel = viewModel)
                    Screen.PRODUCT_DETAIL -> ProductDetailScreen(viewModel = viewModel)
                    Screen.CART -> CartScreen(viewModel = viewModel)
                    Screen.CHECKOUT -> CheckoutScreen(viewModel = viewModel)
                    Screen.ORDER_SUCCESS -> OrderSuccessScreen(viewModel = viewModel)
                    Screen.MY_ORDERS -> OrdersScreen(viewModel = viewModel)
                    Screen.ORDER_TRACKING -> OrderTrackingScreen(viewModel = viewModel)
                    Screen.WISHLIST -> WishlistScreen(viewModel = viewModel)
                    Screen.PROFILE -> ProfileScreen(viewModel = viewModel)
                    Screen.ADMIN_DASHBOARD -> AdminDashboardScreen(viewModel = viewModel)
                    Screen.AUTH -> HomeScreen(viewModel = viewModel)
                }
            }
        }
    }
}
