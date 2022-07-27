package com.geek.notekeeper

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.geek.notekeeper.model.Note
import com.geek.notekeeper.model.NoteAdapter
import io.realm.Realm
import io.realm.kotlin.where
import io.realm.mongodb.User
import io.realm.mongodb.sync.SyncConfiguration
import kotlinx.android.synthetic.main.activity_task.*

class TaskActivity : AppCompatActivity() {

    private lateinit var adapter: NoteAdapter
    //private var noteList = ArrayList<Note>()

    private lateinit var realm: Realm
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)


        //   // create a dialog to enter a task name when the floating action button is clicked
        fab.setOnClickListener {
            val input = EditText(this)
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage("Enter your Note: ")
                .setCancelable(true)
                .setPositiveButton("Create") { dialog, _ ->
                    dialog.dismiss()

                    val note = Note(input.text.toString())

                    realm.executeTransactionAsync { realm ->
                        realm.insert(note)
                    }
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }
            val dialog = dialogBuilder.create()
            dialog.setView(input)
            dialog.setTitle("Create New Note")
            dialog.show()
        }
    }

    override fun onStart() {
        super.onStart()

        user = noteApp.currentUser()

        if(user == null) {
            startActivity(Intent(this@TaskActivity, LoginActivity::class.java))
        }
        else {
            val config = SyncConfiguration.Builder(user, "Public")
                .build()

            Realm.getInstanceAsync(config, object : Realm.Callback() {
                override fun onSuccess(realm: Realm) {
                    this@TaskActivity.realm = realm
                    setUpRecyclerView(realm)
                }

            })
        }

    }

    private fun setUpRecyclerView(realm: Realm) {

        adapter = NoteAdapter(realm.where<Note>().sort("date").findAll())
        rv_tasks.layoutManager = LinearLayoutManager(this)
        rv_tasks.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

}
