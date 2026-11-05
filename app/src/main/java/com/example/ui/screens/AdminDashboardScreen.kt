package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.SampleData
import com.example.model.*
import com.example.ui.components.getCategoryColor
import com.example.ui.components.getCategoryIcon
import com.example.ui.theme.*
import com.example.viewmodel.Screen
import com.example.viewmodel.ShopViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: ShopViewModel,
    modifier: Modifier = Modifier
) {
    val products by viewModel.allProducts.collectAsState()
    val orders by viewModel.orders.collectAsState()

    var selectedAdminTab by remember { mutableStateOf(0) }
    var showAddProductDialog by remember { mutableStateOf(false) }
    val currencyFormat = NumberFormat.getNumberInstance(Locale.US)

    val adminTabs = listOf("Products", "Orders", "Coupons", "Sales Reports")

    BackHandler {
        viewModel.navigateTo(Screen.HOME)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Admin Control Portal", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                        Text("TAIMOOR STORE OPERATIONS", color = TaimoorOrange, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.HOME) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.navigateTo(Screen.HOME) }) {
                        Text("View Store", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TaimoorNavy)
            )
        },
        floatingActionButton = {
            if (selectedAdminTab == 0) {
                FloatingActionButton(
                    onClick = { showAddProductDialog = true },
                    containerColor = TaimoorOrange,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("admin_add_product_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Product")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(NeutralLightBg)
        ) {
            // Analytics KPI Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminMetricCard(
                    title = "Revenue",
                    value = "Rs. 1.48M",
                    sub = "+18% this wk",
                    modifier = Modifier.weight(1f),
                    color = TaimoorOrange
                )
                AdminMetricCard(
                    title = "Orders",
                    value = "${orders.size + 340}",
                    sub = "12 Pending",
                    modifier = Modifier.weight(1f),
                    color = TaimoorNavy
                )
                AdminMetricCard(
                    title = "Inventory",
                    value = "${products.size}",
                    sub = "10 Categories",
                    modifier = Modifier.weight(1f),
                    color = TaimoorGreen
                )
            }

            // Tab Navigation
            TabRow(
                selectedTabIndex = selectedAdminTab,
                containerColor = Color.White,
                contentColor = TaimoorOrange
            ) {
                adminTabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedAdminTab == index,
                        onClick = { selectedAdminTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (selectedAdminTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Tab Content
            when (selectedAdminTab) {
                0 -> {
                    // Products Management Tab
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(products) { product ->
                            AdminProductRow(
                                product = product,
                                onDelete = { viewModel.deleteProduct(product.id) },
                                currencyFormat = currencyFormat
                            )
                        }
                    }
                }
                1 -> {
                    // Orders Management Tab
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(orders) { order ->
                            AdminOrderManagementRow(
                                order = order,
                                onUpdateStatus = { newStatus ->
                                    viewModel.updateOrderStatus(order.id, newStatus)
                                },
                                currencyFormat = currencyFormat
                            )
                        }
                    }
                }
                2 -> {
                    // Coupons & Discounts Tab
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Text("Active Store Coupons", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                        }

                        items(SampleData.sampleCoupons) { coupon ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Surface(
                                            color = TaimoorOrangeContainer,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = coupon.code,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = TaimoorOrangeDark,
                                                fontSize = 13.sp,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(coupon.description, fontSize = 11.sp, color = TextSecondary)
                                    }

                                    Text(
                                        text = "${coupon.discountPercentage}% OFF",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = TaimoorGreen
                                    )
                                }
                            }
                        }
                    }
                }
                3 -> {
                    // Sales Analytics & Reports Tab
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Sales Performance by Category", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                CategorySalesBar("Mobile Phones", 0.42f, "Rs. 620,000")
                                CategorySalesBar("Laptops & Computers", 0.28f, "Rs. 410,000")
                                CategorySalesBar("Shoes & Watches", 0.14f, "Rs. 205,000")
                                CategorySalesBar("Fashion & Apparel", 0.10f, "Rs. 147,000")
                                CategorySalesBar("Electronics & Audio", 0.06f, "Rs. 100,900")
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Fulfillment & Delivery Metrics", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Average Delivery Time:", fontSize = 12.sp, color = TextSecondary)
                                    Text("1.8 Days", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TaimoorGreen)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("On-Time Dispatch Rate:", fontSize = 12.sp, color = TextSecondary)
                                    Text("99.4%", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TaimoorGreen)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Customer Satisfaction Rating:", fontSize = 12.sp, color = TextSecondary)
                                    Text("4.9 / 5.0 (Daraz Mall Standard)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TaimoorOrange)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Product Modal Dialog
    if (showAddProductDialog) {
        var title by remember { mutableStateOf("") }
        var price by remember { mutableStateOf("") }
        var originalPrice by remember { mutableStateOf("") }
        var category by remember { mutableStateOf(ProductCategory.MOBILE) }
        var stock by remember { mutableStateOf("25") }
        var brand by remember { mutableStateOf("Taimoor Brand") }
        var description by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddProductDialog = false },
            title = { Text("Add New Product to Store", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Product Title") }, singleLine = true)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price (Rs.)") }, modifier = Modifier.weight(1f), singleLine = true)
                        OutlinedTextField(value = originalPrice, onValueChange = { originalPrice = it }, label = { Text("Original Price") }, modifier = Modifier.weight(1f), singleLine = true)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = brand, onValueChange = { brand = it }, label = { Text("Brand") }, modifier = Modifier.weight(1f), singleLine = true)
                        OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock Qty") }, modifier = Modifier.weight(1f), singleLine = true)
                    }
                    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, maxLines = 3)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsedPrice = price.toDoubleOrNull() ?: 15000.0
                        val parsedOriginal = originalPrice.toDoubleOrNull() ?: (parsedPrice * 1.25)
                        val parsedStock = stock.toIntOrNull() ?: 20
                        val newProduct = Product(
                            title = if (title.isNotBlank()) title else "New Taimoor Smart Gadget",
                            description = if (description.isNotBlank()) description else "Premium verified item from official Taimoor shopping flagship store.",
                            category = category,
                            price = parsedPrice,
                            originalPrice = parsedOriginal,
                            stock = parsedStock,
                            brand = brand,
                            isMall = true
                        )
                        viewModel.addNewProduct(newProduct)
                        showAddProductDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TaimoorOrange)
                ) {
                    Text("Add Product")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddProductDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun AdminMetricCard(
    title: String,
    value: String,
    sub: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(title, fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Text(sub, fontSize = 9.sp, color = TextMuted)
        }
    }
}

@Composable
private fun AdminProductRow(
    product: Product,
    onDelete: () -> Unit,
    currencyFormat: NumberFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(6.dp))
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
                    Icon(
                        imageVector = getCategoryIcon(product.category),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = getCategoryColor(product.category)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(product.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Rs. ${currencyFormat.format(product.price.toLong())}", fontSize = 11.sp, color = TaimoorOrange, fontWeight = FontWeight.Bold)
                    Text("Stock: ${product.stock}", fontSize = 11.sp, color = TextSecondary)
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TaimoorRed, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun AdminOrderManagementRow(
    order: Order,
    onUpdateStatus: (OrderStatus) -> Unit,
    currencyFormat: NumberFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(order.id, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TaimoorNavy)
                StatusBadge(status = order.status)
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text("Customer: ${order.address.fullName} (${order.address.city})", fontSize = 11.sp, color = TextSecondary)
            Text("Total: Rs. ${currencyFormat.format(order.totalAmount.toLong())} | ${order.paymentMethod.title.split(" ").first()}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)

            Spacer(modifier = Modifier.height(8.dp))

            // Fast status advance buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (order.status == OrderStatus.PENDING) {
                    FilledTonalButton(
                        onClick = { onUpdateStatus(OrderStatus.CONFIRMED) },
                        modifier = Modifier.weight(1f).height(32.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Confirm & Pack", fontSize = 10.sp)
                    }
                }
                if (order.status == OrderStatus.CONFIRMED || order.status == OrderStatus.PENDING) {
                    Button(
                        onClick = { onUpdateStatus(OrderStatus.SHIPPED) },
                        modifier = Modifier.weight(1f).height(32.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TaimoorOrange),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Dispatch Order", fontSize = 10.sp)
                    }
                }
                if (order.status == OrderStatus.SHIPPED) {
                    Button(
                        onClick = { onUpdateStatus(OrderStatus.DELIVERED) },
                        modifier = Modifier.weight(1f).height(32.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TaimoorGreen),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Mark Delivered", fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun CategorySalesBar(category: String, fraction: Float, amount: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(category, fontSize = 11.sp, color = TextPrimary)
            Text(amount, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
        Spacer(modifier = Modifier.height(2.dp))
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = TaimoorOrange,
            trackColor = Color(0xFFEEEEEE)
        )
    }
}
