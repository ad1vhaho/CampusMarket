package com.example.campusmarket.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campusmarket.R
import com.example.campusmarket.databinding.ItemFavouriteBinding
import com.example.campusmarket.model.Favourite
import java.util.Locale

/**
 * RecyclerView adapter used to display the user's favourite products.
 */
class FavouriteAdapter(
    private var favourites: List<Favourite>,
    private val onRemoveFavourite: (Favourite) -> Unit
) : RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder>() {

    /**
     * ViewHolder containing the views for one favourite item.
     */
    class FavouriteViewHolder(
        val binding: ItemFavouriteBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavouriteViewHolder {

        val binding =
            ItemFavouriteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return FavouriteViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: FavouriteViewHolder,
        position: Int
    ) {
        val favourite = favourites[position]

        holder.binding.tvFavouriteTitle.text =
            favourite.title

        holder.binding.tvFavouritePrice.text =
            String.format(
                Locale.getDefault(),
                "R%.0f",
                favourite.price
            )

        holder.binding.tvFavouriteCondition.text =
            favourite.condition

        Glide.with(holder.itemView.context)
            .load(favourite.imageUrl)
            .placeholder(R.drawable.ic_campus_market_logo)
            .error(R.drawable.ic_campus_market_logo)
            .into(holder.binding.ivFavouriteImage)

        holder.binding.btnRemoveFavourite.setOnClickListener {
            onRemoveFavourite(favourite)
        }
    }

    override fun getItemCount(): Int {
        return favourites.size
    }

    /**
     * Updates the adapter with a new list of favourites.
     */
    fun updateFavourites(
        newFavourites: List<Favourite>
    ) {
        favourites = newFavourites
        notifyDataSetChanged()
    }
}