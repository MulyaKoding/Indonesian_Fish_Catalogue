package com.id.soulution.fishcatalog.modules.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.id.soulution.fishcatalog.R
import com.id.soulution.fishcatalog.modules.adapters.AccountMenuAdapter
import com.id.soulution.fishcatalog.modules.adapters.UserAdapter
import com.id.soulution.fishcatalog.modules.items.AccountMenuItem
import com.id.soulution.fishcatalog.modules.items.UserItem
import java.lang.StringBuilder

class AboutUsFragment: Fragment() {

    companion object {
        fun newInstance(): AboutUsFragment {
            return AboutUsFragment()
        }
    }

    private lateinit var mainList: RecyclerView
    private lateinit var mainAppsIdentityUser: TextView
    private lateinit var mainAppsIdentityDate: TextView

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_about_us, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bind
        this.mainList = view.findViewById(R.id.home_detail_list)
        this.mainAppsIdentityUser = view.findViewById(R.id.main_apps_identity_user)
        this.mainAppsIdentityDate = view.findViewById(R.id.main_apps_identity_date)
        val adapter = UserAdapter() { _, _ ->

        }
        val userItems: MutableList<UserItem> = arrayListOf()
        userItems.add(UserItem(R.drawable.rahmat, "Rahmat Mulya Simanjuntak"))
        userItems.add(UserItem(R.drawable.andreas, "Andreas Pakpahan"))

        this.mainAppsIdentityUser.text = StringBuilder().append("Fish Catalogue merupakan aplikasi yang berisi informasi mengenai ikan mulai dari jenis, spesies, dan lainnya")
        this.mainAppsIdentityDate.text = StringBuilder().append("Fish Catalogue")

        // Action
        this.mainList.setBackgroundColor(0)
        adapter.items = userItems
        this.mainList.layoutManager = GridLayoutManager(context, 2)
        this.mainList.adapter = adapter
    }

}