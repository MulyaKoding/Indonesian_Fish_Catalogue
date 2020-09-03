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
import java.lang.StringBuilder

class TypeAdapter(private val handler: (Catalogue, Int) -> Unit):
    RecyclerView.Adapter<TypeAdapter.ViewHolder>() {

    var items: MutableList<Catalogue> = arrayListOf()
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
        holder.itemDesc.text = if (item.description.length > 30) StringBuilder().append(item.description.subSequence(0, 30)).append(" ...")
        else item.description
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