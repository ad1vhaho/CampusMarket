package com.example.campusmarket.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campusmarket.R
import com.example.campusmarket.databinding.ItemProductBinding
import com.example.campusmarket.model.Product
import timber.log.Timber
import java.util.Locale

/**
 * RecyclerView adapter responsible for displaying CampusMarket
 * products.
 *
 * @param onProductClicked function called when a product is selected.
 */
class ProductAdapter(
    private val onProductClicked: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private val products = mutableListOf<Product>()

    /**
     * Replaces the current product list with a new list.
     *
     * @param newProducts products to display.
     */
    fun submitList(newProducts: List<Product>) {

        products.clear()
        products.addAll(newProducts)

        notifyDataSetChanged()

        Timber.d(
            "ProductAdapter updated with ${newProducts.size} products"
        )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {

        val binding =
            ItemProductBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ProductViewHolder,
        position: Int
    ) {

        holder.bind(products[position])
    }

    override fun getItemCount(): Int {

        return products.size
    }

    /**
     * ViewHolder responsible for displaying one product.
     */
    inner class ProductViewHolder(
        private val binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Displays product information in the product card.
         *
         * @param product product to display.
         */
        fun bind(product: Product) {

            binding.tvProductTitle.text =
                product.title

            binding.tvProductPrice.text =
                String.format(
                    Locale.getDefault(),
                    "R%.0f",
                    product.price
                )

            binding.tvProductCondition.text = product.condition ?: "Good condition"

            val imageUrl =
                product.images.firstOrNull()

            Glide.with(binding.ivProduct.context)
                .load(imageUrl)
                .placeholder(
                    R.drawable.ic_campus_market_logo
                )
                .error(
                    R.drawable.ic_campus_market_logo
                )
                .into(binding.ivProduct)

            binding.root.setOnClickListener {

                Timber.d(
                    "Product selected: ${product.title}"
                )

                onProductClicked(product)
            }
        }
    }
}
