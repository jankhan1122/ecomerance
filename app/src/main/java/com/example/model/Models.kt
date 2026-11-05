package com.example.model

import java.util.UUID

enum class ProductCategory(val displayName: String, val urduName: String, val iconName: String) {
    ALL("All", "تمام", "Apps"),
    MOBILE("Mobile Phones", "موبائل فونز", "PhoneAndroid"),
    LAPTOPS("Laptops & PC", "لیپ ٹاپ", "Laptop"),
    MENS_FASHION("Men's Fashion", "مردانہ فیشن", "Checkroom"),
    WOMENS_FASHION("Women's Fashion", "زنprofileانہ فیشن", "DryCleaning"),
    SHOES_WATCHES("Shoes & Watches", "جوتے اور گھڑیاں", "Watch"),
    ELECTRONICS("Electronics", "الیکٹرانکس", "Headphones"),
    HOME_KITCHEN("Home & Kitchen", "گھر اور باورچی خانہ", "Kitchen"),
    BEAUTY_HEALTH("Beauty & Health", "خوبصورتی اور صحت", "Spa"),
    SPORTS_FITNESS("Sports & Fitness", "کھیل اور فٹنس", "FitnessCenter"),
    GROCERY("Grocery", "کریانہ", "LocalGroceryStore")
}

data class ProductReview(
    val id: String = UUID.randomUUID().toString(),
    val userName: String,
    val rating: Int,
    val date: String,
    val comment: String,
    val verifiedPurchase: Boolean = true
)

data class Product(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val category: ProductCategory,
    val price: Double, // in PKR (e.g. 45000.0)
    val originalPrice: Double,
    val rating: Float = 4.8f,
    val reviewCount: Int = 128,
    val stock: Int = 25,
    val isMall: Boolean = true, // Daraz Mall / Taimoor Mall verified
    val isFlashSale: Boolean = false,
    val flashSaleDiscountPercent: Int = 0,
    val freeDelivery: Boolean = true,
    val soldCount: Int = 89,
    val imageDrawableRes: Int? = null,
    val imageUrl: String = "",
    val reviews: List<ProductReview> = emptyList(),
    val brand: String = "Taimoor Certified",
    val colors: List<String> = listOf("Black", "Silver", "Orange"),
    val warranty: String = "1 Year Brand Warranty"
) {
    val discountPercent: Int
        get() = if (originalPrice > price) {
            (((originalPrice - price) / originalPrice) * 100).toInt()
        } else flashSaleDiscountPercent
}

data class CartItem(
    val product: Product,
    var quantity: Int = 1,
    var selectedColor: String = "Default",
    var isSelected: Boolean = true
) {
    val totalPrice: Double
        get() = product.price * quantity
}

enum class OrderStatus(val title: String, val urduTitle: String) {
    PENDING("Order Placed", "آرڈر موصول ہو گیا"),
    CONFIRMED("Confirmed & Packed", "آرڈر تیار ہے"),
    SHIPPED("Dispatched / In Transit", "روانہ کر دیا گیا"),
    OUT_FOR_DELIVERY("Out for Delivery", "ڈلیوری کے لیے روانہ"),
    DELIVERED("Delivered", "ڈلیور ہو گیا"),
    CANCELLED("Cancelled", "منسوخ ہو گیا")
}

data class TrackingStep(
    val status: OrderStatus,
    val description: String,
    val timestamp: String,
    val isCompleted: Boolean
)

enum class PaymentMethod(val title: String, val subtitle: String, val iconRes: String) {
    COD("Cash on Delivery (COD)", "Pay when package arrives at your doorstep", "LocalAtm"),
    RAZORPAY("Razorpay Online Gateway", "Direct Cards, UPI & NetBanking Checkout", "Payment"),
    JAZZCASH("JazzCash Mobile Account", "Instant 0% fee payment via JazzCash wallet", "AccountBalanceWallet"),
    EASYPAISA("Easypaisa Account", "Pay quickly via your Easypaisa number", "PhoneIphone"),
    CARD("Debit / Credit Card", "Visa & MasterCard 3D Secure Protection", "CreditCard"),
    BANK_TRANSFER("Direct Bank Transfer", "HBL / Meezan / Alfalah Online Banking", "AccountBalance")
}

data class DeliveryAddress(
    val fullName: String = "Taimoor Khan",
    val phoneNumber: String = "+92 300 1234567",
    val email: String = "mmtkn0568@gmail.com",
    val streetAddress: String = "House # 42, Block 6, PECHS",
    val city: String = "Karachi",
    val province: String = "Sindh",
    val postalCode: String = "75400"
)

data class Order(
    val id: String = "TMS-" + (100000..999999).random(),
    val invoiceNumber: String = "INV-TMS-" + (10000..99999).random(),
    val items: List<CartItem>,
    val subtotal: Double,
    val shippingFee: Double,
    val discountAmount: Double,
    val totalAmount: Double,
    val address: DeliveryAddress,
    val paymentMethod: PaymentMethod,
    val orderDate: String,
    var status: OrderStatus = OrderStatus.PENDING,
    val trackingSteps: List<TrackingStep> = emptyList(),
    val courierName: String = "Taimoor Express Courier (TCS / Leopards)",
    val estimatedDeliveryDate: String = "2-4 Business Days",
    val customerEmail: String = "mmtkn0568@gmail.com",
    val customerPhone: String = "+92 300 1234567",
    val razorpayPaymentId: String? = null
)

data class Coupon(
    val code: String,
    val discountPercentage: Int,
    val maxDiscount: Double,
    val minOrderAmount: Double,
    val description: String
)

data class UserProfile(
    val name: String = "Taimoor Khan",
    val email: String = "mmtkn0568@gmail.com",
    val phone: String = "+92 300 1234567",
    val avatarUrl: String = "",
    val isAdmin: Boolean = false,
    val savedAddresses: List<DeliveryAddress> = listOf(
        DeliveryAddress()
    )
)
