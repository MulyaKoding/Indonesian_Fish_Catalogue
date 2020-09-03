package com.id.soulution.fishcatalog.modules.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
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
import com.id.soulution.fishcatalog.modules.adapters.LocationAdapter
import com.id.soulution.fishcatalog.modules.models.Catalogue
import java.util.*

class LocationFragment : Fragment() {
    companion object {
        fun newInstance(): LocationFragment = LocationFragment()
    }

    private lateinit var mainList: RecyclerView
    private lateinit var tab: TabLayout
    private lateinit var adapter: LocationAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var fdb: FirebaseDatabase
    private var locationSelected: MutableList<Boolean> = arrayListOf()

    private lateinit var locationRes: MutableList<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_home_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        // Init
        this.auth = FirebaseAuth.getInstance()
        this.fdb = FirebaseDatabase.getInstance()

        locationRes = resources.getStringArray(R.array.location_array).toMutableList()
        locationRes.forEach { _ ->
            locationSelected.add(false)
        }

        // Bind
        this.mainList = view.findViewById(R.id.home_detail_list)
        this.tab = view.findViewById(R.id.home_detail_tab)

        this.adapter = LocationAdapter { item, _ ->
            val intent = Intent(context, DetailFishActivity::class.java)
            intent.putExtra("selected", item)
            startActivity(intent)
        }

        // Action
        this.tab.visibility = View.GONE
        this.mainList.layoutManager = LinearLayoutManager(requireContext())
        this.mainList.adapter = adapter
        this.mainList.isNestedScrollingEnabled = false

        if (this.auth.uid != null)
            this.fdb.getReference("catalogue").addValueEventListener(getAllFish)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.filter_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_filter) {
            showFilterDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showFilterDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Tambah Filter Lokasi")
            .setMultiChoiceItems(R.array.location_array, locationSelected.toBooleanArray()) {
                    _, i, b ->
                locationSelected[i] = b
            }
            .setPositiveButton("Cari") { dialog, _ ->
                // Todo Filter here
                if (auth.uid != null) {
                    fdb.getReference("catalogue").removeEventListener(getAllFish)
                    fdb.getReference("catalogue").addValueEventListener(getAllFish)
                }

                dialog.dismiss()
            }
        builder.create().show()
    }

    private val getAllFish = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {}

        override fun onDataChange(p0: DataSnapshot) {
            val items: MutableList<Catalogue> = arrayListOf()

            if (p0.exists()) {
                for (item: DataSnapshot in p0.children) {
                    val itemData: Catalogue = item.getValue(Catalogue::class.java)!!

                    var status = false
                    val tempLocation: MutableList<String> = arrayListOf()
                    locationSelected.forEachIndexed { index, b ->
                        if (b) tempLocation.add(locationRes[index])
                    }

                    tempLocation.forEach {
                        if (itemData.location.contains(it)) status = true
                    }

                    if (status) items.add(itemData)

                    if (tempLocation.size == 0) items.add(itemData)
                }
            }
            Log.i("Location1", items.toString())

            adapter.items = items.sortedWith(compareBy
            { o -> o.name.toLowerCase(Locale.getDefault()).substring(0, 1) }).toMutableList()
        }
    }
}