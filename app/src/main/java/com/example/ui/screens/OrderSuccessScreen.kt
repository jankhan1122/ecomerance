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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PaymentMethod
import com.example.ui.theme.*
import com.example.util.InvoiceGenerator
import com.example.viewmodel.Screen
import com.example.viewmodel.ShopViewModel
import java.text.NumberFormat
import java.util.*

@Composable
fun OrderSuccessScreen(
    viewModel: ShopViewModel,
    modifier: Modifier = Modifier
) {
    val order = viewModel.lastPlacedOrder.collectAsState().value
    val context = LocalContext.current
    val currencyFormat = NumberFormat.getNumberInstance(Locale.US)

    BackHandler {
        viewModel.navigateTo(Screen.HOME)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Download Receipt Button (MANDATORY per user request)
                    Button(
                        onClick = {
                            if (order != null) {
                                InvoiceGenerator.downloadReceiptFile(context, order)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TaimoorNavy),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("download_receipt_button")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Download Receipt (رسید ڈاؤن لوڈ کریں)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (order != null) {
                                    InvoiceGenerator.sendEmailInvoiceIntent(context, order)
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("email_receipt_button")
                        ) {
                            Icon(Icons.Default.Email, contentDescription = null, tint = TaimoorOrange, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Email Invoice", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = TaimoorOrange)
                        }

                        Button(
                            onClick = {
                                if (order != null) {
                                    viewModel.openOrderTracking(order)
                                } else {
                                    viewModel.navigateTo(Screen.MY_ORDERS)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TaimoorOrange),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("track_new_order_btn")
                        ) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Track Order", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    TextButton(
                        onClick = { viewModel.navigateTo(Screen.HOME) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Back to Shopping", fontSize = 13.sp, color = TextSecondary)
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Big Green Circle Checkmark
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .background(TaimoorGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Success",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Order Placed Successfully!",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Thank you for shopping at TAIMOOR ONLINE SHOPPING APP.",
                fontSize = 12.sp,
                color = TextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email & SMS Dispatched Banner
            Surface(
                color = Color(0xFFF0FDF4),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MarkEmailRead,
                        contentDescription = null,
                        tint = TaimoorGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Email & SMS Confirmation Sent!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF166534)
                        )
                        Text(
                            text = "Receipt dispatched to mmtkn0568@gmail.com and SMS sent to ${order?.customerPhone ?: "+92 300 1234567"}.",
                            fontSize = 11.sp,
                            color = Color(0xFF15803D),
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Order Receipt Card
            if (order != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Official Invoice", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                            Surface(
                                color = TaimoorOrangeContainer,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = order.invoiceNumber,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TaimoorOrangeDark,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Tracking Number", fontSize = 12.sp, color = TextSecondary)
                            Text(order.id, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TaimoorNavy)
                        }

                        Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF1F3F5))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Payment Gateway", fontSize = 12.sp, color = TextSecondary)
                            Text(order.paymentMethod.title.split("(").first(), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        if (order.paymentMethod == PaymentMethod.RAZORPAY) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Razorpay Ref", fontSize = 11.sp, color = Color(0xFF0284C7))
                                Text(order.razorpayPaymentId ?: "pay_rzp_9841285", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0284C7))
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Customer Email", fontSize = 12.sp, color = TextSecondary)
                            Text(order.customerEmail, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Delivery Address", fontSize = 12.sp, color = TextSecondary)
                            Text("${order.address.city}, Pakistan", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF1F3F5))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Amount Paid", fontSize = 13.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                            Text("Rs. ${currencyFormat.format(order.totalAmount.toLong())}", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = TaimoorOrange)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
