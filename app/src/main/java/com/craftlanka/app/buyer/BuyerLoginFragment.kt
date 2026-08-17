package com.craftlanka.app.buyer

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.craftlanka.app.BuildConfig
import com.craftlanka.app.MainActivity
import com.craftlanka.app.RoleSelectionFragment
import com.craftlanka.app.data.AuthRepository
import com.craftlanka.app.databinding.FragmentBuyerLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth

class BuyerLoginFragment : Fragment() {
    private var bindingVar: FragmentBuyerLoginBinding? = null
    private val binding get() = bindingVar!!

    private val authRepository = AuthRepository()
    private lateinit var googleSignInClient: GoogleSignInClient

    // Safely loaded from BuildConfig generated via local.properties
    private val webClientId = BuildConfig.WEB_CLIENT_ID

    private val googleSignInLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    val idToken = account?.idToken
                    if (idToken != null) {
                        authRepository.signInWithGoogle(
                            idToken = idToken,
                            targetRole = "buyer",
                            onSuccess = { role: String ->
                                // FIX: Verify user role is "buyer"
                                if (role == "buyer") {
                                    val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                                    prefs.edit { putString("user_role", role) }

                                    Toast.makeText(requireContext(), "Google Sign-In successful!", Toast.LENGTH_SHORT).show()

                                    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                                    authRepository.getBuyerProfile(uid) { profile ->
                                        val name = profile?.fullName ?: "Buyer"
                                        Toast.makeText(requireContext(), "Welcome back, $name!", Toast.LENGTH_SHORT).show()
                                        // TODO: Navigate to Buyer Dashboard
                                    }
                                } else {
                                    // Wrong role: Sign out and inform user
                                    FirebaseAuth.getInstance().signOut()
                                    googleSignInClient.signOut()
                                    Toast.makeText(
                                        requireContext(),
                                        "This is not a Buyer account. Please use Seller Login.",
                                        Toast.LENGTH_LONG,
                                    ).show()
                                }
                            },
                            onFailure = { error: String ->
                                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
                            },
                        )
                    }
                } catch (e: ApiException) {
                    Toast.makeText(requireContext(), "Google Sign-In failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        bindingVar = FragmentBuyerLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId)
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        // Google Sign-In Action
        binding.btnGoogleLogin.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }

        // Standard Email Login Action
        binding.btnLogin.setOnClickListener {
            if (validateInput()) {
                performBuyerLogin()
            }
        }

        // Redirect to Account Creation Flow
        binding.btnGotoRegister.setOnClickListener {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.navigationManager.replaceFragment(
                fragment = RoleSelectionFragment(),
                addToBackStack = true,
            )
        }
    }

    private fun performBuyerLogin() {
        val email = binding.etIdentifier.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val rememberMe = binding.cbRememberMe.isChecked

        binding.btnLogin.isEnabled = false

        authRepository.loginUser(
            email = email,
            password = password,
            onSuccess = { role: String ->
                // FIX: Verify user role is "buyer"
                if (role == "buyer") {
                    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

                    authRepository.getBuyerProfile(uid) { profile ->
                        val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                        prefs.edit {
                            putString("user_role", role)
                            putString("logged_user", email)
                            putString("user_name", profile?.fullName ?: "")
                            putBoolean("remember_me", rememberMe)
                        }

                        val displayName = profile?.fullName ?: "Buyer"
                        Toast.makeText(requireContext(), "Welcome back, $displayName!", Toast.LENGTH_SHORT).show()

                        binding.btnLogin.isEnabled = true
                        // TODO: Navigate to Buyer Dashboard
                    }
                } else {
                    // Wrong role: Re-enable UI, sign out, and inform user
                    binding.btnLogin.isEnabled = true
                    FirebaseAuth.getInstance().signOut()
                    Toast.makeText(requireContext(), "This is not a Buyer account. Please use Seller Login.", Toast.LENGTH_LONG).show()
                }
            },
            onFailure = { errorMessage: String ->
                binding.btnLogin.isEnabled = true
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
            },
        )
    }

    private fun validateInput(): Boolean {
        val identifier = binding.etIdentifier.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (identifier.isEmpty()) {
            binding.tilIdentifier.error = "Please enter your email address"
            return false
        }
        binding.tilIdentifier.error = null

        if (password.isEmpty()) {
            binding.tilPassword.error = "Please enter your password"
            return false
        }
        binding.tilPassword.error = null

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingVar = null
    }
}
