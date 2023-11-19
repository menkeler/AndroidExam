package com.MendozaTan.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.smarteist.autoimageslider.SliderView
import com.smarteist.autoimageslider.SliderViewAdapter
class MainActivity : AppCompatActivity() {

    lateinit var _btnLogout: Button
    val db = Firebase.firestore
    lateinit var recyclerView: RecyclerView
    lateinit var _btnAdd: Button

    data class Product(
        val id: String,
        val timestamp: String,
        val description: String,
        val imageUrls: List<String>,
        val name: String,
        val price: Double
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        _btnLogout = findViewById(R.id.btnLogout)
        _btnLogout.setOnClickListener {
            Firebase.auth.signOut()
            finish()
        }
        _btnAdd = findViewById(R.id.btnAdd)
        _btnAdd.setOnClickListener {
            var intent =Intent(this,AddItemActivity::class.java)
            startActivity(intent)
        }


        val productList = mutableListOf<Product>()

        db.collection("products")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val product = Product(
                        document.id,
                        document.getString("timestamp") ?: "",
                        document.getString("description") ?: "",
                        document.get("images")?.let { images ->
                            if (images is List<*>) {
                                images.filterIsInstance<String>()
                            } else {
                                emptyList()
                            }
                        } ?: emptyList(),
                        document.getString("name") ?: "",
                        document.getDouble("price") ?: 0.0
                    )
                    // Log the values
                    Log.d(TAG, "Product ID: ${product.id}")
                    Log.d(TAG, "Timestamp: ${product.timestamp}")
                    Log.d(TAG, "Description: ${product.description}")
                    Log.d(TAG, "Image URLs: ${product.imageUrls}")
                    Log.d(TAG, "Name: ${product.name}")
                    Log.d(TAG, "Price: ${product.price}")

                    productList.add(product)
                }
                val productAdapter = ProductAdapter(productList) { product ->
                    val intent = Intent(this, ProductViewActivity::class.java).apply {
                        putExtra(ProductViewActivity.EXTRA_PRODUCT_ID, product.id)
                    }
                    startActivity(intent)
                }
                recyclerView.adapter = productAdapter
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

    class ImagesAdapter(private val context: Context, private val imageUrls: List<String>) :
        SliderViewAdapter<ImagesAdapter.SliderAdapterVH>() {
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

    class ProductAdapter(private val productList: List<Product>, private val onItemClick: (Product) -> Unit) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageSlider: SliderView = itemView.findViewById(R.id.imageSlider)
            val textViewProductName: TextView = itemView.findViewById(R.id.textViewProductName)
            val textViewProductPrice: TextView = itemView.findViewById(R.id.textViewProductPrice)
            val textViewProductDescription: TextView = itemView.findViewById(R.id.textViewProductDescription)
            val buttonViewDetails: Button = itemView.findViewById(R.id.buttonViewDetails)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.itemproduct, parent, false)
            return ProductViewHolder(view)
        }
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder) {
                is ProductViewHolder -> {
                    val product = productList[position]

                    holder.textViewProductName.text = product.name
                    holder.textViewProductDescription.text = product.description
                    holder.textViewProductPrice.text = "Price: $${product.price}"

                    val imagesAdapter = ImagesAdapter(holder.itemView.context, product.imageUrls)
                    holder.imageSlider.setSliderAdapter(imagesAdapter)

                    holder.buttonViewDetails.setOnClickListener {
                        onItemClick(product)
                    }
                }
            }
        }
        override fun getItemCount(): Int = productList.size
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
