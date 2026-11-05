package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ProductSortType
import com.example.model.ProductCategory
import com.example.ui.components.ProductCard
import com.example.ui.theme.*
import com.example.viewmodel.ShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    viewModel: ShopViewModel,
    modifier: Modifier = Modifier
) {
    val products by viewModel.filteredProducts.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val sortType by viewModel.sortType.collectAsState()
    val mallOnly by viewModel.mallOnlyFilter.collectAsState()
    val wishlistIds by viewModel.wishlistProductIds.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isUrdu by viewModel.isUrdu.collectAsState()

    var showSortMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NeutralLightBg)
    ) {
        // Filter bar: Category pills
        Surface(
            color = Color.White,
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                // Category tabs
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ProductCategory.values()) { category ->
                        val isSelected = category == selectedCategory
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectCategory(category) },
                            label = {
                                Text(
                                    text = if (isUrdu) category.urduName else category.displayName,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TaimoorOrange,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFFF1F3F5),
                                labelColor = TextPrimary
                            ),
                            border = null,
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("catalog_cat_chip_${category.name}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Secondary row: Sort dropdown & Mall filter
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sort button with Dropdown
                    Box {
                        TextButton(
                            onClick = { showSortMenu = true },
                            colors = ButtonDefaults.textButtonColors(contentColor = TextPrimary),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("sort_dropdown_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sort,
                                contentDescription = null,
                                tint = TaimoorOrange,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = sortType.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            ProductSortType.values().forEach { sort ->
                                DropdownMenuItem(
                                    text = { Text(sort.title, fontSize = 13.sp) },
                                    onClick = {
                                        viewModel.setSortType(sort)
                                        showSortMenu = false
                                    },
                                    leadingIcon = {
                                        if (sort == sortType) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = TaimoorOrange)
                                        }
                                    }
                                )
                            }
                        }
                    }

                    // Taimoor Mall Filter Chip
                    FilterChip(
                        selected = mallOnly,
                        onClick = { viewModel.toggleMallFilter() },
                        label = {
                            Text(
                                text = "Taimoor Mall",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TaimoorNavy,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = TaimoorGold
                        ),
                        modifier = Modifier.height(32.dp)
                    )
                }
            }
        }

        // Active search or category banner
        if (searchQuery.isNotBlank() || selectedCategory != ProductCategory.ALL) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (searchQuery.isNotBlank()) "Results for \"$searchQuery\" (${products.size})"
                           else "${selectedCategory.displayName} (${products.size})",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )

                if (searchQuery.isNotBlank()) {
                    TextButton(
                        onClick = { viewModel.setSearchQuery("") },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Clear filter", fontSize = 11.sp, color = TaimoorOrange)
                    }
                }
            }
        }

        // Grid of products or Empty state
        if (products.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No matching products found",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Try searching with broader terms or clear your active category filters.",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            viewModel.setSearchQuery("")
                            viewModel.selectCategory(ProductCategory.ALL)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TaimoorOrange)
                    ) {
                        Text("Reset All Filters")
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("catalog_products_grid"),
                contentPadding = PaddingValues(start = 10.dp, end = 10.dp, bottom = 16.dp, top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(products) { product ->
                    ProductCard(
                        product = product,
                        isWishlisted = wishlistIds.contains(product.id),
                        onProductClick = { viewModel.openProductDetail(product) },
                        onAddToCartClick = { viewModel.addToCart(product) },
                        onWishlistToggle = { viewModel.toggleWishlist(product.id) }
                    )
                }
            }
        }
    }
}
