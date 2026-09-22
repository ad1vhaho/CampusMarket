package com.example.campusmarket.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.campusmarket.R
import com.example.campusmarket.databinding.ActivityProductDetailsBinding
import com.example.campusmarket.model.Favourite
import com.example.campusmarket.model.Product
import com.example.campusmarket.repository.FavouriteRepository
import com.example.campusmarket.repository.MessageRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale

/**
 * ProductDetailsActivity displays detailed information about
 * a selected CampusMarket product.
 *
 * Users can view product information, add the product to
 * favourites and contact the seller.
 */
class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailsBinding

    private val favouriteRepository =
        FavouriteRepository()

    private val messageRepository =
        MessageRepository()

    private val firebaseAuth =
        FirebaseAuth.getInstance()

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityProductDetailsBinding.inflate(
                layoutInflater
            )

        setContentView(binding.root)

        Timber.d(
            "ProductDetailsActivity started"
        )

        val product =
            getProductFromIntent()

        if (product == null) {

            Timber.e(
                "No product was supplied to ProductDetailsActivity"
            )

            Toast.makeText(
                this,
                "Product could not be loaded.",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        displayProduct(product)
        setupButtons(product)
    }

    private fun getProductFromIntent(): Product? {

        return intent.getSerializableExtra(
            EXTRA_PRODUCT
        ) as? Product
    }

    private fun displayProduct(
        product: Product
    ) {

        binding.tvProductDetailsPrice.text =
            String.format(
                Locale.getDefault(),
                "R%.0f",
                product.price
            )

        binding.tvProductDetailsTitle.text =
            product.title

        binding.tvProductDescription.text =
            product.description.ifBlank {
                "No description was provided for this item."
            }

        binding.tvProductLocation.text = product.location ?: "Hatfield"

        binding.tvSellerName.text = product.sellerName?.ifBlank { "Catalogue seller" } ?: "Catalogue seller"

        binding.tvSellerRating.text =
            "4.8 ★"

        binding.chipProductCondition.text = product.condition ?: "Good condition"

        val imageUrl =
            product.images.firstOrNull()

        Glide.with(this)
            .load(imageUrl)
            .placeholder(
                R.drawable.ic_campus_market_logo
            )
            .error(
                R.drawable.ic_campus_market_logo
            )
            .into(binding.ivProductDetails)

        Timber.d(
            "Displaying product: ${product.title}"
        )
    }

    private fun setupButtons(
        product: Product
    ) {

        binding.btnProductBack.setOnClickListener {
            finish()
        }

        binding.btnProductFavourite.setOnClickListener {
            addProductToFavourites(product)
        }

        binding.btnAddFavourite.setOnClickListener {
            addProductToFavourites(product)
        }

        binding.btnMessageSeller.setOnClickListener {
            openSellerConversation(product)
        }
    }

    private fun addProductToFavourites(
        product: Product
    ) {

        val currentUser =
            firebaseAuth.currentUser

        if (currentUser == null) {

            Toast.makeText(
                this,
                "Please log in before adding favourites.",
                Toast.LENGTH_LONG
            ).show()

            Timber.w(
                "Favourite attempted without authenticated user"
            )

            return
        }

        lifecycleScope.launch {

            try {

                val favourite =
                    Favourite(
                        userId =
                            currentUser.uid,
                        productId = product.listingId?.ifBlank { product.id.toString() } ?: product.id.toString(),
                        title =
                            product.title,
                        price =
                            product.price,
                        imageUrl =
                            product.images
                                .firstOrNull()
                                .orEmpty(),
                        condition =
                            "Good condition"
                    )

                favouriteRepository
                    .addFavourite(
                        favourite
                    )

                Toast.makeText(
                    this@ProductDetailsActivity,
                    "Added to Favourites.",
                    Toast.LENGTH_SHORT
                ).show()

                Timber.i(
                    "Product added to favourites: ${product.id}"
                )

            } catch (exception: Exception) {

                Timber.e(
                    exception,
                    "Failed to add product to favourites"
                )

                Toast.makeText(
                    this@ProductDetailsActivity,
                    "Unable to add favourite. Please try again.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun openSellerConversation(
        product: Product
    ) {

        val currentUser =
            firebaseAuth.currentUser

        if (currentUser == null) {

            Toast.makeText(
                this,
                "Please log in before messaging the seller.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        lifecycleScope.launch {

            try {

                val sellerId = product.sellerId.orEmpty()
                if (sellerId.isBlank()) {
                    Toast.makeText(this@ProductDetailsActivity, "Messaging is available for student-created listings.", Toast.LENGTH_LONG).show()
                    return@launch
                }
                if (sellerId == currentUser.uid) {
                    Toast.makeText(this@ProductDetailsActivity, "This is your own listing.", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                val sellerName = product.sellerName?.ifBlank { "CampusMarket Seller" } ?: "CampusMarket Seller"

                val conversationId =
                    messageRepository
                        .getOrCreateConversation(
                            currentUserId =
                                currentUser.uid,
                            currentUserName =
                                currentUser.email
                                    ?: "CampusMarket User",
                            otherUserId =
                                sellerId,
                            otherUserName = sellerName
                        )

                val intent =
                    Intent(
                        this@ProductDetailsActivity,
                        ChatActivity::class.java
                    )

                intent.putExtra(
                    ChatActivity.EXTRA_CONVERSATION_ID,
                    conversationId
                )

                intent.putExtra(
                    ChatActivity.EXTRA_OTHER_USER_ID,
                    sellerId
                )

                intent.putExtra(
                    ChatActivity.EXTRA_OTHER_USER_NAME,
                    sellerName
                )

                startActivity(intent)

                Timber.d(
                    "Seller conversation opened: $conversationId"
                )

            } catch (exception: Exception) {

                Timber.e(
                    exception,
                    "Failed to open seller conversation"
                )

                Toast.makeText(
                    this@ProductDetailsActivity,
                    "Unable to open messages. Please try again.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    companion object {

        const val EXTRA_PRODUCT =
            "extra_product"
    }
}
