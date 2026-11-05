package com.example.ui.components

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TaimoorNavy
import com.example.ui.theme.TaimoorOrange
import com.example.viewmodel.Screen
import com.example.viewmodel.ShopViewModel

data class NavItem(
    val screen: Screen,
    val title: String,
    val urduTitle: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@Composable
fun TaimoorBottomNav(
    viewModel: ShopViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val wishlistIds by viewModel.wishlistProductIds.collectAsState()
    val isUrdu by viewModel.isUrdu.collectAsState()

    val totalCartCount = cartItems.sumOf { it.quantity }

    val navItems = listOf(
        NavItem(Screen.HOME, "Home", "ہوم", Icons.Filled.Home, Icons.Outlined.Home, "nav_home"),
        NavItem(Screen.PRODUCTS_CATALOG, "Categories", "اقسام", Icons.Filled.Category, Icons.Outlined.Category, "nav_categories"),
        NavItem(Screen.WISHLIST, "Wishlist", "خواہشات", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder, "nav_wishlist"),
        NavItem(Screen.CART, "Cart", "ٹوکری", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart, "nav_cart"),
        NavItem(Screen.PROFILE, "Account", "پروفائل", Icons.Filled.Person, Icons.Outlined.Person, "nav_profile")
    )

    NavigationBar(
        modifier = modifier
            .navigationBarsPadding()
            .testTag("taimoor_bottom_nav"),
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        navItems.forEach { item ->
            val isSelected = when (item.screen) {
                Screen.HOME -> currentScreen == Screen.HOME
                Screen.PRODUCTS_CATALOG -> currentScreen == Screen.PRODUCTS_CATALOG
                Screen.WISHLIST -> currentScreen == Screen.WISHLIST
                Screen.CART -> currentScreen == Screen.CART || currentScreen == Screen.CHECKOUT
                Screen.PROFILE -> currentScreen == Screen.PROFILE || currentScreen == Screen.MY_ORDERS || currentScreen == Screen.ORDER_TRACKING || currentScreen == Screen.ADMIN_DASHBOARD
                else -> false
            }

            NavigationBarItem(
                selected = isSelected,
                onClick = { viewModel.navigateTo(item.screen) },
                icon = {
                    if (item.screen == Screen.CART && totalCartCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = TaimoorOrange, contentColor = Color.White) {
                                    Text("$totalCartCount")
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        }
                    } else if (item.screen == Screen.WISHLIST && wishlistIds.isNotEmpty()) {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = TaimoorOrange, contentColor = Color.White) {
                                    Text("${wishlistIds.size}")
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        }
                    } else {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.title
                        )
                    }
                },
                label = {
                    Text(
                        text = if (isUrdu) item.urduTitle else item.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TaimoorOrange,
                    selectedTextColor = TaimoorOrange,
                    indicatorColor = TaimoorOrange.copy(alpha = 0.12f),
                    unselectedIconColor = Color(0xFF757575),
                    unselectedTextColor = Color(0xFF757575)
                ),
                modifier = Modifier.testTag(item.testTag)
            )
        }
    }
}
