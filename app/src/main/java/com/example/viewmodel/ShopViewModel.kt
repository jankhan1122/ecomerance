package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ProductSortType
import com.example.data.ShopRepository
import com.example.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class Screen {
    HOME,
    PRODUCTS_CATALOG,
    PRODUCT_DETAIL,
    CART,
    CHECKOUT,
    ORDER_SUCCESS,
    MY_ORDERS,
    ORDER_TRACKING,
    WISHLIST,
    PROFILE,
    ADMIN_DASHBOARD,
    AUTH
}

data class UiNotification(
    val message: String,
    val isError: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

class ShopViewModel(
    private val repository: ShopRepository = ShopRepository()
) : ViewModel() {

    private val screenStack = mutableListOf(Screen.HOME)
    private val _currentScreen = MutableStateFlow(Screen.HOME)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Products & Filtering
    val allProducts: StateFlow<List<Product>> = repository.products
    private val _selectedCategory = MutableStateFlow(ProductCategory.ALL)
    val selectedCategory: StateFlow<ProductCategory> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortType = MutableStateFlow(ProductSortType.POPULAR)
    val sortType: StateFlow<ProductSortType> = _sortType.asStateFlow()

    private val _mallOnlyFilter = MutableStateFlow(false)
    val mallOnlyFilter: StateFlow<Boolean> = _mallOnlyFilter.asStateFlow()

    val filteredProducts: StateFlow<List<Product>> = combine(
        allProducts,
        _selectedCategory,
        _searchQuery,
        _sortType,
        _mallOnlyFilter
    ) { products, category, query, sort, mallOnly ->
        var list = products

        if (category != ProductCategory.ALL) {
            list = list.filter { it.category == category }
        }

        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.title.lowercase().contains(q) ||
                it.brand.lowercase().contains(q) ||
                it.category.displayName.lowercase().contains(q)
            }
        }

        if (mallOnly) {
            list = list.filter { it.isMall }
        }

        when (sort) {
            ProductSortType.POPULAR -> list.sortedByDescending { it.soldCount }
            ProductSortType.PRICE_LOW_HIGH -> list.sortedBy { it.price }
            ProductSortType.PRICE_HIGH_LOW -> list.sortedByDescending { it.price }
            ProductSortType.RATING -> list.sortedByDescending { it.rating }
            ProductSortType.DISCOUNT -> list.sortedByDescending { it.discountPercent }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Product for Detail View
    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    // Cart & Wishlist
    val cartItems: StateFlow<List<CartItem>> = repository.cartItems
    val wishlistProductIds: StateFlow<Set<String>> = repository.wishlistProductIds
    val appliedCoupon: StateFlow<Coupon?> = repository.appliedCoupon
    val userProfile: StateFlow<UserProfile> = repository.userProfile
    val isUrdu: StateFlow<Boolean> = repository.isUrdu
    val isAdminMode: StateFlow<Boolean> = repository.isAdminMode

    // Orders
    val orders: StateFlow<List<Order>> = repository.orders
    private val _selectedOrder = MutableStateFlow<Order?>(null)
    val selectedOrder: StateFlow<Order?> = _selectedOrder.asStateFlow()

    private val _lastPlacedOrder = MutableStateFlow<Order?>(null)
    val lastPlacedOrder: StateFlow<Order?> = _lastPlacedOrder.asStateFlow()

    // Checkout UI states
    private val _selectedPaymentMethod = MutableStateFlow(PaymentMethod.COD)
    val selectedPaymentMethod: StateFlow<PaymentMethod> = _selectedPaymentMethod.asStateFlow()

    private val _deliveryAddress = MutableStateFlow(DeliveryAddress())
    val deliveryAddress: StateFlow<DeliveryAddress> = _deliveryAddress.asStateFlow()

    // Notification toast / snackbar
    private val _notification = MutableStateFlow<UiNotification?>(null)
    val notification: StateFlow<UiNotification?> = _notification.asStateFlow()

    // Flash sale countdown (seconds left in sale)
    private val _flashSaleSeconds = MutableStateFlow(14 * 3600 + 42 * 60 + 19)
    val flashSaleSeconds: StateFlow<Int> = _flashSaleSeconds.asStateFlow()

    init {
        // Flash sale timer tick
        viewModelScope.launch {
            while (true) {
                delay(1000)
                if (_flashSaleSeconds.value > 0) {
                    _flashSaleSeconds.value -= 1
                } else {
                    _flashSaleSeconds.value = 24 * 3600
                }
            }
        }
    }

    // Navigation
    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            screenStack.add(screen)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (screenStack.size > 1) {
            screenStack.removeAt(screenStack.size - 1)
            _currentScreen.value = screenStack.last()
            return true
        }
        return false
    }

    fun openProductDetail(product: Product) {
        _selectedProduct.value = product
        navigateTo(Screen.PRODUCT_DETAIL)
    }

    fun openOrderTracking(order: Order) {
        _selectedOrder.value = order
        navigateTo(Screen.ORDER_TRACKING)
    }

    fun selectCategory(category: ProductCategory) {
        _selectedCategory.value = category
        navigateTo(Screen.PRODUCTS_CATALOG)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSortType(sort: ProductSortType) {
        _sortType.value = sort
    }

    fun toggleMallFilter() {
        _mallOnlyFilter.value = !_mallOnlyFilter.value
    }

    // Cart actions
    fun addToCart(product: Product, quantity: Int = 1, color: String = "Default") {
        repository.addToCart(product, quantity, color)
        showNotification("Added \"${product.title.take(24)}...\" to cart!")
    }

    fun buyNow(product: Product, quantity: Int = 1, color: String = "Default") {
        repository.addToCart(product, quantity, color)
        navigateTo(Screen.CHECKOUT)
    }

    fun updateCartQuantity(productId: String, color: String, quantity: Int) {
        repository.updateCartItemQuantity(productId, color, quantity)
    }

    fun removeCartItem(productId: String, color: String) {
        repository.removeCartItem(productId, color)
        showNotification("Item removed from cart")
    }

    fun toggleCartItemSelection(productId: String, color: String) {
        repository.toggleCartItemSelection(productId, color)
    }

    fun toggleSelectAll(select: Boolean) {
        repository.selectAllCartItems(select)
    }

    // Wishlist
    fun toggleWishlist(productId: String) {
        val isWishlisted = repository.wishlistProductIds.value.contains(productId)
        repository.toggleWishlist(productId)
        if (isWishlisted) {
            showNotification("Removed from wishlist")
        } else {
            showNotification("Added to wishlist ❤️")
        }
    }

    // Coupon
    fun applyCoupon(code: String) {
        val result = repository.applyCoupon(code)
        showNotification(result.second, isError = !result.first)
    }

    fun removeCoupon() {
        repository.removeCoupon()
        showNotification("Coupon removed")
    }

    // Checkout
    fun setPaymentMethod(method: PaymentMethod) {
        _selectedPaymentMethod.value = method
    }

    fun updateAddress(address: DeliveryAddress) {
        _deliveryAddress.value = address
        repository.updateUserAddress(address)
        showNotification("Delivery address updated!")
    }

    fun placeOrder() {
        val selectedItems = repository.cartItems.value.filter { it.isSelected }
        if (selectedItems.isEmpty()) {
            showNotification("Please select items in your cart to checkout", isError = true)
            return
        }

        val order = repository.placeOrder(
            items = selectedItems,
            deliveryAddress = _deliveryAddress.value,
            paymentMethod = _selectedPaymentMethod.value
        )
        _lastPlacedOrder.value = order
        _selectedOrder.value = order
        navigateTo(Screen.ORDER_SUCCESS)
    }

    // Admin
    fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        repository.updateOrderStatus(orderId, newStatus)
        // Refresh selected order if looking at tracking
        val updated = repository.orders.value.find { it.id == orderId }
        if (updated != null && _selectedOrder.value?.id == orderId) {
            _selectedOrder.value = updated
        }
        showNotification("Order status updated to: ${newStatus.title}")
    }

    fun addNewProduct(product: Product) {
        repository.addProduct(product)
        showNotification("New product \"${product.title.take(20)}\" created!")
    }

    fun deleteProduct(productId: String) {
        repository.deleteProduct(productId)
        showNotification("Product deleted successfully")
    }

    fun toggleUrduLanguage() {
        repository.toggleUrduLanguage()
    }

    fun toggleAdminMode() {
        repository.toggleAdminMode()
        if (repository.isAdminMode.value) {
            navigateTo(Screen.ADMIN_DASHBOARD)
        } else {
            navigateTo(Screen.HOME)
        }
    }

    fun clearNotification() {
        _notification.value = null
    }

    private fun showNotification(msg: String, isError: Boolean = false) {
        _notification.value = UiNotification(message = msg, isError = isError)
    }
}
