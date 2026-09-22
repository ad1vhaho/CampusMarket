package com.example.campusmarket.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campusmarket.adapter.MyListingAdapter
import com.example.campusmarket.databinding.ActivityMyListingsBinding
import com.example.campusmarket.model.Listing
import com.example.campusmarket.repository.ListingRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Displays listings created by the current CampusMarket user.
 *
 * Listings are separated into Active, Sold and Drafts tabs.
 */
class MyListingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyListingsBinding

    private lateinit var listingAdapter: MyListingAdapter

    private val listingRepository =
        ListingRepository()

    private val firebaseAuth =
        FirebaseAuth.getInstance()

    private var allListings =
        emptyList<Listing>()

    private var selectedStatus =
        "Active"

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityMyListingsBinding.inflate(
                layoutInflater
            )

        setContentView(binding.root)

        Timber.d(
            "MyListingsActivity started"
        )

        setupToolbar()
        setupTabs()
        setupRecyclerView()
        loadListings()
    }

    override fun onResume() {
        super.onResume()

        if (::binding.isInitialized) {
            loadListings()
        }
    }

    private fun setupToolbar() {

        binding.myListingsToolbar
            .setNavigationOnClickListener {
                finish()
            }
    }

    private fun setupTabs() {

        binding.listingTabs.addTab(
            binding.listingTabs
                .newTab()
                .setText("Active")
        )

        binding.listingTabs.addTab(
            binding.listingTabs
                .newTab()
                .setText("Sold")
        )

        binding.listingTabs.addTab(
            binding.listingTabs
                .newTab()
                .setText("Drafts")
        )

        binding.listingTabs.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {

                override fun onTabSelected(
                    tab: TabLayout.Tab
                ) {

                    selectedStatus =
                        when (tab.position) {

                            0 -> "Active"

                            1 -> "Sold"

                            else -> "Draft"
                        }

                    Timber.d(
                        "Selected listing tab: $selectedStatus"
                    )

                    displaySelectedListings()
                }

                override fun onTabUnselected(
                    tab: TabLayout.Tab
                ) {
                }

                override fun onTabReselected(
                    tab: TabLayout.Tab
                ) {
                    displaySelectedListings()
                }
            }
        )
    }

    private fun setupRecyclerView() {

        listingAdapter =
            MyListingAdapter(
                emptyList(),

                onDeleteListing = { listing ->
                    confirmDeleteListing(
                        listing
                    )
                },

                onMarkSold = { listing ->
                    markListingAsSold(
                        listing
                    )
                }
            )

        binding.recyclerMyListings.apply {

            layoutManager =
                LinearLayoutManager(
                    this@MyListingsActivity
                )

            adapter =
                listingAdapter
        }
    }

    private fun loadListings() {

        val currentUser =
            firebaseAuth.currentUser

        if (currentUser == null) {

            Toast.makeText(
                this,
                "Please log in to view your listings.",
                Toast.LENGTH_LONG
            ).show()

            finish()
            return
        }

        binding.progressMyListings.visibility =
            View.VISIBLE

        lifecycleScope.launch {

            try {

                allListings =
                    listingRepository
                        .getUserListings(
                            currentUser.uid
                        )

                displaySelectedListings()

                Timber.d(
                    "Loaded ${allListings.size} user listings"
                )

            } catch (exception: Exception) {

                Timber.e(
                    exception,
                    "Failed to load user listings"
                )

                Toast.makeText(
                    this@MyListingsActivity,
                    "Unable to load your listings.",
                    Toast.LENGTH_LONG
                ).show()
            }

            binding.progressMyListings.visibility =
                View.GONE
        }
    }

    private fun displaySelectedListings() {

        val filteredListings =
            allListings.filter {
                it.status.equals(
                    selectedStatus,
                    ignoreCase = true
                )
            }

        listingAdapter.updateListings(
            filteredListings
        )

        if (filteredListings.isEmpty()) {

            binding.tvEmptyListings.visibility =
                View.VISIBLE

            binding.recyclerMyListings.visibility =
                View.GONE

        } else {

            binding.tvEmptyListings.visibility =
                View.GONE

            binding.recyclerMyListings.visibility =
                View.VISIBLE
        }
    }

    private fun confirmDeleteListing(
        listing: Listing
    ) {

        AlertDialog.Builder(this)
            .setTitle("Delete Listing")
            .setMessage(
                "Are you sure you want to delete \"${listing.title}\"?"
            )
            .setNegativeButton(
                "Cancel",
                null
            )
            .setPositiveButton(
                "Delete"
            ) { _, _ ->

                deleteListing(
                    listing
                )
            }
            .show()
    }

    private fun deleteListing(
        listing: Listing
    ) {

        lifecycleScope.launch {

            try {

                listingRepository
                    .deleteListing(
                        listing.id
                    )

                Toast.makeText(
                    this@MyListingsActivity,
                    "Listing deleted.",
                    Toast.LENGTH_SHORT
                ).show()

                loadListings()

                Timber.d(
                    "Listing deleted: ${listing.id}"
                )

            } catch (exception: Exception) {

                Timber.e(
                    exception,
                    "Failed to delete listing"
                )

                Toast.makeText(
                    this@MyListingsActivity,
                    "Unable to delete listing.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun markListingAsSold(
        listing: Listing
    ) {

        lifecycleScope.launch {

            try {

                listingRepository
                    .updateListingStatus(
                        listing.id,
                        "Sold"
                    )

                Toast.makeText(
                    this@MyListingsActivity,
                    "Listing marked as sold.",
                    Toast.LENGTH_SHORT
                ).show()

                loadListings()

                Timber.d(
                    "Listing marked as sold: ${listing.id}"
                )

            } catch (exception: Exception) {

                Timber.e(
                    exception,
                    "Failed to mark listing as sold"
                )

                Toast.makeText(
                    this@MyListingsActivity,
                    "Unable to update listing.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}