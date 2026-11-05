package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.model.Product
import com.example.model.ProductCategory
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.*

@Composable
fun ProductCard(
    product: Product,
    isWishlisted: Boolean,
    onProductClick: () -> Unit,
    onAddToCartClick: () -> Unit,
    onWishlistToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormat = NumberFormat.getNumberInstance(Locale.US)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onProductClick() }
            .testTag("product_card_${product.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Product Visual Container with Badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(148.dp)
                    .background(Color(0xFFF7F8FA)),
                contentAlignment = Alignment.Center
            ) {
                val imageSource: Any? = product.imageDrawableRes ?: product.imageUrl.takeIf { it.isNotBlank() }

                if (imageSource != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageSource)
                            .crossfade(true)
                            .build(),
                        contentDescription = product.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Category-specific visual icon & design element
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(getCategoryColor(product.category).copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getCategoryIcon(product.category),
                            contentDescription = product.title,
                            modifier = Modifier.size(40.dp),
                            tint = getCategoryColor(product.category)
                        )
                    }
                }

                // Top Left Badges: Mall & Discount
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                ) {
                    if (product.isMall) {
                        Surface(
                            color = TaimoorNavy,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            Text(
                                text = "MALL",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (product.discountPercent > 0) {
                        Surface(
                            color = TaimoorOrange,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "-${product.discountPercent}%",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Top Right Wishlist Heart Button
                IconButton(
                    onClick = onWishlistToggle,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.85f))
                ) {
                    Icon(
                        imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Wishlist",
                        tint = if (isWishlisted) TaimoorRed else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Free Delivery Tag on Bottom
                if (product.freeDelivery) {
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(topEnd = 6.dp),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalShipping,
                                contentDescription = null,
                                tint = TaimoorGreen,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Free Delivery",
                                fontSize = 9.sp,
                                color = TaimoorGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Info Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                // Brand / Category Tag
                Text(
                    text = product.brand,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Title
                Text(
                    text = product.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Star Rating & Sold Count
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = TaimoorGold,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "%.1f".format(product.rating),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "(${product.soldCount} sold)",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Price Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Rs. ${currencyFormat.format(product.price.toLong())}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TaimoorOrange
                        )
                        if (product.originalPrice > product.price) {
                            Text(
                                text = "Rs. ${currencyFormat.format(product.originalPrice.toLong())}",
                                fontSize = 10.sp,
                                color = TextMuted,
                                textDecoration = TextDecoration.LineThrough
                            )
                        }
                    }

                    // Add to Cart Button
                    FilledIconButton(
                        onClick = onAddToCartClick,
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = TaimoorOrange),
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("add_to_cart_btn_${product.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddShoppingCart,
                            contentDescription = "Add to Cart",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

fun getCategoryIcon(category: ProductCategory): ImageVector {
    return when (category) {
        ProductCategory.MOBILE -> Icons.Default.PhoneAndroid
        ProductCategory.LAPTOPS -> Icons.Default.Laptop
        ProductCategory.MENS_FASHION -> Icons.Default.Checkroom
        ProductCategory.WOMENS_FASHION -> Icons.Default.DryCleaning
        ProductCategory.SHOES_WATCHES -> Icons.Default.Watch
        ProductCategory.ELECTRONICS -> Icons.Default.Headphones
        ProductCategory.HOME_KITCHEN -> Icons.Default.Kitchen
        ProductCategory.BEAUTY_HEALTH -> Icons.Default.Spa
        ProductCategory.SPORTS_FITNESS -> Icons.Default.FitnessCenter
        ProductCategory.GROCERY -> Icons.Default.LocalGroceryStore
        ProductCategory.ALL -> Icons.Default.Apps
    }
}

fun getCategoryColor(category: ProductCategory): Color {
    return when (category) {
        ProductCategory.MOBILE -> Color(0xFF0288D1)
        ProductCategory.LAPTOPS -> Color(0xFF5E35B1)
        ProductCategory.MENS_FASHION -> Color(0xFF3949AB)
        ProductCategory.WOMENS_FASHION -> Color(0xFFD81B60)
        ProductCategory.SHOES_WATCHES -> Color(0xFFF57C00)
        ProductCategory.ELECTRONICS -> Color(0xFF00897B)
        ProductCategory.HOME_KITCHEN -> Color(0xFF6D4C41)
        ProductCategory.BEAUTY_HEALTH -> Color(0xFFE91E63)
        ProductCategory.SPORTS_FITNESS -> Color(0xFF43A047)
        ProductCategory.GROCERY -> Color(0xFF7CB342)
        ProductCategory.ALL -> TaimoorOrange
    }
}
