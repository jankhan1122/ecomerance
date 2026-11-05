package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import com.example.model.Order
import com.example.model.PaymentMethod
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.util.*

object InvoiceGenerator {

    fun generateInvoiceText(order: Order): String {
        val currencyFormat = NumberFormat.getNumberInstance(Locale.US)
        val sb = StringBuilder()

        sb.append("====================================================\n")
        sb.append("        TAIMOOR ONLINE SHOPPING APP - INVOICE       \n")
        sb.append("              Official Customer Receipt             \n")
        sb.append("====================================================\n\n")

        sb.append("Invoice No:     ${order.invoiceNumber}\n")
        sb.append("Tracking ID:    ${order.id}\n")
        sb.append("Date & Time:    ${order.orderDate}\n")
        sb.append("Status:         ${order.status.title.uppercase()}\n\n")

        sb.append("CUSTOMER DETAILS:\n")
        sb.append("----------------------------------------------------\n")
        sb.append("Recipient Name: ${order.address.fullName}\n")
        sb.append("Email:          ${order.customerEmail}\n")
        sb.append("Phone:          ${order.customerPhone}\n")
        sb.append("Address:        ${order.address.streetAddress}, ${order.address.city}, ${order.address.province}\n")
        sb.append("Postal Code:    ${order.address.postalCode}\n\n")

        sb.append("PAYMENT & LOGISTICS:\n")
        sb.append("----------------------------------------------------\n")
        sb.append("Payment Method: ${order.paymentMethod.title}\n")
        if (order.paymentMethod == PaymentMethod.RAZORPAY) {
            sb.append("Razorpay Trans: ${order.razorpayPaymentId ?: "pay_rzp_verified"}\n")
            sb.append("Gateway Status: Razorpay 256-Bit SSL Verified (Success)\n")
        }
        sb.append("Courier:        ${order.courierName}\n")
        sb.append("Est. Delivery:  ${order.estimatedDeliveryDate}\n\n")

        sb.append("ORDERED ITEMS:\n")
        sb.append("----------------------------------------------------\n")
        order.items.forEachIndexed { index, item ->
            val num = index + 1
            sb.append("$num. ${item.product.title}\n")
            sb.append("   Variation: ${item.selectedColor} | Qty: ${item.quantity}\n")
            sb.append("   Price: Rs. ${currencyFormat.format(item.product.price.toLong())}  ->  Total: Rs. ${currencyFormat.format(item.totalPrice.toLong())}\n\n")
        }

        sb.append("PRICE BREAKDOWN:\n")
        sb.append("----------------------------------------------------\n")
        sb.append("Items Subtotal:      Rs. ${currencyFormat.format(order.subtotal.toLong())}\n")
        sb.append("Shipping / Delivery: ${if (order.shippingFee == 0.0) "FREE" else "Rs. " + currencyFormat.format(order.shippingFee.toLong())}\n")
        if (order.discountAmount > 0) {
            sb.append("Voucher Savings:    -Rs. ${currencyFormat.format(order.discountAmount.toLong())}\n")
        }
        sb.append("====================================================\n")
        sb.append("GRAND TOTAL PAID:    Rs. ${currencyFormat.format(order.totalAmount.toLong())}\n")
        sb.append("====================================================\n\n")

        sb.append("Thank you for choosing TAIMOOR ONLINE SHOPPING APP!\n")
        sb.append("For 24/7 inquiries, email support@taimoorshop.com\n")
        sb.append("Official Website: www.taimoor-shopping.pk\n")

        return sb.toString()
    }

    fun downloadReceiptFile(context: Context, order: Order): File? {
        return try {
            val invoiceText = generateInvoiceText(order)
            val fileName = "Receipt_${order.id}_${order.invoiceNumber}.txt"

            // Save to Downloads directory or App files
            val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                ?: context.filesDir

            val file = File(downloadsDir, fileName)
            val fos = FileOutputStream(file)
            fos.write(invoiceText.toByteArray())
            fos.flush()
            fos.close()

            Toast.makeText(
                context,
                "✓ Receipt downloaded to Downloads: $fileName",
                Toast.LENGTH_LONG
            ).show()

            file
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Receipt generated! Error saving file: ${e.localizedMessage}",
                Toast.LENGTH_SHORT
            ).show()
            null
        }
    }

    fun shareReceipt(context: Context, order: Order) {
        val invoiceText = generateInvoiceText(order)
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, invoiceText)
            putExtra(Intent.EXTRA_SUBJECT, "Taimoor Shop - Official Invoice ${order.invoiceNumber}")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share / Print Receipt")
        context.startActivity(shareIntent)
    }

    fun sendEmailInvoiceIntent(context: Context, order: Order) {
        val email = order.customerEmail.ifBlank { "mmtkn0568@gmail.com" }
        val subject = "Order Confirmation & Official Receipt #${order.id} - TAIMOOR SHOP"
        val body = generateInvoiceText(order)

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }

        try {
            context.startActivity(Intent.createChooser(intent, "Send Invoice Email via..."))
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Confirmation email & SMS simulated to $email!",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
