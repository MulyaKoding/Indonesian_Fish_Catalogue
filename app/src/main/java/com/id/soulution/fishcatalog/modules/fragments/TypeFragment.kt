package com.id.soulution.fishcatalog.modules.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.id.soulution.fishcatalog.R
import com.id.soulution.fishcatalog.modules.activity.DetailFishActivity
import com.id.soulution.fishcatalog.modules.adapters.AccountMenuAdapter
import com.id.soulution.fishcatalog.modules.adapters.LocationAdapter
import com.id.soulution.fishcatalog.modules.adapters.TypeAdapter
import com.id.soulution.fishcatalog.modules.items.AccountMenuItem
import com.id.soulution.fishcatalog.modules.models.Catalogue

class TypeFragment: Fragment() {

    companion object {
        fun newInstance(): TypeFragment {
            return TypeFragment()
        }
    }

    private lateinit var mainList: RecyclerView
    private lateinit var tab: TabLayout
    private lateinit var adapter: TypeAdapter

    private lateinit var auth: FirebaseAuth
    private lateinit var fdb: FirebaseDatabase
    private var selected = 0

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Init
        this.auth = FirebaseAuth.getInstance()
        this.fdb = FirebaseDatabase.getInstance()

        // Bind
        this.mainList = view.findViewById(R.id.home_detail_list)
        this.tab = view.findViewById(R.id.home_detail_tab)
        this.adapter = TypeAdapter() { item, _ ->
            val intent = Intent(context, DetailFishActivity::class.java)
            intent.putExtra("selected", item)
            startActivity(intent)
        }
        this.tab.addTab(this.tab.newTab().setText(R.string.label_fish_type_1))
        this.tab.addTab(this.tab.newTab().setText(R.string.label_fish_type_2))
        this.tab.addTab(this.tab.newTab().setText(R.string.label_fish_type_3))

        // Action
        this.mainList.setBackgroundColor(0)
        this.tab.visibility = View.VISIBLE
        this.mainList.layoutManager = LinearLayoutManager(context)
        this.mainList.adapter = adapter

        if (this.auth.uid != null)
            this.fdb.getReference("catalogue")
                .addValueEventListener(getAllFish)
        this.tab.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                selected = tab!!.position
                fdb.getReference("catalogue")
                    .addValueEventListener(getAllFish)
            }

        })
    }

    private val getAllFish = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {}

        override fun onDataChange(p0: DataSnapshot) {
            val items: MutableList<Catalogue> = arrayListOf()
            if (p0.exists()) {
                for (item: DataSnapshot in p0.children) {
                    val itemData: Catalogue = item.getValue(Catalogue::class.java)!!
                    if (selected == itemData.type)
                        items.add(itemData)
                }
            }
            adapter.items = items
        }
    }
}