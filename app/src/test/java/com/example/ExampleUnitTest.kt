package com.example

import com.example.data.SampleData
import com.example.data.ShopRepository
import com.example.model.CartItem
import com.example.model.DeliveryAddress
import com.example.model.OrderStatus
import com.example.model.PaymentMethod
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ExampleUnitTest {

    private lateinit var repository: ShopRepository

    @Before
    fun setup() {
        repository = ShopRepository()
    }

    @Test
    fun testInitialProductsLoaded() {
        val products = repository.products.value
        assertTrue("Products should not be empty", products.isNotEmpty())
        assertEquals(56, products.size)
    }

    @Test
    fun testInitialOrdersAreCleanAndEmpty() {
        // App starts clean for first launch
        val orders = repository.orders.value
        assertTrue("Orders list should be empty on fresh first-time launch", orders.isEmpty())
    }

    @Test
    fun testAddToCartAndQuantityCalculation() {
        val sampleProduct = SampleData.sampleProducts[0]
        repository.addToCart(sampleProduct, quantity = 2, selectedColor = "Natural Titanium")

        val updatedCart = repository.cartItems.value
        val addedItem = updatedCart.find { it.product.id == sampleProduct.id && it.selectedColor == "Natural Titanium" }
        assertNotNull("Product should be in cart", addedItem)
        assertEquals(2, addedItem!!.quantity)
        assertEquals(sampleProduct.price * 2, addedItem.totalPrice, 0.01)
    }

    @Test
    fun testCouponApplication() {
        val (success, message) = repository.applyCoupon("TAIMOOR50")
        assertTrue("Valid coupon TAIMOOR50 should apply", success)
        assertNotNull(repository.appliedCoupon.value)
        assertEquals(50, repository.appliedCoupon.value!!.discountPercentage)

        val (failSuccess, _) = repository.applyCoupon("INVALID_CODE")
        assertFalse("Invalid coupon should fail", failSuccess)
    }

    @Test
    fun testPlaceOrderWithRazorpayAndCustomerEmail() {
        val testItem = CartItem(product = SampleData.sampleProducts[0], quantity = 1, selectedColor = "Natural Titanium")
        val order = repository.placeOrder(
            items = listOf(testItem),
            deliveryAddress = DeliveryAddress(
                fullName = "Taimoor Khan",
                email = "mmtkn0568@gmail.com",
                phoneNumber = "+92 300 1234567",
                city = "Karachi"
            ),
            paymentMethod = PaymentMethod.RAZORPAY
        )

        assertNotNull("Order should be generated", order)
        assertTrue("Order ID should start with TMS-", order.id.startsWith("TMS-"))
        assertTrue("Invoice number should start with INV-TMS-", order.invoiceNumber.startsWith("INV-TMS-"))
        assertEquals("mmtkn0568@gmail.com", order.customerEmail)
        assertEquals("+92 300 1234567", order.customerPhone)
        assertNotNull("Razorpay transaction ID should be generated", order.razorpayPaymentId)
        assertTrue("Razorpay transaction should start with pay_rzp_", order.razorpayPaymentId!!.startsWith("pay_rzp_"))
        assertEquals(OrderStatus.PENDING, order.status)

        // Ensure order is now in orders list
        val allOrders = repository.orders.value
        assertEquals(1, allOrders.size)
        assertEquals(order.id, allOrders[0].id)
    }

    @Test
    fun testOrderStatusAdvance() {
        val testItem = CartItem(product = SampleData.sampleProducts[1], quantity = 1)
        val order = repository.placeOrder(
            items = listOf(testItem),
            deliveryAddress = DeliveryAddress(fullName = "Taimoor Khan", email = "mmtkn0568@gmail.com"),
            paymentMethod = PaymentMethod.COD
        )

        repository.updateOrderStatus(order.id, OrderStatus.DELIVERED)
        val updated = repository.orders.value.first { it.id == order.id }
        assertEquals(OrderStatus.DELIVERED, updated.status)
    }
}
