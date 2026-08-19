package com.craftlanka.app.seller

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.craftlanka.app.MainActivity
import com.craftlanka.app.databinding.LayoutDeleteAccountDialogBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DeleteAccountDialog : DialogFragment() {

    private var _binding: LayoutDeleteAccountDialogBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = LayoutDeleteAccountDialogBinding.inflate(inflater, container, false)
        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            requestFeature(Window.FEATURE_NO_TITLE)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnGoBack.setOnClickListener {
            dismiss()
        }

        binding.btnConfirmDelete.setOnClickListener {
            deleteAccount()
        }
    }

    private fun deleteAccount() {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid ?: return

        binding.btnConfirmDelete.isEnabled = false

        // 1. Delete Firestore Data
        val db = FirebaseFirestore.getInstance()
        db.collection("seller_profiles").document(uid).delete()
            .addOnSuccessListener {
                db.collection("users").document(uid).delete().addOnSuccessListener {
                    // 2. Delete Auth Account
                    user.delete().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(requireContext(), "Account Deleted Successfully", Toast.LENGTH_LONG).show()
                            logoutAndExit()
                        } else {
                            Toast.makeText(requireContext(), "Auth Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            binding.btnConfirmDelete.isEnabled = true
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Delete failed: ${e.message}", Toast.LENGTH_SHORT).show()
                binding.btnConfirmDelete.isEnabled = true
            }
    }

    private fun logoutAndExit() {
        val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()

        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        dismiss()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
