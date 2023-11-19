package com.MendozaTan.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class AddItemActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val storage = Firebase.storage

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                handleImageSelection(data)

                Toast.makeText(
                    baseContext, "Images picked successfully", Toast.LENGTH_SHORT
                ).show()
            }
        }

    private val selectedImageUris = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        val editTextProductName = findViewById<EditText>(R.id.editTextProductName)
        val editTextProductDescription = findViewById<EditText>(R.id.editTextProductDescription)
        val editTextProductPrice = findViewById<EditText>(R.id.editTextProductPrice)
        val btnAddProduct = findViewById<Button>(R.id.btnAddProduct)
        val btnPickImages = findViewById<Button>(R.id.btnPickImages)

        btnPickImages.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            resultLauncher.launch(intent)
        }
        btnAddProduct.setOnClickListener {
            val productName = editTextProductName.text.toString()
            val productDescription = editTextProductDescription.text.toString()
            val productPrice = editTextProductPrice.text.toString()

            if (productName.isNotEmpty() && productDescription.isNotEmpty() && productPrice.isNotEmpty() && selectedImageUris.isNotEmpty()) {
                val currentTimestamp = com.google.firebase.Timestamp.now()

                uploadImagesToStorage(selectedImageUris) { imageUrls ->
                    val newProduct = hashMapOf(
                        "name" to productName,
                        "description" to productDescription,
                        "price" to productPrice.toDouble(),
                        "images" to imageUrls,
                        "dateCreated" to currentTimestamp
                    )
                    db.collection("products")
                        .add(newProduct)
                        .addOnSuccessListener { documentReference ->
                            finish()
                        }
                        .addOnFailureListener { e ->
                            //errors
                        }
                }
            } else {
                Toast.makeText(
                    baseContext, "Please fill in all fields and select at least one image", Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun handleImageSelection(data: Intent?) {
        if (data!!.clipData != null) {
            val clipData = data.clipData
            if (clipData != null) {
                for (i in 0 until clipData.itemCount) {
                    val imageUri = clipData.getItemAt(i).uri
                    selectedImageUris.add(imageUri.toString())
                }
            }
        } else if (data!!.data != null) {
            val imageUri = data.data
            selectedImageUris.add(imageUri.toString())
        }
    }

    private fun uploadImagesToStorage(imageUris: List<String>, onComplete: (List<String>) -> Unit) {
        val storageRef = storage.reference
        val imageUrls = mutableListOf<String>()
        var uploadCount = 0

        for (uriString in imageUris) {
            val imageUri = Uri.parse(uriString)
            val imageName = "${System.currentTimeMillis()}_${imageUri.lastPathSegment}"

            val imageRef = storageRef.child("images/$imageName")

            imageRef.putFile(imageUri)
                .addOnSuccessListener {
                    uploadCount++
                    imageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                        imageUrls.add(imageUrl.toString())
                        if (uploadCount == imageUris.size) {
                            onComplete(imageUrls)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    //errors
                }
        }
    }
}