package com.geek.notekeeper

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.geek.notekeeper.model.User
import io.realm.Realm
import io.realm.mongodb.Credentials
import io.realm.mongodb.sync.SyncConfiguration

class LoginActivity : AppCompatActivity() {

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button
    private lateinit var createUserButton: Button

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        username = findViewById(R.id.input_username)
        password = findViewById(R.id.input_password)
        loginButton = findViewById(R.id.button_login)
        createUserButton = findViewById(R.id.button_create)

        loginButton.setOnClickListener { login(false) }
        createUserButton.setOnClickListener { login(true) }
    }

    override fun onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true)
    }

    private fun onLoginSuccess() {
        //Log.i("USER_LOG", noteApp.currentUser().toString())
        // successful login ends this activity, bringing the user back to the project activity
        val config2 = SyncConfiguration.Builder(noteApp.currentUser(), "User")
            .build()

        Realm.getInstanceAsync(config2, object : Realm.Callback() {
            override fun onSuccess(realm: Realm) {
                val customUser: com.geek.notekeeper.model.User =
                    com.geek.notekeeper.model.User(id = noteApp.currentUser()!!.id, email = username.text.toString().trim())

                realm.executeTransactionAsync {
                    it.insertOrUpdate(customUser)
                }
            }
        })
        val intent = Intent(this@LoginActivity, SampleResult::class.java)
        intent.putExtra("EMAIL", username.text.toString())
        startActivity(intent)
        finish()
    }

    private fun onLoginFailed(errorMsg: String) {
        Log.e(TAG(), errorMsg)
        Toast.makeText(baseContext, errorMsg, Toast.LENGTH_LONG).show()
    }

    private fun validateCredentials(): Boolean = when {
        // zero-length usernames and passwords are not valid (or secure), so prevent users from creating accounts with those client-side.
        username.text.toString().isEmpty() -> false
        password.text.toString().isEmpty() -> false
        else -> true
    }

    // handle user authentication (login) and account creation
    private fun login(createUser: Boolean) {
        if (!validateCredentials()) {
            onLoginFailed("Invalid username or password")
            return
        }

        // while this operation completes, disable the buttons to login or create a new account
        createUserButton.isEnabled = false
        loginButton.isEnabled = false

        val username = this.username.text.toString()
        val password = this.password.text.toString()


        if (createUser) {
            // register a user using the Realm App we created in the TaskTracker class
            noteApp.emailPassword.registerUserAsync(username.trim(), password) {
                // re-enable the buttons after user registration returns a result
                createUserButton.isEnabled = true
                loginButton.isEnabled = true
                if (!it.isSuccess) {
                    onLoginFailed("Could not register user.")
                    Log.e(TAG(), "Error: ${it.error}")
                } else {
                    Log.i(TAG(), "Successfully registered user.")

                    // when the account has been created successfully, log in to the account
                    login(false)
                }
            }
        } else {
            val creds = Credentials.emailPassword(username.trim(), password)
            noteApp.loginAsync(creds) {
                // re-enable the buttons after user login returns a result
                loginButton.isEnabled = true
                createUserButton.isEnabled = true
                if (!it.isSuccess) {
                    onLoginFailed(it.error.message ?: "An error occurred.")
                } else {
                    val config = SyncConfiguration.Builder(it.get(), it.get().id)
                        .build()
                    Realm.getInstanceAsync(config, object : Realm.Callback() {
                        override fun onSuccess(realm: Realm) {
                            realm.executeTransactionAsync{
                                var user = it.where(User::class.java).findFirst()
                                if(user != null) {
                                    user.name = username
                                }
                                else {
                                    user = User().apply { this.name = name }
                                }

                                it.copyToRealmOrUpdate(user).apply {
                                    name = username
                                }

                            }
                        }

                    })

                    onLoginSuccess()
                }
            }
        }
    }
}

/*
val customUser: com.geek.notekeeper.model.User =
                    com.geek.notekeeper.model.User(id = noteApp.currentUser()!!.id, email = intent.getStringExtra("EMAIL").toString())

                realm.executeTransactionAsync {
                    it.insertOrUpdate(customUser)
                }
 */