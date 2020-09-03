package com.id.soulution.fishcatalog.modules.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.id.soulution.fishcatalog.R
import com.id.soulution.fishcatalog.modules.items.UserItem
import com.id.soulution.fishcatalog.modules.models.Catalogue

class UserAdapter(private val handler: (UserItem, Int) -> Unit):
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    var items: MutableList<UserItem> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemLabel: TextView
        var itemCover: ImageView
        var view: View = itemView

        init {
            itemLabel = view.findViewById(R.id.item_label)
            itemCover = view.findViewById(R.id.item_cover)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_user, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: UserItem = this.items[position]
        holder.itemLabel.text = item.label
        Glide.with(holder.view)  //2
            .load(item.uri) //3
            .centerCrop() //4
            .error(R.drawable.ic_not_found_black_24dp) //6
            .into(holder.itemCover)
    }
}