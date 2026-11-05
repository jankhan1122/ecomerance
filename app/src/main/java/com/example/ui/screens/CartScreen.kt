package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.CartItem
import com.example.ui.components.getCategoryColor
import com.example.ui.components.getCategoryIcon
import com.example.ui.theme.*
import com.example.viewmodel.Screen
import com.example.viewmodel.ShopViewModel
import java.text.NumberFormat
import java.util.*

@Composable
fun CartScreen(
    viewModel: ShopViewModel,
    modifier: Modifier = Modifier
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val appliedCoupon by viewModel.appliedCoupon.collectAsState()
    val isUrdu by viewModel.isUrdu.collectAsState()

    var couponInput by remember { mutableStateOf("") }
    val currencyFormat = NumberFormat.getNumberInstance(Locale.US)

    val selectedItems = cartItems.filter { it.isSelected }
    val subtotal = selectedItems.sumOf { it.totalPrice }
    val shippingFee = if (subtotal > 5000.0 || subtotal == 0.0) 0.0 else 250.0
    val discountAmount = if (appliedCoupon != null && subtotal >= appliedCoupon!!.minOrderAmount) {
        val calc = subtotal * (appliedCoupon!!.discountPercentage / 100.0)
        calc.coerceAtMost(appliedCoupon!!.maxDiscount)
    } else 0.0
    val total = (subtotal + shippingFee - discountAmount).coerceAtLeast(0.0)

    val allSelected = cartItems.isNotEmpty() && cartItems.all { it.isSelected }

    if (cartItems.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(NeutralLightBg)
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(TaimoorOrangeContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(52.dp),
                        tint = TaimoorOrange
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = if (isUrdu) "آپ کی ٹوکری خالی ہے" else "Your Shopping Cart is Empty",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isUrdu) "شاندار رعایتوں کے ساتھ خریداری شروع کریں!" else "Explore top deals and flash sales from certified sellers!",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.navigateTo(Screen.HOME) },
                    colors = ButtonDefaults.buttonColors(containerColor = TaimoorOrange),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(46.dp)
                ) {
                    Text("Start Shopping Now", fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            // Cart Checkout Footer
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = Color.White,
                shadowElevation = 12.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = allSelected,
                            onCheckedChange = { viewModel.toggleSelectAll(it) },
                            colors = CheckboxDefaults.colors(checkedColor = TaimoorOrange)
                        )
                        Text("All", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Total: Rs. ${currencyFormat.format(total.toLong())}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TaimoorOrange
                        )
                        if (discountAmount > 0) {
                            Text(
                                text = "Saved: Rs. ${currencyFormat.format(discountAmount.toLong())}",
                                fontSize = 10.sp,
                                color = TaimoorGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.navigateTo(Screen.CHECKOUT) },
                        enabled = selectedItems.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = TaimoorOrange),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(46.dp)
                            .testTag("cart_checkout_button")
                    ) {
                        Text(
                            text = "Check Out (${selectedItems.sumOf { it.quantity }})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(NeutralLightBg)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Info
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Shopping Cart (${cartItems.size} items)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )

                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (subtotal > 5000.0) "✓ Free Delivery Unlocked" else "Add Rs. ${(5000.0 - subtotal).toInt()} for Free Delivery",
                            color = TaimoorGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Cart Items List
            items(cartItems) { item ->
                CartItemRow(
                    item = item,
                    onToggleSelect = { viewModel.toggleCartItemSelection(item.product.id, item.selectedColor) },
                    onIncrease = { viewModel.updateCartQuantity(item.product.id, item.selectedColor, item.quantity + 1) },
                    onDecrease = { viewModel.updateCartQuantity(item.product.id, item.selectedColor, item.quantity - 1) },
                    onDelete = { viewModel.removeCartItem(item.product.id, item.selectedColor) },
                    onClick = { viewModel.openProductDetail(item.product) }
                )
            }

            // Voucher Coupon Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Store Voucher / Discount Code",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = couponInput,
                                onValueChange = { couponInput = it.uppercase() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("voucher_input_field"),
                                placeholder = { Text("e.g. TAIMOOR50", fontSize = 12.sp) },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp)
                            )

                            Button(
                                onClick = {
                                    if (couponInput.isNotBlank()) {
                                        viewModel.applyCoupon(couponInput)
                                        couponInput = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TaimoorNavy),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(48.dp)
                            ) {
                                Text("Apply")
                            }
                        }

                        // Applied Coupon Chip
                        if (appliedCoupon != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = TaimoorOrangeContainer,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = TaimoorOrange, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "${appliedCoupon!!.code}: ${appliedCoupon!!.discountPercentage}% OFF applied",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TaimoorOrangeDark
                                        )
                                    }

                                    IconButton(
                                        onClick = { viewModel.removeCoupon() },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Remove", tint = TaimoorOrangeDark, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        } else {
                            // Quick Suggestion Chips
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                SuggestionChip(
                                    onClick = { viewModel.applyCoupon("TAIMOOR50") },
                                    label = { Text("Use TAIMOOR50 (50% Off)", fontSize = 11.sp) }
                                )
                                SuggestionChip(
                                    onClick = { viewModel.applyCoupon("FREESHIP") },
                                    label = { Text("FREESHIP", fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                }
            }

            // Order Price Breakdown Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Order Summary",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        PriceRow(label = "Subtotal (${selectedItems.sumOf { it.quantity }} items)", value = "Rs. ${currencyFormat.format(subtotal.toLong())}")
                        PriceRow(
                            label = "Shipping Fee",
                            value = if (shippingFee == 0.0) "FREE" else "Rs. ${currencyFormat.format(shippingFee.toLong())}",
                            valueColor = if (shippingFee == 0.0) TaimoorGreen else TextPrimary
                        )

                        if (discountAmount > 0) {
                            PriceRow(
                                label = "Voucher Discount",
                                value = "-Rs. ${currencyFormat.format(discountAmount.toLong())}",
                                valueColor = TaimoorGreen
                            )
                        }

                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFEEEEEE))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total Amount", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                            Text("Rs. ${currencyFormat.format(total.toLong())}", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = TaimoorOrange)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItem,
    onToggleSelect: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val currencyFormat = NumberFormat.getNumberInstance(Locale.US)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Selection Checkbox
            Checkbox(
                checked = item.isSelected,
                onCheckedChange = { onToggleSelect() },
                colors = CheckboxDefaults.colors(checkedColor = TaimoorOrange)
            )

            // Product Visual
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF7F8FA))
                    .clickable { onClick() },
                contentAlignment = Alignment.Center
            ) {
                val imageSource: Any? = item.product.imageDrawableRes ?: item.product.imageUrl.takeIf { it.isNotBlank() }

                if (imageSource != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageSource)
                            .crossfade(true)
                            .build(),
                        contentDescription = item.product.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = getCategoryIcon(item.product.category),
                        contentDescription = null,
                        modifier = Modifier.size(34.dp),
                        tint = getCategoryColor(item.product.category)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.product.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    color = TextPrimary,
                    lineHeight = 16.sp,
                    modifier = Modifier.clickable { onClick() }
                )

                if (item.selectedColor != "Default") {
                    Text(
                        text = "Variation: ${item.selectedColor}",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rs. ${currencyFormat.format(item.product.price.toLong())}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TaimoorOrange
                    )

                    // Stepper (- / +) and Delete
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedIconButton(
                            onClick = onDecrease,
                            modifier = Modifier.size(28.dp),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(12.dp))
                        }

                        Text(
                            text = "${item.quantity}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        OutlinedIconButton(
                            onClick = onIncrease,
                            modifier = Modifier.size(28.dp),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(12.dp))
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Delete",
                                tint = Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PriceRow(
    label: String,
    value: String,
    valueColor: Color = TextPrimary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.sp, color = TextSecondary)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}
