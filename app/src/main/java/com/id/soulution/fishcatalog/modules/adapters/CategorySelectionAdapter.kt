package com.id.soulution.fishcatalog.modules.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.id.soulution.fishcatalog.R
import com.id.soulution.fishcatalog.modules.models.Catalogue
import com.id.soulution.fishcatalog.modules.models.Category
import java.lang.StringBuilder

class CategorySelectionAdapter(private val handler: (Catalogue, Int) -> Unit):
    RecyclerView.Adapter<CategorySelectionAdapter.ViewHolder>() {

    var items: MutableList<Catalogue> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var category: Int = 0
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var categoryLabel: String = "Warna"
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemName: TextView
        var itemDesc: TextView
        var itemCover: ImageView
        var view: View = itemView

        init {
            itemName = view.findViewById(R.id.item_name)
            itemDesc = view.findViewById(R.id.item_description)
            itemCover = view.findViewById(R.id.item_cover)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_catalogue, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: Catalogue = this.items[position]
        holder.itemName.text = item.name
        for (itemCategory: Category in item.categories) {
            if (category == itemCategory.id) {
                holder.itemDesc.text = StringBuilder().append(categoryLabel).append(" : ").append(itemCategory.label)
            }
        }
        Glide.with(holder.view)  //2
            .load(item.uri) //3
            .centerCrop() //4
            .error(R.drawable.ic_not_found_black_24dp) //6
            .into(holder.itemCover)
        holder.view.setOnClickListener {
            handler(item, position)
        }
    }
}