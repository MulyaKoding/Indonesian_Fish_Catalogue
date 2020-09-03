package com.id.soulution.fishcatalog.modules.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.id.soulution.fishcatalog.R
import com.id.soulution.fishcatalog.modules.models.User

class MainActivity : AppCompatActivity() {

    private lateinit var mainLogo: ImageView
    private lateinit var mainTitle: TextView
    private lateinit var mainSignIn: Button
    private lateinit var mainCopyright: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var fdb: FirebaseDatabase
    private val isSignIn = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.init()
        this.bind()
        this.actions()
    }

    // Auth users using google sign in
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {

                        // Check if users already created on database
                        this.fdb.getReference("users").child(auth.uid!!)
                            .addListenerForSingleValueEvent(object: ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {}
                                override fun onDataChange(p0: DataSnapshot) {
                                    if (!p0.exists()) {
                                        // If user not already, create it to database
                                        val key = auth.uid
                                        fdb.getReference("users").child(key!!).setValue(
                                            User(user.uid, user.email!!, user.displayName!!, "", "", 0)
                                        ).addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                startActivity(Intent(applicationContext, DashboardActivity::class.java))
                                                finish()
                                            }
                                        }
                                    }
                                    else {
                                        startActivity(Intent(applicationContext, DashboardActivity::class.java))
                                        finish()
                                    }
                                }
                            })
                    }
                } else {
                    // Show error message
                    Toast.makeText(applicationContext, getString(R.string.msg_failed_login), Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == isSignIn) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    firebaseAuthWithGoogle(account!!)
                } catch (e: ApiException) {
                    Log.w("GOOGLE_SIGN_IN", "Google sign in failed", e)
                }
            }
        }
    }

    private fun actions() {
        this.mainSignIn.setOnClickListener {
            // Todo Sign In With Google
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, isSignIn)
        }
    }

    private fun bind() {
        this.mainLogo.setImageDrawable(resources.getDrawable(R.drawable.logo))
        this.mainTitle.text = resources.getString(R.string.app_name)
        this.mainSignIn.text = resources.getString(R.string.btn_sign_in)
        this.mainCopyright.text = resources.getString(R.string.label_copyright)
    }

    private fun init() {
        this.mainLogo = findViewById(R.id.main_logo)
        this.mainTitle = findViewById(R.id.main_title)
        this.mainSignIn = findViewById(R.id.main_sign_in)
        this.mainCopyright = findViewById(R.id.main_copyright)

        this.auth = FirebaseAuth.getInstance()
        this.fdb = FirebaseDatabase.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        this.googleSignInClient = GoogleSignIn.getClient(this, gso)

        if (this.auth.currentUser != null) {
            this.mainSignIn.visibility = View.GONE
            this.fdb.getReference("users").child(auth.uid!!)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.exists()) {
                            startActivity(Intent(applicationContext, DashboardActivity::class.java))
                            finish()
                        }
                    }
                })
        }
    }
}
