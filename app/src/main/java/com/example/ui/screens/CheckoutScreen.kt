package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DeliveryAddress
import com.example.model.PaymentMethod
import com.example.ui.theme.*
import com.example.viewmodel.ShopViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: ShopViewModel,
    modifier: Modifier = Modifier
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val appliedCoupon by viewModel.appliedCoupon.collectAsState()
    val selectedPayment by viewModel.selectedPaymentMethod.collectAsState()
    val address by viewModel.deliveryAddress.collectAsState()

    var showAddressDialog by remember { mutableStateOf(false) }
    var onlineAccountInput by remember { mutableStateOf("03001234567") }
    var cardNumberInput by remember { mutableStateOf("4242 •••• •••• 4242") }
    var isPlacingOrder by remember { mutableStateOf(false) }

    val currencyFormat = NumberFormat.getNumberInstance(Locale.US)
    val selectedItems = cartItems.filter { it.isSelected }
    val subtotal = selectedItems.sumOf { it.totalPrice }
    val shippingFee = if (subtotal > 5000.0 || subtotal == 0.0) 0.0 else 250.0
    val discountAmount = if (appliedCoupon != null && subtotal >= appliedCoupon!!.minOrderAmount) {
        val calc = subtotal * (appliedCoupon!!.discountPercentage / 100.0)
        calc.coerceAtMost(appliedCoupon!!.maxDiscount)
    } else 0.0
    val total = (subtotal + shippingFee - discountAmount).coerceAtLeast(0.0)

    BackHandler {
        viewModel.navigateBack()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Checkout & Order", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 17.sp) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TaimoorNavy)
            )
        },
        bottomBar = {
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
                    Column {
                        Text("Total to Pay:", fontSize = 11.sp, color = TextSecondary)
                        Text(
                            text = "Rs. ${currencyFormat.format(total.toLong())}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TaimoorOrange
                        )
                    }

                    Button(
                        onClick = {
                            isPlacingOrder = true
                            viewModel.placeOrder()
                        },
                        enabled = !isPlacingOrder && selectedItems.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = TaimoorOrange),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("confirm_order_button")
                    ) {
                        if (isPlacingOrder) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Confirm Order", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(NeutralLightBg)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Delivery Address Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = TaimoorOrange, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Delivery Address", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                        }

                        TextButton(
                            onClick = { showAddressDialog = true },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Change / Edit", color = TaimoorOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(text = "${address.fullName}  |  ${address.phoneNumber}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "${address.streetAddress}, ${address.city}, ${address.province} - ${address.postalCode}", fontSize = 12.sp, color = TextSecondary)
                }
            }

            // Payment Methods Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Payment, contentDescription = null, tint = TaimoorNavy, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Select Payment Method", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    PaymentMethod.values().forEach { method ->
                        val isSelected = method == selectedPayment
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.setPaymentMethod(method) }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { viewModel.setPaymentMethod(method) },
                                colors = RadioButtonDefaults.colors(selectedColor = TaimoorOrange)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = method.title,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = TextPrimary
                                )
                                Text(
                                    text = method.subtitle,
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        if (isSelected && method == PaymentMethod.RAZORPAY) {
                            Box(modifier = Modifier.padding(start = 48.dp, bottom = 8.dp)) {
                                Surface(
                                    color = Color(0xFFF0F7FF),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "Razorpay Secure Gateway",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = Color(0xFF0C2340)
                                            )
                                            Surface(
                                                color = Color(0xFF0284C7),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = "RAZORPAY CONNECTED",
                                                    color = Color.White,
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Key: rzp_live_taimoor_shop948",
                                            fontSize = 10.sp,
                                            color = Color.Gray
                                        )
                                        Text(
                                            text = "✓ 100% Secure 256-Bit SSL Encryption\n✓ Instant Email & SMS confirmation to mmtkn0568@gmail.com",
                                            fontSize = 11.sp,
                                            color = TaimoorGreen,
                                            lineHeight = 15.sp
                                        )
                                    }
                                }
                            }
                        } else if (isSelected && (method == PaymentMethod.JAZZCASH || method == PaymentMethod.EASYPAISA)) {
                            // Sub-input for wallet phone number
                            Box(modifier = Modifier.padding(start = 48.dp, bottom = 8.dp)) {
                                OutlinedTextField(
                                    value = onlineAccountInput,
                                    onValueChange = { onlineAccountInput = it },
                                    label = { Text("${method.name} Mobile Number", fontSize = 11.sp) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().height(56.dp)
                                )
                            }
                        } else if (isSelected && method == PaymentMethod.CARD) {
                            Box(modifier = Modifier.padding(start = 48.dp, bottom = 8.dp)) {
                                Column {
                                    OutlinedTextField(
                                        value = cardNumberInput,
                                        onValueChange = { cardNumberInput = it },
                                        label = { Text("Card Number", fontSize = 11.sp) },
                                        singleLine = true,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth().height(56.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Protected by 256-bit SSL & 3D Secure OTP verification.",
                                        fontSize = 10.sp,
                                        color = TaimoorGreen
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Order Items Overview Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Order Package (${selectedItems.size} items)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    selectedItems.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${item.quantity}x  ${item.product.title.take(28)}...",
                                fontSize = 12.sp,
                                color = TextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "Rs. ${currencyFormat.format(item.totalPrice.toLong())}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TaimoorOrange
                            )
                        }
                    }
                }
            }

            // Summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Payment Summary", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    PriceRow(label = "Items Subtotal", value = "Rs. ${currencyFormat.format(subtotal.toLong())}")
                    PriceRow(label = "Delivery Fee", value = if (shippingFee == 0.0) "FREE" else "Rs. ${currencyFormat.format(shippingFee.toLong())}")
                    if (discountAmount > 0) {
                        PriceRow(label = "Voucher Savings", value = "-Rs. ${currencyFormat.format(discountAmount.toLong())}", valueColor = TaimoorGreen)
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFEEEEEE))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Amount", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Rs. ${currencyFormat.format(total.toLong())}", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = TaimoorOrange)
                    }
                }
            }
        }
    }

    // Edit Address Dialog
    if (showAddressDialog) {
        var name by remember { mutableStateOf(address.fullName) }
        var phone by remember { mutableStateOf(address.phoneNumber) }
        var street by remember { mutableStateOf(address.streetAddress) }
        var city by remember { mutableStateOf(address.city) }
        var postal by remember { mutableStateOf(address.postalCode) }

        AlertDialog(
            onDismissRequest = { showAddressDialog = false },
            title = { Text("Update Delivery Address", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, singleLine = true)
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") }, singleLine = true)
                    OutlinedTextField(value = street, onValueChange = { street = it }, label = { Text("Street Address / House #") })
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("City") }, modifier = Modifier.weight(1f), singleLine = true)
                        OutlinedTextField(value = postal, onValueChange = { postal = it }, label = { Text("Postal Code") }, modifier = Modifier.weight(1f), singleLine = true)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateAddress(
                            DeliveryAddress(
                                fullName = name,
                                phoneNumber = phone,
                                streetAddress = street,
                                city = city,
                                province = "Sindh",
                                postalCode = postal
                            )
                        )
                        showAddressDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TaimoorOrange)
                ) {
                    Text("Save Address")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddressDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
