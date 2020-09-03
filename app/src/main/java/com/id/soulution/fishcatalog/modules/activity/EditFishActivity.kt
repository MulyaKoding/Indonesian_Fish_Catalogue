package com.id.soulution.fishcatalog.modules.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.id.soulution.fishcatalog.R
import com.id.soulution.fishcatalog.modules.adapters.CategoryAdapter
import com.id.soulution.fishcatalog.modules.models.Catalogue
import com.id.soulution.fishcatalog.modules.models.Category
import java.util.*

class EditFishActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var fdb: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    private lateinit var createNewSave: Button
    private lateinit var createNewChoose: Button
    private lateinit var createNewCategory: RecyclerView
    private lateinit var createNewCategoryDialog: Button
    private lateinit var createNewName: EditText
    private lateinit var createNewDesc: EditText
    private lateinit var createNewLocation: Spinner
    private lateinit var createNewType: Spinner
    private lateinit var createNewPreview: ImageView

    private val pickFileRequest = 31
    private val pickFile = 32
    private var fishType = -1
    private var fieldLocation = -1
    private var fileUri: Uri? = null
    private lateinit var categoryList: MutableList<Category>
    private lateinit var categoryAdapter: CategoryAdapter
    private var catalogue: Catalogue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_fish)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            title = "Edit Ikan"
        }

        this.init()
        this.bind()
        this.action()
    }

    private fun init() {
        this.auth = FirebaseAuth.getInstance()
        this.fdb = FirebaseDatabase.getInstance()
        this.storage = FirebaseStorage.getInstance()
        this.storageReference = storage.reference
        this.categoryList = arrayListOf()

        this.catalogue = intent.getSerializableExtra("selected") as Catalogue
        if (catalogue != null) {
            this.categoryList = this.catalogue!!.categories
        }
    }

    private fun bind() {
        this.createNewSave = findViewById(R.id.create_new_save)
        this.createNewChoose = findViewById(R.id.create_new_choose)
        this.createNewCategory = findViewById(R.id.create_new_category)
        this.createNewCategoryDialog = findViewById(R.id.create_new_category_dialog)
        this.createNewName = findViewById(R.id.create_new_name)
        this.createNewDesc = findViewById(R.id.create_new_description)
        this.createNewLocation = findViewById(R.id.create_new_location)
        this.createNewType = findViewById(R.id.create_new_type)
        this.createNewPreview = findViewById(R.id.create_new_preview)

        this.createNewCategory.layoutManager = LinearLayoutManager(applicationContext)
        this.categoryAdapter = CategoryAdapter { _, _ ->

        }
        this.createNewCategory.adapter = categoryAdapter
        this.categoryAdapter.items = this.categoryList

        // Set Type Array
        ArrayAdapter.createFromResource(
            this,
            R.array.type_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            this.createNewType.adapter = adapter
        }

        // Set Location Array
        ArrayAdapter.createFromResource(
            this,
            R.array.location_array,
            android.R.layout.simple_spinner_item
        ).also { locationAdapter ->
            // Specify the layout to use when the list of choices appears
            locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            this.createNewLocation.adapter = locationAdapter
        }

        if (this.catalogue != null) {
            this.createNewType.setSelection(this.catalogue!!.type)
            this.createNewName.setText(this.catalogue!!.name)
            this.createNewDesc.setText(this.catalogue!!.description)
            this.createNewLocation.setSelection(this.catalogue!!.location.toInt())

            Glide.with(this)  //2
                .load(catalogue!!.uri) //3
                .centerCrop() //4
                .error(R.drawable.ic_not_found_black_24dp) //6
                .into(createNewPreview)

            this.fishType = this.catalogue!!.type
            this.fieldLocation = this.catalogue!!.location.toInt()
        }
    }

    private fun action() {
        this.createNewChoose.setOnClickListener {
            //check runtime permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED
                ) {
                    //permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    //show popup to request runtime permission
                    requestPermissions(permissions, pickFileRequest)
                } else {
                    //permission already granted
                    pickImageFromGallery()
                }
            } else {
                //system OS is < Marshmallow
                pickImageFromGallery()
            }
        }
        this.createNewType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                fishType = position
            }
        }
        this.createNewLocation.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    fieldLocation = position
                }
            }
        this.createNewCategoryDialog.setOnClickListener {
            openDialogAddCategory()
        }
        this.createNewSave.setOnClickListener {
            when {
                fileUri == null -> {
                    Toast.makeText(
                        this,
                        "Silakan isi gambar ikan",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                TextUtils.isEmpty(this.createNewName.text) -> {
                    Toast.makeText(
                        this,
                        "Silakan isi nama ikan",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                fishType == -1 -> {
                    Toast.makeText(
                        this,
                        "Silakan pilih jenis ikan",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                fieldLocation == -1 -> {
                    Toast.makeText(
                        this,
                        "Silakan pilih lokasi penyebaran",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    uploadToStorage()
                }
            }
        }
    }

    private fun openDialogAddCategory() {
        val builder = AlertDialog.Builder(this@EditFishActivity)
        val view =
            LayoutInflater.from(applicationContext).inflate(R.layout.dialog_create_category, null)
        builder.setCancelable(true)
        builder.setView(view)
        builder.setTitle("Tambah Kategori")

        val chooseCategory = view.findViewById(R.id.dialog_choose_category) as Spinner
        val inputCategory = view.findViewById(R.id.dialog_input_category) as EditText
        var positionCategory = -1

        // Set Classification Array
        ArrayAdapter.createFromResource(
            this,
            R.array.classification_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            chooseCategory.adapter = adapter
        }

        chooseCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                positionCategory = position
            }
        }

        // Action Builder Button
        builder.setPositiveButton(
            android.R.string.yes
        ) { dialog, _ ->
            if (TextUtils.isEmpty(inputCategory.text.toString())) {
                Toast.makeText(applicationContext, "Harap isi label", Toast.LENGTH_SHORT).show()
            } else {
                categoryList.add(
                    Category(
                        if (positionCategory != -1) positionCategory else 0,
                        inputCategory.text.toString()
                    )
                )
                categoryAdapter.items = categoryList
                dialog.dismiss()
            }
        }.setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }

        builder.create().show()
    }

    private fun uploadToStorage() {
        if (fileUri != null) {
            this.createNewSave.isEnabled = false
            // Code for showing progressDialog while uploading

            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()

            // Defining the child of storageReference
            val ref = storageReference
                .child(
                    "fishImages/"
                            + UUID.randomUUID().toString()
                )

            // adding listeners on upload
            // or failure of image
            ref.putFile(fileUri!!)
                .addOnSuccessListener {
                    // Image uploaded successfully
                    // Dismiss dialog
                    progressDialog.dismiss()
                    ref.downloadUrl.addOnSuccessListener {
                        if (it != null) {
                            doInsertToFirebase(it.toString())
                        }
                    }
                }
                .addOnFailureListener { p0 ->
                    // Error, Image not uploaded
                    createNewSave.isEnabled = true
                    progressDialog.dismiss()
                    Toast
                        .makeText(
                            applicationContext,
                            "Failed " + p0.message,
                            Toast.LENGTH_SHORT
                        )
                        .show()
                }
                .addOnProgressListener {
                    val progress = (100.0 * it.bytesTransferred / it.totalByteCount)
                    progressDialog.setMessage(String.format("Uploaded %.2f", progress) + "%")
                }
        }
    }

    private fun doInsertToFirebase(uri: String) {
        val uid = this.catalogue!!.uid

        this.fdb.getReference("catalogue").child(uid).setValue(
            Catalogue(
                uid, this.auth.uid!!, categoryList, createNewName.text.toString(),
                createNewDesc.text.toString(), fieldLocation.toString(),
                fishType, uri
            )
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast
                    .makeText(
                        applicationContext,
                        "Berhasil mengubah ikan",
                        Toast.LENGTH_SHORT
                    )
                    .show()
                finish()
                createNewSave.isEnabled = true
            } else {
                Toast.makeText(
                    applicationContext,
                    "Terjadi kesalahan, harap periksa koneksi internet anda atau isi seluruh form dengan benar",
                    Toast.LENGTH_SHORT
                ).show()
                createNewSave.isEnabled = true
            }
        }
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, pickFile)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            pickFileRequest -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup granted
                    pickImageFromGallery()
                } else {
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //handle result of picked image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == pickFile) {
            createNewPreview.setImageURI(data?.data)
            fileUri = data?.data
        }
    }
}
