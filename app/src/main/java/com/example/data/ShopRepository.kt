package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

enum class ProductSortType(val title: String) {
    POPULAR("Most Popular"),
    PRICE_LOW_HIGH("Price: Low to High"),
    PRICE_HIGH_LOW("Price: High to Low"),
    RATING("Highest Rated"),
    DISCOUNT("Biggest Discount")
}

class ShopRepository {

    private val _products = MutableStateFlow<List<Product>>(SampleData.sampleProducts)
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(
        listOf(
            CartItem(product = SampleData.sampleProducts[6], quantity = 1, selectedColor = "White & Orange"),
            CartItem(product = SampleData.sampleProducts[8], quantity = 1, selectedColor = "Matte Black")
        )
    )
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _wishlistProductIds = MutableStateFlow<Set<String>>(
        setOf(SampleData.sampleProducts[0].id, SampleData.sampleProducts[2].id)
    )
    val wishlistProductIds: StateFlow<Set<String>> = _wishlistProductIds.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(SampleData.createInitialOrders())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _appliedCoupon = MutableStateFlow<Coupon?>(null)
    val appliedCoupon: StateFlow<Coupon?> = _appliedCoupon.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _isUrdu = MutableStateFlow(false)
    val isUrdu: StateFlow<Boolean> = _isUrdu.asStateFlow()

    private val _isAdminMode = MutableStateFlow(false)
    val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

    // Cart Operations
    fun addToCart(product: Product, quantity: Int = 1, selectedColor: String = "Default") {
        val currentList = _cartItems.value.toMutableList()
        val existingIndex = currentList.indexOfFirst { it.product.id == product.id && it.selectedColor == selectedColor }
        if (existingIndex != -1) {
            val existing = currentList[existingIndex]
            currentList[existingIndex] = existing.copy(quantity = existing.quantity + quantity)
        } else {
            currentList.add(CartItem(product = product, quantity = quantity, selectedColor = selectedColor))
        }
        _cartItems.value = currentList
    }

    fun updateCartItemQuantity(productId: String, color: String, newQuantity: Int) {
        val currentList = _cartItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.product.id == productId && it.selectedColor == color }
        if (index != -1) {
            if (newQuantity <= 0) {
                currentList.removeAt(index)
            } else {
                currentList[index] = currentList[index].copy(quantity = newQuantity)
            }
            _cartItems.value = currentList
        }
    }

    fun removeCartItem(productId: String, color: String) {
        _cartItems.value = _cartItems.value.filterNot { it.product.id == productId && it.selectedColor == color }
    }

    fun toggleCartItemSelection(productId: String, color: String) {
        val currentList = _cartItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.product.id == productId && it.selectedColor == color }
        if (index != -1) {
            val item = currentList[index]
            currentList[index] = item.copy(isSelected = !item.isSelected)
            _cartItems.value = currentList
        }
    }

    fun selectAllCartItems(select: Boolean) {
        _cartItems.value = _cartItems.value.map { it.copy(isSelected = select) }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    // Wishlist Operations
    fun toggleWishlist(productId: String) {
        val set = _wishlistProductIds.value.toMutableSet()
        if (set.contains(productId)) {
            set.remove(productId)
        } else {
            set.add(productId)
        }
        _wishlistProductIds.value = set
    }

    // Coupon Operations
    fun applyCoupon(code: String): Pair<Boolean, String> {
        val trimmed = code.trim().uppercase()
        val match = SampleData.sampleCoupons.find { it.code == trimmed }
        return if (match != null) {
            _appliedCoupon.value = match
            Pair(true, "Voucher applied! ${match.discountPercentage}% discount")
        } else {
            Pair(false, "Invalid coupon code. Try TAIMOOR50 or DARAZ10")
        }
    }

    fun removeCoupon() {
        _appliedCoupon.value = null
    }

    // Order Placement Flow
    fun placeOrder(
        items: List<CartItem>,
        deliveryAddress: DeliveryAddress,
        paymentMethod: PaymentMethod
    ): Order {
        val subtotal = items.sumOf { it.totalPrice }
        val shippingFee = if (subtotal > 5000.0) 0.0 else 250.0
        val coupon = _appliedCoupon.value
        val discountAmount = if (coupon != null && subtotal >= coupon.minOrderAmount) {
            val calc = subtotal * (coupon.discountPercentage / 100.0)
            calc.coerceAtMost(coupon.maxDiscount)
        } else 0.0

        val total = (subtotal + shippingFee - discountAmount).coerceAtLeast(0.0)
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val orderTime = dateFormat.format(Date())

        val trackingSteps = listOf(
            TrackingStep(
                OrderStatus.PENDING,
                "Order placed successfully via ${paymentMethod.title}",
                orderTime,
                true
            ),
            TrackingStep(
                OrderStatus.CONFIRMED,
                "Order confirmed and dispatched for processing at central warehouse",
                "Estimated: 3-6 hours",
                false
            ),
            TrackingStep(
                OrderStatus.SHIPPED,
                "Handed over to Leopards/TCS Courier partner",
                "Estimated: Tomorrow",
                false
            ),
            TrackingStep(
                OrderStatus.OUT_FOR_DELIVERY,
                "Courier assigned for doorstep delivery in ${deliveryAddress.city}",
                "Estimated: 2 business days",
                false
            ),
            TrackingStep(
                OrderStatus.DELIVERED,
                "Delivered safely to recipient: ${deliveryAddress.fullName}",
                "Pending final delivery",
                false
            )
        )

        val razorpayId = if (paymentMethod == PaymentMethod.RAZORPAY) "pay_rzp_" + (10000000..99999999).random() else null

        val newOrder = Order(
            items = items,
            subtotal = subtotal,
            shippingFee = shippingFee,
            discountAmount = discountAmount,
            totalAmount = total,
            address = deliveryAddress,
            paymentMethod = paymentMethod,
            orderDate = orderTime,
            status = OrderStatus.PENDING,
            trackingSteps = trackingSteps,
            courierName = "Taimoor Express Logistics (Leopards / TCS)",
            estimatedDeliveryDate = "Delivery expected in 2-3 Days",
            customerEmail = deliveryAddress.email.ifBlank { "mmtkn0568@gmail.com" },
            customerPhone = deliveryAddress.phoneNumber,
            razorpayPaymentId = razorpayId
        )

        val currentOrders = _orders.value.toMutableList()
        currentOrders.add(0, newOrder)
        _orders.value = currentOrders

        // Remove ordered items from cart
        val orderedKeys = items.map { "${it.product.id}_${it.selectedColor}" }.toSet()
        _cartItems.value = _cartItems.value.filterNot { orderedKeys.contains("${it.product.id}_${it.selectedColor}") }
        _appliedCoupon.value = null

        return newOrder
    }

    // Admin Operations
    fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        val currentOrders = _orders.value.toMutableList()
        val index = currentOrders.indexOfFirst { it.id == orderId }
        if (index != -1) {
            val order = currentOrders[index]
            val updatedSteps = order.trackingSteps.map { step ->
                if (step.status == newStatus || isStatusReached(step.status, newStatus)) {
                    step.copy(isCompleted = true)
                } else {
                    step.copy(isCompleted = false)
                }
            }
            currentOrders[index] = order.copy(status = newStatus, trackingSteps = updatedSteps)
            _orders.value = currentOrders
        }
    }

    private fun isStatusReached(target: OrderStatus, current: OrderStatus): Boolean {
        val orderHierarchy = listOf(
            OrderStatus.PENDING,
            OrderStatus.CONFIRMED,
            OrderStatus.SHIPPED,
            OrderStatus.OUT_FOR_DELIVERY,
            OrderStatus.DELIVERED
        )
        val targetIdx = orderHierarchy.indexOf(target)
        val currentIdx = orderHierarchy.indexOf(current)
        return targetIdx != -1 && currentIdx != -1 && targetIdx <= currentIdx
    }

    fun addProduct(product: Product) {
        val list = _products.value.toMutableList()
        list.add(0, product)
        _products.value = list
    }

    fun updateProduct(product: Product) {
        val list = _products.value.toMutableList()
        val idx = list.indexOfFirst { it.id == product.id }
        if (idx != -1) {
            list[idx] = product
            _products.value = list
        }
    }

    fun deleteProduct(productId: String) {
        _products.value = _products.value.filterNot { it.id == productId }
    }

    fun toggleUrduLanguage() {
        _isUrdu.value = !_isUrdu.value
    }

    fun toggleAdminMode() {
        _isAdminMode.value = !_isAdminMode.value
    }

    fun updateUserAddress(address: DeliveryAddress) {
        _userProfile.value = _userProfile.value.copy(
            savedAddresses = listOf(address)
        )
    }
}
