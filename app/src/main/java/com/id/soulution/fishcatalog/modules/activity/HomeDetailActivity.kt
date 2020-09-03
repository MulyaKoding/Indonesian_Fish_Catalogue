package com.id.soulution.fishcatalog.modules.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.id.soulution.fishcatalog.R
import com.id.soulution.fishcatalog.modules.fragments.*

class HomeDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_detail)
        if (supportActionBar != null)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

        this.init()
        this.bind()
        this.actions()
    }

    private fun init() {}

    private fun bind() {}

    private fun actions() {
        // Set Fragment
        val selection: Int = intent.getIntExtra("selection", 0)
        this.setFragment(selection)
        when (selection) {
            0 -> setTitle(R.string.label_type)
            1 -> setTitle(R.string.label_location)
            2 -> setTitle(R.string.label_category)
            else -> setTitle(R.string.label_about_us)
        }
    }

    // Set Fragment
    private fun setFragment(position: Int) {
        val fragment: Fragment = when (position) {
            0 -> TypeFragment.newInstance()
            1 -> LocationFragment.newInstance()
            2 -> CategoryFragment.newInstance()
            else -> AboutUsFragment.newInstance()
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.home_detail_frame, fragment)
            .commit()
    }
}
