package com.example.campusmarket.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campusmarket.adapter.FavouriteAdapter
import com.example.campusmarket.databinding.ActivityFavouritesBinding
import com.example.campusmarket.model.Favourite
import com.example.campusmarket.repository.FavouriteRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Displays the products saved by the current user as favourites.
 */
class FavouritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavouritesBinding

    private lateinit var favouriteAdapter: FavouriteAdapter

    private val favouriteRepository =
        FavouriteRepository()

    private val firebaseAuth =
        FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityFavouritesBinding.inflate(layoutInflater)

        setContentView(binding.root)

        Timber.d("FavouritesActivity started")

        setupRecyclerView()
        setupToolbar()
        loadFavourites()
    }

    private fun setupToolbar() {
        binding.favouritesToolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        favouriteAdapter =
            FavouriteAdapter(
                emptyList()
            ) { favourite ->
                removeFavourite(favourite)
            }

        binding.recyclerFavourites.apply {
            layoutManager =
                LinearLayoutManager(this@FavouritesActivity)

            adapter = favouriteAdapter
        }
    }

    private fun loadFavourites() {
        val currentUser =
            firebaseAuth.currentUser

        if (currentUser == null) {
            Toast.makeText(
                this,
                "Please log in to view your favourites.",
                Toast.LENGTH_LONG
            ).show()

            finish()
            return
        }

        binding.progressFavourites.visibility =
            View.VISIBLE

        lifecycleScope.launch {
            try {
                val favourites =
                    favouriteRepository.getFavourites(
                        currentUser.uid
                    )

                favouriteAdapter.updateFavourites(
                    favourites
                )

                updateEmptyState(favourites)

                Timber.d(
                    "Loaded ${favourites.size} favourites"
                )

            } catch (exception: Exception) {

                Timber.e(
                    exception,
                    "Failed to load favourites"
                )

                Toast.makeText(
                    this@FavouritesActivity,
                    "Unable to load favourites.",
                    Toast.LENGTH_LONG
                ).show()
            }

            binding.progressFavourites.visibility =
                View.GONE
        }
    }

    private fun removeFavourite(
        favourite: Favourite
    ) {
        lifecycleScope.launch {
            try {
                favouriteRepository.removeFavourite(
                    favourite.id
                )

                val currentUser =
                    firebaseAuth.currentUser

                if (currentUser != null) {
                    val favourites =
                        favouriteRepository.getFavourites(
                            currentUser.uid
                        )

                    favouriteAdapter.updateFavourites(
                        favourites
                    )

                    updateEmptyState(favourites)
                }

                Toast.makeText(
                    this@FavouritesActivity,
                    "Removed from Favourites.",
                    Toast.LENGTH_SHORT
                ).show()

            } catch (exception: Exception) {

                Timber.e(
                    exception,
                    "Failed to remove favourite"
                )

                Toast.makeText(
                    this@FavouritesActivity,
                    "Unable to remove favourite.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateEmptyState(
        favourites: List<Favourite>
    ) {
        if (favourites.isEmpty()) {
            binding.tvEmptyFavourites.visibility =
                View.VISIBLE

            binding.recyclerFavourites.visibility =
                View.GONE
        } else {
            binding.tvEmptyFavourites.visibility =
                View.GONE

            binding.recyclerFavourites.visibility =
                View.VISIBLE
        }
    }
}