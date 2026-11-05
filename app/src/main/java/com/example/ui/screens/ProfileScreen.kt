package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.OrderStatus
import com.example.ui.theme.*
import com.example.viewmodel.Screen
import com.example.viewmodel.ShopViewModel

@Composable
fun ProfileScreen(
    viewModel: ShopViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val isUrdu by viewModel.isUrdu.collectAsState()
    val isAdminMode by viewModel.isAdminMode.collectAsState()

    val pendingCount = orders.count { it.status == OrderStatus.PENDING || it.status == OrderStatus.CONFIRMED }
    val shippedCount = orders.count { it.status == OrderStatus.SHIPPED || it.status == OrderStatus.OUT_FOR_DELIVERY }
    val deliveredCount = orders.count { it.status == OrderStatus.DELIVERED }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NeutralLightBg)
            .verticalScroll(rememberScrollState())
    ) {
        // User Header Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = TaimoorNavy
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(TaimoorOrange),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "TK",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = userProfile.name,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = TaimoorOrange,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "VIP MEMBER",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = userProfile.email, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                        Text(text = userProfile.phone, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                    }
                }
            }
        }

        // My Orders Summary Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("profile_orders_section"),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isUrdu) "میرے آرڈرز" else "My Orders",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { viewModel.navigateTo(Screen.MY_ORDERS) }
                    ) {
                        Text("View All", fontSize = 12.sp, color = TaimoorOrange, fontWeight = FontWeight.SemiBold)
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TaimoorOrange, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    OrderStatusQuickItem(
                        icon = Icons.Outlined.Inventory,
                        title = "To Pack",
                        count = pendingCount,
                        onClick = { viewModel.navigateTo(Screen.MY_ORDERS) }
                    )
                    OrderStatusQuickItem(
                        icon = Icons.Outlined.LocalShipping,
                        title = "To Ship",
                        count = shippedCount,
                        onClick = { viewModel.navigateTo(Screen.MY_ORDERS) }
                    )
                    OrderStatusQuickItem(
                        icon = Icons.Outlined.CheckCircle,
                        title = "Delivered",
                        count = deliveredCount,
                        onClick = { viewModel.navigateTo(Screen.MY_ORDERS) }
                    )
                    OrderStatusQuickItem(
                        icon = Icons.Outlined.AssignmentReturn,
                        title = "Returns",
                        count = 0,
                        onClick = { viewModel.navigateTo(Screen.MY_ORDERS) }
                    )
                }
            }
        }

        // Admin Portal Entry Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .clickable { viewModel.navigateTo(Screen.ADMIN_DASHBOARD) }
                .testTag("admin_dashboard_card"),
            colors = CardDefaults.cardColors(containerColor = TaimoorNavy),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(TaimoorOrange),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Dashboard, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text("Admin & Seller Portal", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Manage inventory, orders & revenue reports", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                    }
                }

                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Settings & Options Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                ProfileOptionRow(
                    icon = Icons.Outlined.Translate,
                    title = "App Language / زبان",
                    subtitle = if (isUrdu) "اردو (Urdu Active)" else "English (US)",
                    onClick = { viewModel.toggleUrduLanguage() }
                )
                Divider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F3F5))

                ProfileOptionRow(
                    icon = Icons.Outlined.FavoriteBorder,
                    title = "My Wishlist",
                    subtitle = "Saved items and bookmarks",
                    onClick = { viewModel.navigateTo(Screen.WISHLIST) }
                )
                Divider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F3F5))

                ProfileOptionRow(
                    icon = Icons.Outlined.LocationOn,
                    title = "Saved Delivery Addresses",
                    subtitle = userProfile.savedAddresses.firstOrNull()?.streetAddress ?: "PECHS, Karachi",
                    onClick = { viewModel.navigateTo(Screen.CHECKOUT) }
                )
                Divider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F3F5))

                ProfileOptionRow(
                    icon = Icons.Outlined.Payment,
                    title = "Payment Methods",
                    subtitle = "JazzCash, Easypaisa, COD, Credit/Debit Card",
                    onClick = { viewModel.navigateTo(Screen.CHECKOUT) }
                )
                Divider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F3F5))

                ProfileOptionRow(
                    icon = Icons.Outlined.Headphones,
                    title = "24/7 Customer Support & Help",
                    subtitle = "Live chat and dispute resolution center",
                    onClick = { /* Help center */ }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun OrderStatusQuickItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    count: Int,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        BadgedBox(
            badge = {
                if (count > 0) {
                    Badge(containerColor = TaimoorOrange, contentColor = Color.White) {
                        Text("$count")
                    }
                }
            }
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = TaimoorNavy,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = title,
            fontSize = 11.sp,
            color = TextPrimary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ProfileOptionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = TaimoorNavy, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
            Text(text = subtitle, fontSize = 11.sp, color = TextSecondary)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
    }
}
