package com.example.campusmarket.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campusmarket.R
import com.example.campusmarket.adapter.ProductAdapter
import com.example.campusmarket.databinding.ActivityHomeBinding
import com.example.campusmarket.viewmodel.HomeViewModel
import com.example.campusmarket.repository.ListingRepository
import com.example.campusmarket.model.Category
import com.example.campusmarket.model.Product
import com.example.campusmarket.notifications.NotificationHelper
import com.example.campusmarket.offline.NetworkMonitor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * HomeActivity displays the main CampusMarket marketplace.
 *
 * Products are retrieved from the EscuelaJS REST API and
 * displayed using RecyclerViews.
 */
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private lateinit var productAdapter: ProductAdapter

    private val homeViewModel: HomeViewModel by viewModels()

    private var allProducts = emptyList<Product>()
    private var apiProducts = emptyList<Product>()
    private var listingProducts = emptyList<Product>()
    private val listingRepository = ListingRepository()
    private var selectedCategory = "All"
    private lateinit var networkMonitor: NetworkMonitor
    private var wasOffline = false
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            Toast.makeText(
                this,
                if (granted) R.string.notifications_enabled else R.string.notifications_permission_denied,
                Toast.LENGTH_LONG
            ).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityHomeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        Timber.d("HomeActivity started")

        setupRecyclerViews()
        setupCategorySelection()
        setupNavigation()
        setupHeaderButtons()
        setupSearch()
        observeProducts()
        setupOfflineAndNotifications()

        homeViewModel.loadProducts()
    }

    override fun onResume() {
        super.onResume()
        loadMarketplaceListings()
    }

    override fun onDestroy() {
        if (::networkMonitor.isInitialized) networkMonitor.stop()
        super.onDestroy()
    }

    /**
     * Configures the product RecyclerViews.
     */
    private fun setupRecyclerViews() {

        productAdapter =
            ProductAdapter { product ->

                Timber.d(
                    "Opening Product Details: ${product.title}"
                )

                val intent =
                    android.content.Intent(
                        this,
                        ProductDetailsActivity::class.java
                    )

                intent.putExtra(
                    ProductDetailsActivity.EXTRA_PRODUCT,
                    product
                )

                startActivity(intent)
            }

        binding.recommendedRecyclerView.apply {

            layoutManager =
                GridLayoutManager(
                    this@HomeActivity,
                    2
                )

            adapter = productAdapter

            setHasFixedSize(false)
        }

        binding.recentRecyclerView.apply {

            layoutManager =
                LinearLayoutManager(
                    this@HomeActivity
                )

            adapter = productAdapter

            setHasFixedSize(false)
        }

        Timber.d("Product RecyclerViews configured")
    }

    /**
     * Observes products retrieved from the API.
     */
    private fun observeProducts() {

        homeViewModel.products.observe(
            this
        ) { products ->

            if (products.isNotEmpty()) {

                apiProducts = products
                combineProducts()
                applyFilters()

                Timber.d(
                    "Displaying ${products.size} products"
                )
            }
        }

        homeViewModel.error.observe(
            this
        ) { errorMessage ->

            if (!errorMessage.isNullOrEmpty()) {

                Toast.makeText(
                    this,
                    errorMessage,
                    Toast.LENGTH_LONG
                ).show()

                Timber.e(
                    "Product loading error: $errorMessage"
                )
            }
        }

        homeViewModel.usingOfflineCache.observe(this) { fromCache ->
            binding.tvSyncStatus.visibility = if (fromCache) android.view.View.VISIBLE else android.view.View.GONE
            if (fromCache) binding.tvSyncStatus.setText(R.string.offline_catalogue_message)
        }
    }

    /**
     * Configures the category chips.
     */
    private fun setupCategorySelection() {

        binding.categoryChipGroup
            .setOnCheckedStateChangeListener {
                    _, checkedIds ->

                if (checkedIds.isNotEmpty()) {

                    val selectedId =
                        checkedIds.first()

                    selectedCategory =
                        when (selectedId) {

                            R.id.chipAll ->
                                "All"

                            R.id.chipBooks ->
                                "Books"

                            R.id.chipElectronics ->
                                "Electronics"

                            R.id.chipClothing ->
                                "Clothing"

                            else ->
                                "Unknown"
                        }

                    Timber.d(
                        "Selected category: $selectedCategory"
                    )
                    applyFilters()
                }
            }
    }

    /**
     * Configures the bottom navigation.
     */
    private fun setupNavigation() {

        binding.bottomNavigation.selectedItemId =
            R.id.navigation_home

        binding.bottomNavigation
            .setOnItemSelectedListener { item ->

                when (item.itemId) {

                    R.id.navigation_home -> {

                        Timber.d(
                            "Home navigation selected"
                        )

                        true
                    }

                    R.id.navigation_favourites -> {
                        Timber.d("Opening Favourites screen")

                        val intent =
                            android.content.Intent(
                                this,
                                FavouritesActivity::class.java
                            )

                        startActivity(intent)

                        false
                    }

                    R.id.navigation_sell -> {

                        Timber.d(
                            "Opening Sell Item screen"
                        )

                        val intent =
                            android.content.Intent(
                                this,
                                SellItemActivity::class.java
                            )

                        startActivity(intent)

                        false
                    }

                    R.id.navigation_messages -> {
                        Timber.d("Opening Messages screen")

                        val intent =
                            android.content.Intent(
                                this,
                                MessagesActivity::class.java
                            )

                        startActivity(intent)

                        false
                    }

                    R.id.navigation_profile -> {
                        Timber.d("Opening Profile screen")

                        val intent =
                            android.content.Intent(
                                this,
                                ProfileActivity::class.java
                            )

                        startActivity(intent)

                        false
                    }

                    else -> false
                }
            }
    }

    /**
     * Configures the Home screen header buttons.
     */
    private fun setupHeaderButtons() {

        binding.btnMenu.setOnClickListener {
            val menu = androidx.appcompat.widget.PopupMenu(this, binding.btnMenu)
            menu.menu.add("My Listings")
            menu.menu.add("Settings")
            menu.menu.add("Help & Support")
            menu.setOnMenuItemClickListener { item ->
                when (item.title.toString()) {
                    "My Listings" -> startActivity(android.content.Intent(this, MyListingsActivity::class.java))
                    "Settings" -> startActivity(android.content.Intent(this, SettingsActivity::class.java))
                    "Help & Support" -> startActivity(android.content.Intent(this, HelpSupportActivity::class.java))
                }
                true
            }
            menu.show()
        }

        binding.btnNotifications.setOnClickListener {
            requestNotificationPermission()
            NotificationHelper.showMessage(
                this,
                getString(R.string.notifications_ready_title),
                getString(R.string.notifications_ready_message),
                1001
            )
        }
    }

    private fun setupOfflineAndNotifications() {
        networkMonitor = NetworkMonitor(this) { online ->
            runOnUiThread {
                if (!online) {
                    wasOffline = true
                    binding.tvSyncStatus.visibility = android.view.View.VISIBLE
                    binding.tvSyncStatus.setText(R.string.offline_catalogue_message)
                } else if (wasOffline) {
                    wasOffline = false
                    binding.tvSyncStatus.visibility = android.view.View.VISIBLE
                    binding.tvSyncStatus.setText(R.string.syncing_message)
                    homeViewModel.loadProducts()
                    loadMarketplaceListings()
                    binding.tvSyncStatus.postDelayed({
                        binding.tvSyncStatus.visibility = android.view.View.GONE
                    }, 2500)
                }
            }
        }
        networkMonitor.start()
        requestNotificationPermission()

        FirebaseMessaging.getInstance().subscribeToTopic("campus_market_updates")
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            FirebaseAuth.getInstance().currentUser?.uid?.let { uid ->
                FirebaseFirestore.getInstance().collection("users").document(uid)
                    .set(mapOf("fcmToken" to token), SetOptions.merge())
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun loadMarketplaceListings() {
        lifecycleScope.launch {
            try {
                listingProducts = listingRepository.getActiveListings().map { listing ->
                    Product(
                        id = listing.id.hashCode(), title = listing.title,
                        price = listing.price, description = listing.description,
                        images = listOfNotNull(listing.imageUrl.takeIf { it.isNotBlank() }),
                        category = Category(name = listing.category), listingId = listing.id,
                        sellerId = listing.sellerId, sellerName = listing.sellerName,
                        condition = listing.condition, location = listing.location
                    )
                }
                combineProducts()
                applyFilters()
            } catch (exception: Exception) {
                Timber.e(exception, "Unable to load Firestore listings")
            }
        }
    }

    private fun combineProducts() {
        allProducts = listingProducts + apiProducts
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = applyFilters()
            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    private fun applyFilters() {
        productAdapter.submitList(
            com.example.campusmarket.utils.ProductFilter.filter(
                allProducts,
                binding.etSearch.text?.toString().orEmpty(),
                selectedCategory
            )
        )
    }
}
