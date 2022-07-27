package com.geek.notekeeper

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.geek.notekeeper.fragment.HomeFragment
import com.geek.notekeeper.fragment.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.realm.Realm
import io.realm.mongodb.User

class SampleResult : AppCompatActivity() {

    private val homeFragment = HomeFragment()
    private val profileFragment = ProfileFragment()
    var user: User? = null
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample_result)

        user = noteApp.currentUser()

        if(user == null) {
            startActivity(Intent(this@SampleResult, LoginActivity::class.java))
        }
        else {

            /*
            val config = SyncConfiguration.Builder(user, "User").build()
            Realm.getInstanceAsync(config, object : Realm.Callback() {
                override fun onSuccess(realm: Realm) {
                    this@SampleResult.realm = realm
                }
            })

            val customUser: com.geek.notekeeper.model.User =
                com.geek.notekeeper.model.User(id = noteApp.currentUser()!!.id, email = intent.getStringExtra("EMAIL").toString())

            realm.executeTransactionAsync { realm ->
                realm.insert(customUser)
            }

             */

            replaceFragment(homeFragment)

            val bottom_navigation: BottomNavigationView = findViewById(R.id.bottom_navigation)

            bottom_navigation.setOnNavigationItemSelectedListener {
                when(it.itemId) {
                    R.id.nav_home -> {
                        replaceFragment(homeFragment)
                    }
                    R.id.nav_profile -> {
                        replaceFragment(profileFragment)
                    }
                }

                true
            }
        }
    }

    /*
    override fun onStart() {
        super.onStart()

        user = noteApp.currentUser()

        if(user == null) {
            startActivity(Intent(this@SampleResult, LoginActivity::class.java))
        }
        else {
            val config = SyncConfiguration.Builder(user, "User")
                .build()

            Realm.getInstanceAsync(config, object : Realm.Callback() {
                override fun onSuccess(realm: Realm) {
                    this@SampleResult.realm = realm

                    val customUser: com.geek.notekeeper.model.User =
                        com.geek.notekeeper.model.User(id = noteApp.currentUser()!!.id, email = intent.getStringExtra("EMAIL").toString())

                    realm.executeTransactionAsync {
                        it.insertOrUpdate(customUser)
                    }
                }
            })

        }
    }

     */

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }

}