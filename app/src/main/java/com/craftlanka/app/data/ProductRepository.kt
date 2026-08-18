package com.craftlanka.app.data

import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale
import com.craftlanka.app.Product as UiProduct
import com.craftlanka.app.model.Product as FirestoreProduct

class ProductRepository {
    private val db = FirebaseFirestore.getInstance()
    private val productsRef = db.collection("products")

    fun fetchAllProducts(
        onSuccess: (List<UiProduct>) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        productsRef.get()
            .addOnSuccessListener { snapshot ->
                val products = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(FirestoreProduct::class.java)?.let { toUiProduct(it) }
                }
                onSuccess(products)
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Failed to load products")
            }
    }

    private fun toUiProduct(product: FirestoreProduct): UiProduct {
        val formattedPrice = NumberFormat.getNumberInstance(Locale("en", "LK")).format(product.price)
        return UiProduct(
            title = product.productName,
            artisan = "CraftLanka Seller",
            priceString = "LKR $formattedPrice",
            priceAmount = product.price,
            rating = 0.0,
            reviews = 0,
            category = product.category,
            inStock = product.stockQuantity > 0,
            imageUrl = product.imageUrl,
        )
    }
}
