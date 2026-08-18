package com.craftlanka.app.data

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.craftlanka.app.BuildConfig
import com.craftlanka.app.model.Product
import com.google.firebase.firestore.FirebaseFirestore

class SellerRepository {
    private val db = FirebaseFirestore.getInstance()
    private val productsRef = db.collection("products")

    /**
     * Uploads a product image to Cloudinary.
     */
    fun uploadProductImage(
        context: Context,
        imageUri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        try {
            MediaManager.get()
        } catch (e: Exception) {
            val config =
                hashMapOf(
                    "cloud_name" to BuildConfig.CLOUDINARY_CLOUD_NAME,
                    "secure" to true,
                )
            MediaManager.init(context, config)
        }

        MediaManager.get().upload(imageUri)
            .option("unsigned", true)
            .option("upload_preset", "craftlanka_preset")
            .callback(
                object : UploadCallback {
                    override fun onStart(requestId: String?) {}

                    override fun onProgress(
                        requestId: String?,
                        bytes: Long,
                        totalBytes: Long,
                    ) {}

                    override fun onSuccess(
                        requestId: String?,
                        resultData: MutableMap<Any?, Any?>?,
                    ) {
                        val url = resultData?.get("secure_url") as? String ?: ""
                        onSuccess(url)
                    }

                    override fun onError(
                        requestId: String?,
                        error: ErrorInfo?,
                    ) {
                        onFailure(error?.description ?: "Image upload failed")
                    }

                    override fun onReschedule(
                        requestId: String?,
                        error: ErrorInfo?,
                    ) {}
                },
            )
            .dispatch()
    }

    /**
     * Saves or Updates product details to Firestore.
     */
    fun addProduct(
        product: Product,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val docRef = if (product.productId.isEmpty()) productsRef.document() else productsRef.document(product.productId)
        val finalProduct = product.copy(productId = docRef.id)

        docRef.set(finalProduct)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.message ?: "Failed to save product") }
    }

    /**
     * Fetches products for a specific seller.
     */
    fun getSellerProducts(
        sellerUid: String,
        onSuccess: (List<Product>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        productsRef.whereEqualTo("sellerUid", sellerUid)
            .get()
            .addOnSuccessListener { snapshot ->
                val productList = snapshot.toObjects(Product::class.java)
                onSuccess(productList)
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Failed to fetch products")
            }
    }

    /**
     * Fetches a single product by ID.
     */
    fun getProduct(
        productId: String,
        onSuccess: (Product?) -> Unit,
        onFailure: (String) -> Unit
    ) {
        productsRef.document(productId).get()
            .addOnSuccessListener { document ->
                onSuccess(document.toObject(Product::class.java))
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Failed to fetch product details")
            }
    }

    /**
     * Deletes a product from Firestore.
     */
    fun deleteProduct(
        productId: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (productId.isEmpty()) return
        productsRef.document(productId).delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.message ?: "Failed to delete product") }
    }
}
