package com.id.soulution.fishcatalog.modules.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.id.soulution.fishcatalog.R
import com.id.soulution.fishcatalog.modules.items.HomeMenuItem

class HomeMenuAdapter(private val handler: (HomeMenuItem, Int) -> Unit):
    RecyclerView.Adapter<HomeMenuAdapter.ViewHolder>() {

    var items: MutableList<HomeMenuItem> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemIcon: ImageView
        var itemLabel: TextView
        var view: View = itemView

        init {
            this.itemIcon = this.view.findViewById(R.id.item_icon)
            this.itemLabel = this.view.findViewById(R.id.item_label)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_menu, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: HomeMenuItem = this.items[position]
        holder.itemIcon.setImageResource(item.icon)
        holder.itemLabel.text = item.label
        holder.view.setOnClickListener {
            handler(item, position)
        }
    }
}