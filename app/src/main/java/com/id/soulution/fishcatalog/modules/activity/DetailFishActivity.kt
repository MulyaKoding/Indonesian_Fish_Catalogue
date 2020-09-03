package com.id.soulution.fishcatalog.modules.activity

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.id.soulution.fishcatalog.R
import com.id.soulution.fishcatalog.modules.adapters.CategoryAdapter
import com.id.soulution.fishcatalog.modules.models.Catalogue
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom
import com.squareup.picasso.Target
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DetailFishActivity : AppCompatActivity() {
    private lateinit var cover: ImageView
    private lateinit var name: TextView
    private lateinit var description: TextView
    private lateinit var type: TextView
    private lateinit var location: TextView
    private lateinit var category: RecyclerView
    private lateinit var share: Button

    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_fish)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.detail_fish_name)
        }

        this.bind()
        this.action()
    }

    private fun bind() {
        this.category = findViewById(R.id.detail_fish_category)
        this.name = findViewById(R.id.detail_fish_name)
        this.type = findViewById(R.id.detail_fish_type)
        this.location = findViewById(R.id.detail_fish_location)
        this.description = findViewById(R.id.detail_fish_description)
        this.cover = findViewById(R.id.detail_fish_preview)
        this.share = findViewById(R.id.detail_fish_share)

        this.category.layoutManager = LinearLayoutManager(applicationContext)
        this.categoryAdapter = CategoryAdapter { _, _ -> }
        this.category.adapter = categoryAdapter

        // Get Catalogue
        val catalogue: Catalogue? = intent.getSerializableExtra("selected") as Catalogue
        if (catalogue != null) {
            this.categoryAdapter.items = catalogue.categories
            Glide.with(this)  // 2
                .load(catalogue.uri) // 3
                .centerCrop() // 4
                .error(R.drawable.ic_not_found_black_24dp) //6
                .into(this.cover)
            this.name.text = catalogue.name
            this.description.text = catalogue.description
            this.location.text = catalogue.location
            when (catalogue.type) {
                0 -> this.type.text = getString(R.string.label_fish_type_1)
                1 -> this.type.text = getString(R.string.label_fish_type_2)
                else -> this.type.text = getString(R.string.label_fish_type_3)
            }
        }
    }

    private fun action() {
        // Get Catalogue
        val catalogue: Catalogue? = intent.getSerializableExtra("selected") as Catalogue
        if (catalogue != null) {
            this.share.setOnClickListener {
                val builder = AlertDialog.Builder(this@DetailFishActivity)
                share.isEnabled = false
                builder.setTitle("Bagikan " + catalogue.name)
                builder.setItems(R.array.choole_share) { dialog, which ->
                    if (which == 0) {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            this.putExtra(Intent.EXTRA_TEXT, StringBuilder()
                                    .append(catalogue.name)
                                    .append(catalogue.description)
                                    .append(catalogue.location).toString()
                            )
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        share.isEnabled = true
                        startActivity(shareIntent)

                    } else {
                        // Code for showing progressDialog while uploading
                        val progressDialog = ProgressDialog(this)
                        progressDialog.setTitle("Tunggu sebentar...")
                        progressDialog.setCancelable(false)
                        progressDialog.show()
                        Picasso.get().load(catalogue.uri).into(object : Target {
                            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}

                            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                                share.isEnabled = true
                            }

                            override fun onBitmapLoaded(bitmap: Bitmap?, from: LoadedFrom?) {
                             //   val shareIntent = Intent()

                             //   shareIntent.action = Intent.ACTION_SEND
                              //  shareIntent.putExtra(
                              //      Intent.EXTRA_TITLE, StringBuilder()
                              //          .append(catalogue.name)
                              //          .append(catalogue.description).toString()
                              //  )
                              //  shareIntent.putExtra(
                              //      Intent.EXTRA_STREAM,
                             //       getLocalBitmapUri(bitmap!!)
                              //  )
                              //  shareIntent.type = "image/jpeg"
                              //  shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                             //   share.isEnabled = true
                               // startActivity(Intent.createChooser(shareIntent, "send"))

                                if (bitmap != null) {
                                    val shareIntent = Intent()
                                    shareIntent.action = Intent.ACTION_SEND
                                    shareIntent.putExtra(
                                        Intent.EXTRA_STREAM,
                                        getLocalBitmapUri(bitmap)
                                    )
                                    shareIntent.type = "image/*"
                                    shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
                                    share.isEnabled = true
                                    progressDialog.dismiss()
                                    startActivity(Intent.createChooser(shareIntent, "send"))
                                }
                            }
                        })
                    }
                    dialog.dismiss()
                }
                builder.create().show()
            }
        }
    }

    private fun getLocalBitmapUri(bmp: Bitmap): Uri? {
        var bmpUri: Uri? = null
        try {
            val file = File(
                getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "share_image_" + System.currentTimeMillis() + ".png"
            )
            val out = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out)
            out.close()
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                bmpUri = FileProvider.getUriForFile(applicationContext, "com.id.soulution.fishcatalog.fileprovider", file);
            } else{
                bmpUri = Uri.fromFile(file)
            }


        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bmpUri
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
