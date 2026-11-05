package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TaimoorNavy
import com.example.ui.theme.TaimoorOrange
import com.example.viewmodel.Screen
import com.example.viewmodel.ShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaimoorTopBar(
    viewModel: ShopViewModel,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val wishlistIds by viewModel.wishlistProductIds.collectAsState()
    val isAdminMode by viewModel.isAdminMode.collectAsState()
    val isUrdu by viewModel.isUrdu.collectAsState()
    val focusManager = LocalFocusManager.current

    val totalCartCount = cartItems.sumOf { it.quantity }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = TaimoorNavy,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // Top Row: Brand Logo, Language Toggle, Wishlist, Cart
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Logo & Brand
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { viewModel.navigateTo(Screen.HOME) }
                        .padding(vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(TaimoorOrange),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = "Logo",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (isUrdu) "تیمور شاپ" else "TAIMOOR",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = if (isUrdu) "آن لائن شاپنگ" else "ONLINE SHOPPING",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 9.sp,
                            color = TaimoorOrange,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Action Icons (Urdu toggle, Wishlist, Cart, Admin toggle)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Language Switcher
                    AssistChip(
                        onClick = { viewModel.toggleUrduLanguage() },
                        label = {
                            Text(
                                text = if (isUrdu) "English" else "اردو",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = Color.White.copy(alpha = 0.15f)
                        ),
                        border = null,
                        modifier = Modifier
                            .height(30.dp)
                            .padding(end = 4.dp)
                    )

                    // Wishlist Icon with count
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.WISHLIST) },
                        modifier = Modifier.testTag("topbar_wishlist_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (wishlistIds.isNotEmpty()) {
                                    Badge(
                                        containerColor = TaimoorOrange,
                                        contentColor = Color.White
                                    ) {
                                        Text("${wishlistIds.size}")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.FavoriteBorder,
                                contentDescription = "Wishlist",
                                tint = Color.White
                            )
                        }
                    }

                    // Cart Icon with count
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.CART) },
                        modifier = Modifier.testTag("topbar_cart_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (totalCartCount > 0) {
                                    Badge(
                                        containerColor = TaimoorOrange,
                                        contentColor = Color.White
                                    ) {
                                        Text("$totalCartCount")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ShoppingCart,
                                contentDescription = "Cart",
                                tint = Color.White
                            )
                        }
                    }

                    // Admin Portal Button
                    IconButton(
                        onClick = { viewModel.toggleAdminMode() },
                        modifier = Modifier.testTag("topbar_admin_toggle")
                    ) {
                        Icon(
                            imageVector = if (isAdminMode) Icons.Filled.Storefront else Icons.Outlined.AdminPanelSettings,
                            contentDescription = "Admin Mode",
                            tint = if (isAdminMode) TaimoorOrange else Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Search Bar (Amazon / Daraz style)
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("search_bar_input"),
                placeholder = {
                    Text(
                        text = if (isUrdu) "لاکھوں مصنوعات میں تلاش کریں..." else "Search 100,000+ products on Taimoor...",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TaimoorOrange
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search",
                                tint = Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = TaimoorOrange,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    focusManager.clearFocus()
                    if (viewModel.currentScreen.value != Screen.PRODUCTS_CATALOG) {
                        viewModel.navigateTo(Screen.PRODUCTS_CATALOG)
                    }
                })
            )
        }
    }
}
