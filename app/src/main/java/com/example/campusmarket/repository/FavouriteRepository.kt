package com.example.campusmarket.repository

import com.example.campusmarket.model.Favourite
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import timber.log.Timber

/**
 * Repository responsible for adding, retrieving and removing
 * favourite products using Firebase Firestore.
 */
class FavouriteRepository {

    private val firestore =
        FirebaseFirestore.getInstance()

    /**
     * Checks whether a product has already been added
     * to a user's favourites.
     *
     * @param userId Firebase user ID.
     * @param productId product ID.
     * @return true when the product is already a favourite.
     */
    suspend fun isFavourite(
        userId: String,
        productId: String
    ): Boolean {

        val snapshot =
            firestore
                .collection("favourites")
                .whereEqualTo("userId", userId)
                .whereEqualTo("productId", productId)
                .limit(1)
                .get()
                .await()

        return !snapshot.isEmpty
    }

    /**
     * Adds a product to the user's favourites.
     *
     * @param favourite favourite information to save.
     */
    suspend fun addFavourite(
        favourite: Favourite
    ) {

        val alreadyFavourite =
            isFavourite(
                favourite.userId,
                favourite.productId
            )

        if (alreadyFavourite) {
            Timber.d(
                "Product is already a favourite: ${favourite.productId}"
            )
            return
        }

        val documentReference =
            firestore
                .collection("favourites")
                .document()

        val favouriteWithId =
            favourite.copy(
                id = documentReference.id
            )

        documentReference
            .set(favouriteWithId)
            .await()

        Timber.d(
            "Favourite added: ${documentReference.id}"
        )
    }

    /**
     * Retrieves all favourites belonging to a user.
     *
     * @param userId Firebase user ID.
     * @return list of favourites.
     */
    suspend fun getFavourites(
        userId: String
    ): List<Favourite> {

        val snapshot =
            firestore
                .collection("favourites")
                .whereEqualTo(
                    "userId",
                    userId
                )
                .get()
                .await()

        return snapshot.documents.mapNotNull {
            it.toObject(Favourite::class.java)
        }
    }

    /**
     * Removes a favourite from Firestore.
     *
     * @param favouriteId Firestore favourite document ID.
     */
    suspend fun removeFavourite(
        favouriteId: String
    ) {

        firestore
            .collection("favourites")
            .document(favouriteId)
            .delete()
            .await()

        Timber.d(
            "Favourite removed: $favouriteId"
        )
    }
}