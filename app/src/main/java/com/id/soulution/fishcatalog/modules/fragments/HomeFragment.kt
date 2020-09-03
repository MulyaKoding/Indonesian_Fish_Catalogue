package com.id.soulution.fishcatalog.modules.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.id.soulution.fishcatalog.R
import com.id.soulution.fishcatalog.modules.activity.HomeDetailActivity
import com.id.soulution.fishcatalog.modules.adapters.HomeMenuAdapter
import com.id.soulution.fishcatalog.modules.items.HomeMenuItem

class HomeFragment: Fragment() {

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    private lateinit var mainList: RecyclerView

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bind
        this.mainList = view.findViewById(R.id.main_list)
        val adapter = HomeMenuAdapter{ _, position ->
            val intent = Intent(context, HomeDetailActivity::class.java)
            intent.putExtra("selection", position)
            startActivity(intent)
        }
        val homeMenuItems: MutableList<HomeMenuItem> = arrayListOf()
        homeMenuItems.add(HomeMenuItem(R.drawable.jenis_ikan, getString(R.string.label_type)))
        homeMenuItems.add(HomeMenuItem(R.drawable.lokasi, getString(R.string.label_location)))
        homeMenuItems.add(HomeMenuItem(R.drawable.kategori_ikan, getString(R.string.label_category)))
        homeMenuItems.add(HomeMenuItem(R.drawable.tentang_kami, getString(R.string.label_about_us)))

        // Action
        adapter.items = homeMenuItems
        this.mainList.layoutManager = GridLayoutManager(context, 2)
        this.mainList.adapter = adapter
    }

}