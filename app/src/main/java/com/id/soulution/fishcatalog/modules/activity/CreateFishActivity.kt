package com.id.soulution.fishcatalog.modules.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.id.soulution.fishcatalog.R
import com.id.soulution.fishcatalog.modules.adapters.CategoryAdapter
import com.id.soulution.fishcatalog.modules.adapters.LocationSelectedAdapter
import com.id.soulution.fishcatalog.modules.models.Catalogue
import com.id.soulution.fishcatalog.modules.models.Category
import java.util.*

class CreateFishActivity : AppCompatActivity() {

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
    private lateinit var createNewLocation: RecyclerView
    private lateinit var createNewLocationChoose: Button
    private lateinit var createNewType: Spinner
    private lateinit var createNewPreview: ImageView

    private val pickFileRequest = 31
    private val pickFile = 32
    private var fishType = -1
    private var fileUri: Uri? = null
    private lateinit var categoryList: MutableList<Category>
    private lateinit var locationList: MutableList<String>
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var locationAdapter: LocationSelectedAdapter
    private lateinit var categoryRes: MutableList<String>
    private lateinit var locationRes: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_fish)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.create_fish_name)
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
        this.locationList = arrayListOf()
    }

    private fun bind() {
        this.createNewSave = findViewById(R.id.create_new_save)
        this.createNewChoose = findViewById(R.id.create_new_choose)
        this.createNewCategory = findViewById(R.id.create_new_category)
        this.createNewCategoryDialog = findViewById(R.id.create_new_category_dialog)
        this.createNewName = findViewById(R.id.create_new_name)
        this.createNewDesc = findViewById(R.id.create_new_description)
        this.createNewLocation = findViewById(R.id.create_new_location)
        this.createNewLocationChoose = findViewById(R.id.create_new_location_dialog)
        this.createNewType = findViewById(R.id.create_new_type)
        this.createNewPreview = findViewById(R.id.create_new_preview)

        this.createNewCategory.layoutManager = LinearLayoutManager(applicationContext)
        this.categoryAdapter = CategoryAdapter { _, _ -> }
        this.createNewCategory.adapter = categoryAdapter

        this.createNewLocation.layoutManager = LinearLayoutManager(applicationContext)
        this.locationAdapter = LocationSelectedAdapter { _, _ -> }
        this.createNewLocation.adapter = locationAdapter

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

        this.locationRes = resources.getStringArray(R.array.location_array).toMutableList()
        this.categoryRes = resources.getStringArray(R.array.classification_array).toMutableList()
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
        this.createNewCategoryDialog.setOnClickListener {
            if (this.categoryList.size >= 9) Toast.makeText(
                this,
                "Maksimal kategori adalah 9",
                Toast.LENGTH_SHORT
            ).show()
            else openDialogAddCategory()
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
                !checkIsAlreadyFixCategory() -> {
                    Toast.makeText(
                        this,
                        "Silakan mengisi kategori ikan seluruhnya terlebih dahulu",
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
                locationList.size == 0 -> {
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

        this.createNewLocationChoose.setOnClickListener {
            val builder = AlertDialog.Builder(this@CreateFishActivity)
            builder.setTitle("Tambah Lokasi")
                .setItems(R.array.location_array
                ) { dialog, which ->
                    if (!locationList.contains(locationRes[which]))
                        locationList.add(locationRes[which])
                    else Toast.makeText(this@CreateFishActivity, "Lokasi sudah dipilih sebelumnya",
                    Toast.LENGTH_LONG).show()
                    locationAdapter.items = locationList
                    dialog.dismiss()
                }
            builder.create().show()
        }
    }

    private fun openDialogAddCategory() {
        val builder = AlertDialog.Builder(this@CreateFishActivity)
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
                positionCategory = if (positionCategory != -1) positionCategory else 0
                var status = false
                categoryList.forEach {
                    if (it.id == positionCategory) status = true
                }
                if (!status) {
                    categoryList.add(
                        Category(
                            positionCategory,
                            "${categoryRes[positionCategory]}: ${inputCategory.text}"
                        )
                    )
                    categoryAdapter.items = categoryList
                    dialog.dismiss()
                } else
                    Toast
                        .makeText(
                            applicationContext,
                            "Kategori sejenis sudah ditambahkan",
                            Toast.LENGTH_SHORT
                        )
                        .show()
            }
        }.setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    private fun checkIsAlreadyFixCategory(): Boolean {
        val category = arrayListOf(0, 1, 2, 3, 4, 5, 6, 7, 8)
        val statusCategory = arrayListOf(false, false, false, false, false, false, false, false, false)

        if (this.categoryList.size < 9) return false

        this.categoryList.forEachIndexed { _, it ->
            val index = category.indexOf(it.id)
            if (index != -1)
                statusCategory[index] = true
        }

        return !statusCategory.contains(false)
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
                    Toast.makeText(
                            applicationContext,
                            "Failed " + p0.message,
                            Toast.LENGTH_SHORT
                        ).show()
                }
                .addOnProgressListener {
                    val progress = (100.0 * it.bytesTransferred
                            / it.totalByteCount)
                    progressDialog.setMessage(String.format("Uploaded %.2f", progress) + "%")
                }
        }
    }

    private fun doInsertToFirebase(uri: String) {
        val uid = this.fdb.getReference("catalogue").push().key!!
        this.fdb.getReference("catalogue").child(uid).setValue(
            Catalogue(
                uid, this.auth.uid!!, categoryList, createNewName.text.toString(),
                createNewDesc.text.toString(), this.locationList.joinToString(),
                fishType, uri
            )
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(
                        applicationContext,
                        "Berhasil membuat ikan",
                        Toast.LENGTH_SHORT
                    ).show()
                finish()

                createNewSave.isEnabled = true
            } else {
                Toast
                    .makeText(
                        applicationContext,
                        "Terjadi kesalahan, harap periksa koneksi internet anda atau isi seluruh form dengan benar",
                        Toast.LENGTH_SHORT
                    )
                    .show()
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