package com.craftlanka.app.data

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.craftlanka.app.BuildConfig
import com.craftlanka.app.model.BuyerProfile
import com.craftlanka.app.model.SellerProfile
import com.craftlanka.app.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // --- GOOGLE SIGN-IN ---

    fun signInWithGoogle(
        idToken: String,
        targetRole: String, // "buyer" or "seller"
        onSuccess: (String) -> Unit, // returns user role
        onFailure: (String) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid ?: ""
                val email = authResult.user?.email ?: ""
                val userRef = db.collection("users").document(uid)

                userRef.get().addOnSuccessListener { document ->
                    if (document.exists()) {
                        val role = document.getString("role") ?: targetRole
                        onSuccess(role)
                    } else {
                        // Create initial user role record on first Google login
                        val userDoc = User(uid = uid, email = email, role = targetRole)
                        userRef.set(userDoc)
                            .addOnSuccessListener { onSuccess(targetRole) }
                            .addOnFailureListener { e -> onFailure(e.message ?: "Failed to save user role") }
                    }
                }.addOnFailureListener { e ->
                    onFailure(e.message ?: "Failed to check user profile")
                }
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Google Authentication failed")
            }
    }

    // --- WRITING DATA (Registration) ---

    fun registerBuyer(
        profile: BuyerProfile,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(profile.email, password)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid ?: ""
                val finalProfile = profile.copy(uid = uid)
                
                db.collection("buyer_profiles").document(uid).set(finalProfile)
                    .addOnSuccessListener {
                        val userRecord = User(uid = uid, email = profile.email, role = "buyer")
                        db.collection("users").document(uid).set(userRecord)
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { onFailure("Failed to save user record") }
                    }
                    .addOnFailureListener { e -> onFailure(e.message ?: "Failed to save buyer profile") }
            }
            .addOnFailureListener { e -> onFailure(e.message ?: "Buyer registration failed") }
    }

    fun registerSeller(
        profile: SellerProfile,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(profile.email, password)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid ?: ""
                val finalProfile = profile.copy(uid = uid)
                
                db.collection("seller_profiles").document(uid).set(finalProfile)
                    .addOnSuccessListener {
                        val userRecord = User(uid = uid, email = profile.email, role = "seller")
                        db.collection("users").document(uid).set(userRecord)
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { onFailure("Failed to save user record") }
                    }
                    .addOnFailureListener { e -> onFailure(e.message ?: "Failed to save seller profile") }
            }
            .addOnFailureListener { e -> onFailure(e.message ?: "Seller registration failed") }
    }

    // --- PHOTO UPLOAD (Using Cloudinary) ---

    fun uploadSellerPhoto(
        context: Context,
        imageUri: Uri,
        onSuccess: (String) -> Unit, // returns the URL string
        onFailure: (String) -> Unit
    ) {
        try {
            MediaManager.get()
        } catch (e: Exception) {
            val config = hashMapOf(
                "cloud_name" to BuildConfig.CLOUDINARY_CLOUD_NAME,
                "secure" to true
            )
            MediaManager.init(context, config)
        }

        MediaManager.get().upload(imageUri)
            .option("unsigned", true)
            .option("upload_preset", "craftlanka_preset")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {}
                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                
                override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                    val url = resultData?.get("secure_url") as? String ?: ""
                    onSuccess(url)
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    onFailure(error?.description ?: "Cloudinary upload failed")
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
            })
            .dispatch()
    }

    // --- READING DATA (Retrieval) ---

    fun loginUser(
        email: String,
        password: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid ?: ""
                db.collection("users").document(uid).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val role = document.getString("role") ?: "buyer"
                            onSuccess(role)
                        } else {
                            onFailure("User profile not found")
                        }
                    }
            }
            .addOnFailureListener { e -> onFailure(e.message ?: "Login failed") }
    }

    fun getBuyerProfile(uid: String, onSuccess: (BuyerProfile?) -> Unit) {
        db.collection("buyer_profiles").document(uid).get()
            .addOnSuccessListener { document ->
                onSuccess(document.toObject(BuyerProfile::class.java))
            }
            .addOnFailureListener { onSuccess(null) }
    }

    fun getSellerProfile(uid: String, onSuccess: (SellerProfile?) -> Unit) {
        db.collection("seller_profiles").document(uid).get()
            .addOnSuccessListener { document ->
                onSuccess(document.toObject(SellerProfile::class.java))
            }
            .addOnFailureListener { onSuccess(null) }
    }
}
