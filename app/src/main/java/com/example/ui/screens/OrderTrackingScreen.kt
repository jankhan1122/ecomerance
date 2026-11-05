package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.model.OrderStatus
import com.example.model.TrackingStep
import com.example.ui.theme.*
import com.example.util.InvoiceGenerator
import com.example.viewmodel.Screen
import com.example.viewmodel.ShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    viewModel: ShopViewModel,
    modifier: Modifier = Modifier
) {
    val order = viewModel.selectedOrder.collectAsState().value
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    BackHandler {
        viewModel.navigateBack()
    }

    if (order == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No order selected")
        }
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Live Order Tracking", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 17.sp) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
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
                .verticalScroll(rememberScrollState())
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Status Header Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = TaimoorNavy),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Tracking ID", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.testTag("copy_tracking_id")
                        ) {
                            Text(order.id, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            IconButton(
                                onClick = { clipboardManager.setText(AnnotatedString(order.id)) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TaimoorOrange, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalShipping, contentDescription = null, tint = TaimoorOrange, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Current Status: ${order.status.title}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(order.estimatedDeliveryDate, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Courier Partner: ${order.courierName}", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                }
            }

            // Receipt & Email Invoice Action Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { InvoiceGenerator.downloadReceiptFile(context, order) },
                    colors = ButtonDefaults.buttonColors(containerColor = TaimoorNavy),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Download Receipt", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { InvoiceGenerator.sendEmailInvoiceIntent(context, order) },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Icon(Icons.Default.Email, contentDescription = null, tint = TaimoorOrange, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Email to mmtkn0568", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TaimoorOrange)
                }
            }

            // Interactive Status Advancement Button (Simulator)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Simulate Live Tracking", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TaimoorOrangeDark)
                        Text("Advance order status through delivery pipeline", fontSize = 10.sp, color = TextSecondary)
                    }

                    Button(
                        onClick = {
                            val next = when (order.status) {
                                OrderStatus.PENDING -> OrderStatus.CONFIRMED
                                OrderStatus.CONFIRMED -> OrderStatus.SHIPPED
                                OrderStatus.SHIPPED -> OrderStatus.OUT_FOR_DELIVERY
                                OrderStatus.OUT_FOR_DELIVERY -> OrderStatus.DELIVERED
                                OrderStatus.DELIVERED -> OrderStatus.PENDING
                                OrderStatus.CANCELLED -> OrderStatus.PENDING
                            }
                            viewModel.updateOrderStatus(order.id, next)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TaimoorOrange),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Advance Status", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Timeline Milestones
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Delivery Progress Timeline", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)

                    Spacer(modifier = Modifier.height(16.dp))

                    order.trackingSteps.forEachIndexed { index, step ->
                        TimelineRow(
                            step = step,
                            isLast = index == order.trackingSteps.size - 1
                        )
                    }
                }
            }

            // Destination Address Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Home, contentDescription = null, tint = TaimoorNavy, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Shipping Address", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text("${order.address.fullName}  |  ${order.address.phoneNumber}", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Text("${order.address.streetAddress}, ${order.address.city}, ${order.address.province}", fontSize = 11.sp, color = TextSecondary)
                }
            }
        }
    }
}

@Composable
private fun TimelineRow(
    step: TrackingStep,
    isLast: Boolean
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Vertical line with dot
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(if (step.isCompleted) TaimoorGreen else Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                if (step.isCompleted) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                }
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(46.dp)
                        .background(if (step.isCompleted) TaimoorGreen else Color(0xFFE0E0E0))
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Text Info
        Column(modifier = Modifier.weight(1f).padding(bottom = if (isLast) 0.dp else 16.dp)) {
            Text(
                text = step.status.title,
                fontWeight = if (step.isCompleted) FontWeight.Bold else FontWeight.Medium,
                fontSize = 13.sp,
                color = if (step.isCompleted) TextPrimary else Color.Gray
            )

            Text(
                text = step.description,
                fontSize = 11.sp,
                color = TextSecondary
            )

            Text(
                text = step.timestamp,
                fontSize = 10.sp,
                color = TextMuted
            )
        }
    }
}
