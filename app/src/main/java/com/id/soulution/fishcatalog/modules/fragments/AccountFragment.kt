package com.id.soulution.fishcatalog.modules.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.id.soulution.fishcatalog.R
import com.id.soulution.fishcatalog.modules.activity.MainActivity
import com.id.soulution.fishcatalog.modules.adapters.AccountMenuAdapter
import com.id.soulution.fishcatalog.modules.items.AccountMenuItem
import com.id.soulution.fishcatalog.modules.models.User

class AccountFragment: Fragment() {

    companion object {
        fun newInstance(): AccountFragment {
            return AccountFragment()
        }
    }

    private lateinit var mainList: RecyclerView
    private lateinit var auth: FirebaseAuth
    private lateinit var fdb: FirebaseDatabase

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
        val adapter = AccountMenuAdapter() { _, position ->
            if (position == 1) {
                if (this.auth.currentUser != null) {
                    this.auth.signOut()
                    startActivity(Intent(context, MainActivity::class.java))
                    activity!!.finish()
                }
            }
            else {
                openDialogChangeName()
            }
        }
        val accountMenuItems: MutableList<AccountMenuItem> = arrayListOf()
        accountMenuItems.add(AccountMenuItem("Ubah Akun"))
        accountMenuItems.add(AccountMenuItem("Keluar"))

        // Action
        this.mainList.setBackgroundColor(0)
        adapter.items = accountMenuItems
        this.mainList.layoutManager = LinearLayoutManager(context)
        this.mainList.adapter = adapter

    }

    private fun openDialogChangeName() {
        val builder = AlertDialog.Builder(activity)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_profile, null)
        builder.setCancelable(true)
        builder.setView(view)
        builder.setTitle("Ubah Akun")

        val inputName = view.findViewById(R.id.dialog_input_name) as EditText
        // Check Already Authentication or Not
        if (this.auth.currentUser != null) {
            // Check Already Fill The Profile Or Not
            this.fdb.getReference("users").child(this.auth.uid!!)
                .addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.exists()) {
                            val user = p0.getValue(User::class.java)
                            if (user != null) {
                                inputName.setText(user.full_name)
                            }
                        }
                    }
                })
        }

        // Action Builder Button
        builder.setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialog, _ ->
                if (TextUtils.isEmpty(inputName.text.toString())) {
                    Toast.makeText(context, "Harap isi label", Toast.LENGTH_SHORT).show()
                }
                else {
                    this.fdb.getReference("users").child(this.auth.uid!!).child("full_name").setValue(
                        inputName.text.toString()
                    )
                    dialog.dismiss()
                }
            })
            .setNegativeButton(android.R.string.cancel,
                DialogInterface.OnClickListener { dialog, _ ->
                    dialog.dismiss()
                })
        builder.create().show()
    }
}