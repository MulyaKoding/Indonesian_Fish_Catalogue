package com.id.soulution.fishcatalog.modules.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.id.soulution.fishcatalog.R
import com.id.soulution.fishcatalog.helpers.SeederDummy
import com.id.soulution.fishcatalog.modules.activity.DetailFishActivity
import com.id.soulution.fishcatalog.modules.activity.EditFishActivity
import com.id.soulution.fishcatalog.modules.adapters.CatalogueAdapter
import com.id.soulution.fishcatalog.modules.adapters.HomeMenuAdapter
import com.id.soulution.fishcatalog.modules.items.HomeMenuItem
import com.id.soulution.fishcatalog.modules.models.Catalogue
import com.id.soulution.fishcatalog.modules.models.Category
import java.lang.StringBuilder

class ContributionFragment: Fragment() {

    companion object {
        fun newInstance(): ContributionFragment {
            return ContributionFragment()
        }
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var fdb: FirebaseDatabase

    private lateinit var mainList: RecyclerView
    private lateinit var adapter: CatalogueAdapter

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Init
        this.auth = FirebaseAuth.getInstance()
        this.fdb = FirebaseDatabase.getInstance()

        // Bind
        this.mainList = view.findViewById(R.id.main_list)
        this.adapter = CatalogueAdapter { item, _, type ->
            if (type == 1) {
                showDialogChoose(item)
            }
            else {
                val intent = Intent(context, DetailFishActivity::class.java)
                intent.putExtra("selected", item)
                startActivity(intent)
            }
        }
        this.mainList.layoutManager = LinearLayoutManager(context)
        this.mainList.adapter = this.adapter

        // Action
        this.mainList.setBackgroundColor(0)
        if (this.auth.uid != null)
        this.fdb.getReference("catalogue").orderByChild("user_id").equalTo(this.auth.uid)
            .addValueEventListener(getMyFish)

//        if (activity != null)
//        SeederDummy(activity!!, this.auth, this.fdb).seed()
    }

    private fun showDialogChoose(item: Catalogue) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(activity!!)
        builder.setTitle("Pilih Aksi")
        builder.setItems(R.array.choole_action, DialogInterface.OnClickListener { dialog, which ->
            if (which == 1) {
                dialog.dismiss()
                showDialogDelete(item)
            }
            else {
                dialog.dismiss()
                val intent = Intent(context, EditFishActivity::class.java)
                intent.putExtra("selected", item)
                startActivity(intent)
            }
        })
        builder.create().show()
    }

    private fun showDialogDelete(item: Catalogue) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Hapus Katalog?")
        builder.setMessage(StringBuilder().append("Apa anda yakin ingin menghapus ${item.name} ?"))
        builder.setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialog, _ ->
            fdb.getReference("catalogue").child(item.uid).removeValue()

            this.fdb.getReference("catalogue").orderByChild("user_id").equalTo(this.auth.uid)
                .removeEventListener(getMyFish)
            this.fdb.getReference("catalogue").orderByChild("user_id").equalTo(this.auth.uid)
                .addValueEventListener(getMyFish)
        })

        builder.create().show()
    }

    private val getMyFish = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {}

        override fun onDataChange(p0: DataSnapshot) {
            val items: MutableList<Catalogue> = arrayListOf()
            if (p0.exists()) {
                for (item:DataSnapshot in p0.children) {
                    val itemData: Catalogue = item.getValue(Catalogue::class.java)!!
                    items.add(itemData)
                }
            }
            adapter.items = items
        }
    }
}