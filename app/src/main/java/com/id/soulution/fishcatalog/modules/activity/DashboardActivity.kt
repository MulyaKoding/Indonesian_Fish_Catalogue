package com.id.soulution.fishcatalog.modules.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.id.soulution.fishcatalog.R
import com.id.soulution.fishcatalog.helpers.DateTimeHelper
import com.id.soulution.fishcatalog.modules.fragments.AccountFragment
import com.id.soulution.fishcatalog.modules.fragments.ContributionFragment
import com.id.soulution.fishcatalog.modules.fragments.HomeFragment
import com.id.soulution.fishcatalog.modules.models.User
import java.lang.StringBuilder

class DashboardActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var fdb: FirebaseDatabase
    private lateinit var mainMenu: BottomNavigationView
    private lateinit var mainAppsIdentityUser: TextView
    private lateinit var mainAppsIdentityDate: TextView
    private lateinit var mainCreateNew: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        this.init()
        this.bind()
        this.actions()
    }

    private fun init() {
        this.auth = FirebaseAuth.getInstance()
        this.fdb = FirebaseDatabase.getInstance()
    }

    private fun bind() {
        this.mainMenu = findViewById(R.id.main_menu)
        this.mainAppsIdentityUser = findViewById(R.id.main_apps_identity_user)
        this.mainAppsIdentityDate = findViewById(R.id.main_apps_identity_date)
        this.mainCreateNew = findViewById(R.id.main_create_new)
    }

    private fun actions() {
        // Check Already Authentication or Not
        if (this.auth.currentUser != null) {
            // Check Already Fill The Profile Or Not
            this.fdb.getReference("users").child(this.auth.uid!!)
                .addValueEventListener(this.getUserProfile)
        }

        // Set Handler On Bottom Navigation Selected Listener
        this.mainMenu.setOnNavigationItemSelectedListener(mainMenuListener)

        // Set Handler On Create New Listerner
        this.mainCreateNew.setOnClickListener {
            startActivity(Intent(applicationContext, CreateFishActivity::class.java))
        }

        // Set Fragment
        this.setFragment(0)
    }

    // Get User Profile And Check Is Register or Not
    private var getUserProfile = object: ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {}

        override fun onDataChange(p0: DataSnapshot) {
            if (p0.exists()) {
                val user = p0.getValue(User::class.java)
                if (user != null) {
                    mainAppsIdentityUser.text = StringBuilder()
                        .append("Selamat Datang,")
                        .append("\n")
                        .append(user.full_name)
                    mainAppsIdentityDate.text = DateTimeHelper().currentTime()
                }
            }
        }
    }

    // Set Fragment
    private fun setFragment(position: Int) {
        val fragment: Fragment = when (position) {
            1 -> ContributionFragment.newInstance()
            2 -> AccountFragment.newInstance()
            else -> HomeFragment.newInstance()
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frame, fragment)
            .commit()
    }

    // Handle MainMenu
    private val mainMenuListener = BottomNavigationView.OnNavigationItemSelectedListener {
        when (it.itemId) {
            R.id.item_home -> {
                setFragment(0)
                this.mainCreateNew.visibility = View.GONE
                return@OnNavigationItemSelectedListener true
            }
            R.id.item_contribution -> {
                setFragment(1)
                this.mainCreateNew.visibility = View.VISIBLE
                return@OnNavigationItemSelectedListener true
            }
            R.id.item_account -> {
                setFragment(2)
                this.mainCreateNew.visibility = View.GONE
                return@OnNavigationItemSelectedListener true
            }
        }
        return@OnNavigationItemSelectedListener false
    }
}
