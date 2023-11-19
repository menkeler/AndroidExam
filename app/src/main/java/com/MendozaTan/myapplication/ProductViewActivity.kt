package com.MendozaTan.myapplication

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.smarteist.autoimageslider.SliderView
import com.smarteist.autoimageslider.SliderViewAdapter

class ProductViewActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_PRODUCT_ID = "extra_product_id"
        private const val TAG = "ProductViewActivity"
    }

    private val db = Firebase.firestore
    private lateinit var sliderView: SliderView
    private lateinit var textViewProductName: TextView
    private lateinit var textViewProductDescription: TextView
    private lateinit var textViewProductPrice: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_view)


        sliderView = findViewById(R.id.imageViewProduct)
        textViewProductName = findViewById(R.id.textViewProductName)
        textViewProductDescription = findViewById(R.id.textViewProductDescription)
        textViewProductPrice = findViewById(R.id.textViewProductPrice)

        val productId = intent.getStringExtra(EXTRA_PRODUCT_ID)

        Log.d(TAG, "Product ID: $productId")

        val productRef = productId?.let {
            db.collection("products").document(it)
        }

        productRef?.get()
            ?.addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    Log.d(TAG, "Document data: ${document.data}")

                    val productName = document.getString("name") ?: ""
                    val productDescription = document.getString("description") ?: ""
                    val productPrice = document.getDouble("price") ?: 0.0
                    val imagesList = document.get("images") as? List<String> ?: emptyList()

                    val sliderAdapter = ImagesSliderAdapter(this, imagesList)
                    sliderView.setSliderAdapter(sliderAdapter)

                    textViewProductName.text = productName
                    textViewProductDescription.text = productDescription
                    textViewProductPrice.text = "Price: $${productPrice}"
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            ?.addOnFailureListener { exception ->
                Log.d(TAG, "Error getting document: ", exception)
            }
    }

    class ImagesSliderAdapter(private val context: Context, private val imageUrls: List<String>) :
        SliderViewAdapter<ImagesSliderAdapter.SliderAdapterVH>() {
        class SliderAdapterVH(itemView: View) : SliderViewAdapter.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.imageViewProduct)
        }
        override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.itemimage, parent, false)
            return SliderAdapterVH(view)
        }
        override fun onBindViewHolder(viewHolder: SliderAdapterVH, position: Int) {
            val imageUrl = imageUrls[position]

            Glide.with(viewHolder.itemView)
                .load(imageUrl)
                .into(viewHolder.imageView)
        }
        override fun getCount(): Int {
            return imageUrls.size
        }
    }
}
