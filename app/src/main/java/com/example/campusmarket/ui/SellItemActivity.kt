package com.example.campusmarket.ui

import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.campusmarket.R
import com.example.campusmarket.databinding.ActivitySellItemBinding
import com.example.campusmarket.model.Listing
import com.example.campusmarket.repository.ListingRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Activity used by students to create a new CampusMarket listing.
 *
 * The activity collects listing information, allows the user
 * to select a photo and saves the listing using Firebase.
 */
class SellItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySellItemBinding

    private val listingRepository =
        ListingRepository()

    private val firebaseAuth =
        FirebaseAuth.getInstance()

    private var selectedImageUri: Uri? = null

    /**
     * Opens the device image picker.
     */
    private val imagePicker =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->

            if (uri != null) {

                selectedImageUri = uri

                Glide.with(this)
                    .load(uri)
                    .into(binding.ivSelectedPhoto)

                binding.ivSelectedPhoto.visibility =
                    android.view.View.VISIBLE

                binding.photoUploadMessage.visibility =
                    android.view.View.GONE

                Timber.d(
                    "Listing image selected"
                )
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            ActivitySellItemBinding.inflate(layoutInflater)

        setContentView(binding.root)

        Timber.d("SellItemActivity started")

        setupToolbar()
        setupCategoryDropdown()
        setupConditionDropdown()
        setupPhotoUpload()
        setupButtons()
    }

    /**
     * Configures the toolbar back button.
     */
    private fun setupToolbar() {

        binding.sellToolbar.setNavigationOnClickListener {

            Timber.d(
                "Sell Item back button clicked"
            )

            finish()
        }
    }

    /**
     * Configures the category dropdown.
     */
    private fun setupCategoryDropdown() {

        val categories = arrayOf(
            "Books",
            "Electronics",
            "Clothing",
            "Furniture",
            "Stationery",
            "Other"
        )

        val adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                categories
            )

        binding.actCategory.setAdapter(adapter)

        binding.actCategory.setOnItemClickListener {
                _, _, position, _ ->

            Timber.d(
                "Category selected: ${categories[position]}"
            )
        }
    }

    /**
     * Configures the condition dropdown.
     */
    private fun setupConditionDropdown() {

        val conditions = arrayOf(
            "New",
            "Like new",
            "Good condition",
            "Fair condition",
            "Used"
        )

        val adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                conditions
            )

        binding.actCondition.setAdapter(adapter)

        binding.actCondition.setOnItemClickListener {
                _, _, position, _ ->

            Timber.d(
                "Condition selected: ${conditions[position]}"
            )
        }
    }

    /**
     * Configures the photo upload area.
     */
    private fun setupPhotoUpload() {

        binding.photoUploadContainer.setOnClickListener {

            Timber.d(
                "Opening image picker"
            )

            imagePicker.launch("image/*")
        }
    }

    /**
     * Configures Publish Listing and Save Draft buttons.
     */
    private fun setupButtons() {

        binding.btnPublishListing.setOnClickListener {

            if (validateListing()) {

                publishListing()
            }
        }

        binding.btnSaveDraft.setOnClickListener {
            saveDraft()
        }
    }

    /**
     * Validates the listing fields.
     *
     * @return true when all required fields are valid.
     */
    private fun validateListing(): Boolean {

        val title =
            binding.etTitle.text
                ?.toString()
                ?.trim()
                .orEmpty()

        val category =
            binding.actCategory.text
                ?.toString()
                ?.trim()
                .orEmpty()

        val condition =
            binding.actCondition.text
                ?.toString()
                ?.trim()
                .orEmpty()

        val priceText =
            binding.etPrice.text
                ?.toString()
                ?.trim()
                .orEmpty()

        val description =
            binding.etDescription.text
                ?.toString()
                ?.trim()
                .orEmpty()

        binding.titleInputLayout.error = null
        binding.categoryInputLayout.error = null
        binding.conditionInputLayout.error = null
        binding.priceInputLayout.error = null
        binding.descriptionInputLayout.error = null

        if (title.isEmpty()) {

            binding.titleInputLayout.error =
                "Enter a title"

            binding.etTitle.requestFocus()

            return false
        }

        if (category.isEmpty()) {

            binding.categoryInputLayout.error =
                "Select a category"

            binding.actCategory.requestFocus()

            return false
        }

        if (condition.isEmpty()) {

            binding.conditionInputLayout.error =
                "Select the condition"

            binding.actCondition.requestFocus()

            return false
        }

        val price =
            priceText.toDoubleOrNull()

        if (price == null || price <= 0) {

            binding.priceInputLayout.error =
                "Enter a valid price"

            binding.etPrice.requestFocus()

            return false
        }

        if (description.isEmpty()) {

            binding.descriptionInputLayout.error =
                "Enter a description"

            binding.etDescription.requestFocus()

            return false
        }

        return true
    }

    /**
     * Stores the listing in Firebase Firestore. The selected
     * photo is preview-only because Storage is not required.
     */
    private fun publishListing() {

        val currentUser =
            firebaseAuth.currentUser

        if (currentUser == null) {

            Toast.makeText(
                this,
                "Please log in before publishing a listing.",
                Toast.LENGTH_LONG
            ).show()

            Timber.w(
                "Publish attempted without authenticated user"
            )

            return
        }

        setLoading(true)

        lifecycleScope.launch {

            try {

                Timber.d(
                    "Starting listing publication"
                )

                val title =
                    binding.etTitle.text
                        ?.toString()
                        ?.trim()
                        .orEmpty()

                val category =
                    binding.actCategory.text
                        ?.toString()
                        ?.trim()
                        .orEmpty()

                val condition =
                    binding.actCondition.text
                        ?.toString()
                        ?.trim()
                        .orEmpty()

                val price =
                    binding.etPrice.text
                        ?.toString()
                        ?.trim()
                        ?.toDoubleOrNull()
                        ?: 0.0

                val description =
                    binding.etDescription.text
                        ?.toString()
                        ?.trim()
                        .orEmpty()

                val listing =
                    Listing(
                        sellerId =
                            currentUser.uid,
                        sellerName =
                            currentUser.email
                                ?: "CampusMarket Seller",
                        title =
                            title,
                        category =
                            category,
                        condition =
                            condition,
                        price =
                            price,
                        description =
                            description,
                        imageUrl = "",
                        location =
                            "Hatfield",
                        status =
                            "Active"
                    )

                listingRepository.saveListing(
                    listing
                )

                setLoading(false)

                Toast.makeText(
                    this@SellItemActivity,
                    "Listing published successfully.",
                    Toast.LENGTH_LONG
                ).show()

                Timber.i(
                    "Listing published successfully"
                )

                finish()

            } catch (exception: Exception) {

                setLoading(false)

                Timber.e(
                    exception,
                    "Listing publication failed"
                )

                Toast.makeText(
                    this@SellItemActivity,
                    "Unable to publish listing. Please try again.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    /**
     * Saves the current listing as a draft.
     *
     * Drafts require a title and persist to the My Listings
     * Drafts tab. Other fields can be completed later.
     */
    private fun saveDraft() {
        val user = firebaseAuth.currentUser
        if (user == null) {
            Toast.makeText(this, "Please log in before saving a draft.", Toast.LENGTH_LONG).show()
            return
        }
        val title = binding.etTitle.text?.toString()?.trim().orEmpty()
        if (title.isBlank()) {
            binding.titleInputLayout.error = "Enter a title before saving"
            return
        }
        setLoading(true)
        lifecycleScope.launch {
            try {
                val listing = Listing(
                    sellerId = user.uid,
                    sellerName = user.displayName ?: user.email ?: "CampusMarket Seller",
                    title = title,
                    category = binding.actCategory.text?.toString()?.trim().orEmpty(),
                    condition = binding.actCondition.text?.toString()?.trim().orEmpty(),
                    price = binding.etPrice.text?.toString()?.trim()?.toDoubleOrNull() ?: 0.0,
                    description = binding.etDescription.text?.toString()?.trim().orEmpty(),
                    imageUrl = "",
                    location = "Hatfield",
                    status = "Draft"
                )
                listingRepository.saveListing(listing)
                Toast.makeText(this@SellItemActivity, "Draft saved in My Listings.", Toast.LENGTH_LONG).show()
                finish()
            } catch (exception: Exception) {
                Timber.e(exception, "Draft save failed")
                Toast.makeText(this@SellItemActivity, "Unable to save draft.", Toast.LENGTH_LONG).show()
            } finally {
                setLoading(false)
            }
        }
    }

    /**
     * Updates the Publish button while a listing is uploading.
     */
    private fun setLoading(
        isLoading: Boolean
    ) {

        binding.btnPublishListing.isEnabled =
            !isLoading

        binding.btnSaveDraft.isEnabled =
            !isLoading

        binding.btnPublishListing.text =
            if (isLoading) {
                "Publishing..."
            } else {
                "Publish Listing"
            }

        binding.btnSaveDraft.text = if (isLoading) "Saving..." else "Save Draft"
    }
}
