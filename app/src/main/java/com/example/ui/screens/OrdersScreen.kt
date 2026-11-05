package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Order
import com.example.model.OrderStatus
import com.example.ui.components.getCategoryColor
import com.example.ui.components.getCategoryIcon
import com.example.ui.theme.*
import com.example.viewmodel.Screen
import com.example.viewmodel.ShopViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    viewModel: ShopViewModel,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.orders.collectAsState()
    val isUrdu by viewModel.isUrdu.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val currencyFormat = NumberFormat.getNumberInstance(Locale.US)

    val tabs = listOf("All", "Pending", "Shipped", "Delivered")

    val filteredOrders = when (selectedTab) {
        1 -> orders.filter { it.status == OrderStatus.PENDING || it.status == OrderStatus.CONFIRMED }
        2 -> orders.filter { it.status == OrderStatus.SHIPPED || it.status == OrderStatus.OUT_FOR_DELIVERY }
        3 -> orders.filter { it.status == OrderStatus.DELIVERED }
        else -> orders
    }

    BackHandler {
        viewModel.navigateTo(Screen.PROFILE)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(if (isUrdu) "میرے آرڈرز" else "My Orders", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 17.sp) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.PROFILE) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TaimoorNavy)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(NeutralLightBg)
        ) {
            // Status Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = TaimoorOrange
            ) {
                tabs.forEachIndexed { index, tabTitle ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = tabTitle,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            if (filteredOrders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingBag,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            tint = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "No orders found in this category",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredOrders) { order ->
                        OrderCard(
                            order = order,
                            onTrackClick = { viewModel.openOrderTracking(order) },
                            currencyFormat = currencyFormat
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderCard(
    order: Order,
    onTrackClick: () -> Unit,
    currencyFormat: NumberFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Order ID & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Order #${order.id}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TaimoorNavy)
                    Text(text = order.orderDate, fontSize = 10.sp, color = TextMuted)
                }

                StatusBadge(status = order.status)
            }

            Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF1F3F5))

            // Items Preview
            order.items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = getCategoryIcon(item.product.category),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = getCategoryColor(item.product.category)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${item.quantity}x ${item.product.title}",
                        fontSize = 12.sp,
                        color = TextPrimary,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Total and Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Total Price:", fontSize = 10.sp, color = TextSecondary)
                    Text(
                        text = "Rs. ${currencyFormat.format(order.totalAmount.toLong())}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = TaimoorOrange
                    )
                }

                Button(
                    onClick = onTrackClick,
                    colors = ButtonDefaults.buttonColors(containerColor = TaimoorNavy),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("track_order_${order.id}")
                ) {
                    Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Track Order", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: OrderStatus) {
    val (bgColor, textColor) = when (status) {
        OrderStatus.PENDING -> Pair(Color(0xFFFFF8E1), Color(0xFFF57F17))
        OrderStatus.CONFIRMED -> Pair(Color(0xFFE1F5FE), Color(0xFF0288D1))
        OrderStatus.SHIPPED, OrderStatus.OUT_FOR_DELIVERY -> Pair(Color(0xFFEDE7F6), Color(0xFF512DA8))
        OrderStatus.DELIVERED -> Pair(Color(0xFFE8F5E9), TaimoorGreen)
        OrderStatus.CANCELLED -> Pair(Color(0xFFFFEBEE), TaimoorRed)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = status.title,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}
