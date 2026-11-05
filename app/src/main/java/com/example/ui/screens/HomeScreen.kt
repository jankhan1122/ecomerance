package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ProductCategory
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.Screen
import com.example.viewmodel.ShopViewModel

@Composable
fun HomeScreen(
    viewModel: ShopViewModel,
    modifier: Modifier = Modifier
) {
    val products by viewModel.allProducts.collectAsState()
    val wishlistIds by viewModel.wishlistProductIds.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val isUrdu by viewModel.isUrdu.collectAsState()

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .fillMaxSize()
            .background(NeutralLightBg)
            .testTag("home_screen_grid"),
        contentPadding = PaddingValues(bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Hero Promo Banner
        item(span = { GridItemSpan(2) }) {
            PromoBannerCarousel(
                onBannerClick = { viewModel.navigateTo(Screen.PRODUCTS_CATALOG) }
            )
        }

        // Fast Service Badges (Free Delivery, 100% Authentic, 7 Days Return, 24/7 Support)
        item(span = { GridItemSpan(2) }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FeatureBadge(icon = Icons.Default.Verified, title = "100% Authentic")
                    Divider(modifier = Modifier.height(20.dp).width(1.dp), color = Color(0xFFEEEEEE))
                    FeatureBadge(icon = Icons.Default.LocalShipping, title = "Free Delivery")
                    Divider(modifier = Modifier.height(20.dp).width(1.dp), color = Color(0xFFEEEEEE))
                    FeatureBadge(icon = Icons.Default.Autorenew, title = "7 Days Return")
                }
            }
        }

        // 10 Product Categories Row
        item(span = { GridItemSpan(2) }) {
            CategorySection(
                selectedCategory = selectedCategory,
                isUrdu = isUrdu,
                onCategorySelected = { category ->
                    viewModel.selectCategory(category)
                }
            )
        }

        // Flash Sale Section with Countdown
        item(span = { GridItemSpan(2) }) {
            FlashSaleBanner(
                viewModel = viewModel,
                onViewAllClick = { viewModel.navigateTo(Screen.PRODUCTS_CATALOG) },
                onProductClick = { product -> viewModel.openProductDetail(product) }
            )
        }

        // "Just For You" / Recommended Section Header
        item(span = { GridItemSpan(2) }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(18.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(TaimoorOrange)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isUrdu) "صرف آپ کے لیے" else "Just For You",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        color = TextPrimary
                    )
                }

                Text(
                    text = "${products.size} Products",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Recommended Products Grid (2 columns)
        items(products) { product ->
            Box(modifier = Modifier.padding(horizontal = 4.dp)) {
                ProductCard(
                    product = product,
                    isWishlisted = wishlistIds.contains(product.id),
                    onProductClick = { viewModel.openProductDetail(product) },
                    onAddToCartClick = { viewModel.addToCart(product) },
                    onWishlistToggle = { viewModel.toggleWishlist(product.id) }
                )
            }
        }
    }
}

@Composable
private fun FeatureBadge(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TaimoorOrange,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = title,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
    }
}
