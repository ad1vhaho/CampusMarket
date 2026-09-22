package com.example.campusmarket.repository

import com.example.campusmarket.model.Listing
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import timber.log.Timber

/**
 * Repository responsible for storing, retrieving and deleting
 * CampusMarket listings.
 */
class ListingRepository {

    private val firestore =
        FirebaseFirestore.getInstance()

    /**
     * Stores a listing in the Firestore listings collection.
     *
     * @param listing listing information to store.
     */
    suspend fun saveListing(
        listing: Listing
    ) {

        val documentReference =
            firestore
                .collection("listings")
                .document()

        val listingWithId =
            listing.copy(
                id = documentReference.id
            )

        documentReference
            .set(listingWithId)
            .await()

        Timber.d(
            "Listing saved with ID: ${documentReference.id}"
        )
    }

    /**
     * Retrieves all listings belonging to a specific user.
     *
     * @param userId Firebase user ID.
     * @return user's listings.
     */
    suspend fun getUserListings(
        userId: String
    ): List<Listing> {

        val snapshot =
            firestore
                .collection("listings")
                .whereEqualTo(
                    "sellerId",
                    userId
                )
                .get()
                .await()

        return snapshot.documents
            .mapNotNull {
                it.toObject(Listing::class.java)
            }
            .sortedByDescending {
                it.createdAt
            }
    }

    /**
     * Deletes a listing from Firestore.
     *
     * @param listingId Firestore listing ID.
     */
    suspend fun deleteListing(
        listingId: String
    ) {

        firestore
            .collection("listings")
            .document(listingId)
            .delete()
            .await()

        Timber.d(
            "Listing deleted: $listingId"
        )
    }

    suspend fun getActiveListings(): List<Listing> {
        val snapshot = firestore.collection("listings")
            .whereEqualTo("status", "Active")
            .get().await()
        return snapshot.documents.mapNotNull { it.toObject(Listing::class.java) }
            .sortedByDescending { it.createdAt }
    }

    /**
     * Updates the status of a listing.
     *
     * @param listingId Firestore listing ID.
     * @param status new listing status.
     */
    suspend fun updateListingStatus(
        listingId: String,
        status: String
    ) {

        firestore
            .collection("listings")
            .document(listingId)
            .update(
                "status",
                status
            )
            .await()

        Timber.d(
            "Listing status updated: $listingId -> $status"
        )
    }
}
