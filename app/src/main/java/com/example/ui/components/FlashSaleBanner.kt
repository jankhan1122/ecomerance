package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.model.Product
import com.example.ui.theme.*
import com.example.viewmodel.ShopViewModel
import java.text.NumberFormat
import java.util.*

@Composable
fun FlashSaleBanner(
    viewModel: ShopViewModel,
    onViewAllClick: () -> Unit,
    onProductClick: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    val secondsLeft by viewModel.flashSaleSeconds.collectAsState()
    val allProducts by viewModel.allProducts.collectAsState()
    val isUrdu by viewModel.isUrdu.collectAsState()

    val flashProducts = allProducts.filter { it.isFlashSale }

    val hours = secondsLeft / 3600
    val minutes = (secondsLeft % 3600) / 60
    val seconds = secondsLeft % 60

    val currencyFormat = NumberFormat.getNumberInstance(Locale.US)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.White)
            .padding(vertical = 12.dp)
            .testTag("flash_sale_section")
    ) {
        // Flash Sale Header with Countdown
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.FlashOn,
                    contentDescription = null,
                    tint = TaimoorOrange,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isUrdu) "فلیش سیل" else "FLASH SALE",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = TaimoorOrange,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.width(10.dp))

                // Timer boxes
                TimerBlock(text = "%02d".format(hours))
                Text(text = ":", fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.padding(horizontal = 2.dp))
                TimerBlock(text = "%02d".format(minutes))
                Text(text = ":", fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.padding(horizontal = 2.dp))
                TimerBlock(text = "%02d".format(seconds))
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { onViewAllClick() }
                    .padding(4.dp)
            ) {
                Text(
                    text = if (isUrdu) "مزید دیکھیں" else "SHOP MORE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TaimoorOrange
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = TaimoorOrange,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontal Flash Products
        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(flashProducts) { product ->
                FlashProductMiniCard(
                    product = product,
                    onClick = { onProductClick(product) }
                )
            }
        }
    }
}

@Composable
private fun TimerBlock(text: String) {
    Box(
        modifier = Modifier
            .size(width = 24.dp, height = 22.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(TaimoorNavy),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun FlashProductMiniCard(
    product: Product,
    onClick: () -> Unit
) {
    val currencyFormat = NumberFormat.getNumberInstance(Locale.US)

    Card(
        modifier = Modifier
            .width(135.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFB)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            // Visual
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White),
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
                    Icon(
                        imageVector = getCategoryIcon(product.category),
                        contentDescription = null,
                        tint = getCategoryColor(product.category),
                        modifier = Modifier.size(40.dp)
                    )
                }

                // Discount tag
                Surface(
                    color = TaimoorOrange,
                    shape = RoundedCornerShape(bottomEnd = 6.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = "-${product.discountPercent}%",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Title
            Text(
                text = product.title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Price
            Text(
                text = "Rs. ${currencyFormat.format(product.price.toLong())}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TaimoorOrange
            )

            Text(
                text = "Rs. ${currencyFormat.format(product.originalPrice.toLong())}",
                fontSize = 9.sp,
                color = TextMuted,
                textDecoration = TextDecoration.LineThrough
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Stock progress indicator
            LinearProgressIndicator(
                progress = { 0.75f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = TaimoorOrange,
                trackColor = Color(0xFFFFECE2),
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${product.stock} left in stock",
                fontSize = 8.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
