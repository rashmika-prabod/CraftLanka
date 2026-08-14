package com.craftlanka.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.craftlanka.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    lateinit var navigationManager: NavigationManager
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navigationManager = NavigationManager(supportFragmentManager, R.id.fragment_container)

        if (savedInstanceState == null) {
            navigationManager.replaceFragment(
                fragment = SplashFragment(),
                addToBackStack = false
            )
        }
    }
}