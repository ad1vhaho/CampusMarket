package com.example.campusmarket.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campusmarket.R
import com.example.campusmarket.databinding.ItemMyListingBinding
import com.example.campusmarket.model.Listing
import java.util.Locale

/**
 * RecyclerView adapter used to display listings created
 * by the current CampusMarket user.
 */
class MyListingAdapter(
    private var listings: List<Listing>,
    private val onDeleteListing: (Listing) -> Unit,
    private val onMarkSold: (Listing) -> Unit
) : RecyclerView.Adapter<MyListingAdapter.MyListingViewHolder>() {

    /**
     * Holds the views for one listing.
     */
    class MyListingViewHolder(
        val binding: ItemMyListingBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyListingViewHolder {

        val binding =
            ItemMyListingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return MyListingViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MyListingViewHolder,
        position: Int
    ) {

        val listing =
            listings[position]

        holder.binding.tvMyListingTitle.text =
            listing.title

        holder.binding.tvMyListingPrice.text =
            String.format(
                Locale.getDefault(),
                "R%.0f",
                listing.price
            )

        holder.binding.tvMyListingCondition.text =
            listing.condition

        holder.binding.tvMyListingStatus.text =
            listing.status

        Glide.with(
            holder.itemView.context
        )
            .load(listing.imageUrl)
            .placeholder(
                R.drawable.ic_campus_market_logo
            )
            .error(
                R.drawable.ic_campus_market_logo
            )
            .into(
                holder.binding.ivMyListingImage
            )

        holder.binding.btnMyListingMenu
            .setOnClickListener { view ->

                showListingMenu(
                    view,
                    listing
                )
            }
    }

    override fun getItemCount(): Int {
        return listings.size
    }

    /**
     * Updates the adapter with a new list of listings.
     */
    fun updateListings(
        newListings: List<Listing>
    ) {

        listings =
            newListings

        notifyDataSetChanged()
    }

    private fun showListingMenu(
        view: View,
        listing: Listing
    ) {

        val popupMenu =
            PopupMenu(
                view.context,
                view
            )

        popupMenu.menu.add(
            "Mark as Sold"
        )

        popupMenu.menu.add(
            "Delete"
        )

        popupMenu.setOnMenuItemClickListener { item ->

            when (item.title.toString()) {

                "Mark as Sold" -> {

                    onMarkSold(
                        listing
                    )

                    true
                }

                "Delete" -> {

                    onDeleteListing(
                        listing
                    )

                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }
}