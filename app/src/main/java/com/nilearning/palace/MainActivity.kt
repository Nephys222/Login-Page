package com.nilearning.palace

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.view.MenuProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.nilearning.palace.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // Add menu items without overriding methods in the Activity
//        addMenuProvider(object : MenuProvider {
//            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//                // Add menu items here
//                menuInflater.inflate(R.menu.profile_menu, menu)
//            }
//
//            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
//                // Handle the menu selection
//                return true
//            }
//        })

        binding.fab.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.findNavController()

        binding.navView.setupWithNavController(navController)

        navController.graph.setStartDestination(R.id.navigation_home)
//        binding.navView.setOnApplyWindowInsetsListener(null)

        binding.navView.setOnItemSelectedListener {
            // clear backstack if it's the start fragment
            if (R.id.navigation_home == it.itemId) navController.backQueue.clear()

            when (it.itemId) {
                R.id.navigation_home -> {
                    navController.navigate(R.id.navigation_home)
                }
                R.id.navigation_dashboard -> {
                    navController.navigate(R.id.navigation_dashboard)
                }
                R.id.navigation_cart -> {
                    navController.navigate(R.id.navigation_cart)
                }
                R.id.navigation_profile -> {
                    navController.navigate(R.id.navigation_profile)

                }
            }
            false
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_home -> binding.toolbar.setTitle(R.string.title_home)
                R.id.navigation_dashboard -> binding.toolbar.setTitle(R.string.title_dashboard)
                R.id.navigation_cart -> binding.toolbar.setTitle(R.string.title_cart)
                R.id.navigation_profile -> {
//                    binding.toolbar.inflateMenu(R.menu.profile_menu)
                    binding.toolbar.setTitle(R.string.title_profile)
                }
            }
        }
    }
}

//        val navView: BottomNavigationView = binding.navView
//
//        val navController = findNavController(R.id.nav_host_fragment_activity_main)
//
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_home, R.id.navigation_dashboard,
//                R.id.navigation_notifications,
//                R.id.navigation_profile
//            )
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)

//        val intentFragment = intent.extras?.getInt("frag")
//        if (intentFragment != null) {
//            navController.navigate(intentFragment)
//            intent.removeExtra("frag")
//        }