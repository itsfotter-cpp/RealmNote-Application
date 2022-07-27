package com.geek.notekeeper.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.geek.notekeeper.LoginActivity
import com.geek.notekeeper.R
import com.geek.notekeeper.model.Note
import com.geek.notekeeper.model.NoteAdapter
import com.geek.notekeeper.noteApp
import io.realm.Realm
import io.realm.kotlin.where
import io.realm.mongodb.User
import io.realm.mongodb.sync.SyncConfiguration
import kotlinx.android.synthetic.main.activity_task.*
import kotlinx.android.synthetic.main.fragment_home.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var adapter: NoteAdapter
    private lateinit var realm: Realm
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        // create a dialog to enter a task name when the floating action button is clicked

        view.fab.setOnClickListener {
            val input = EditText(context)
            val dialogBuilder = AlertDialog.Builder(context!!)
            dialogBuilder.setMessage("Enter your Note: ")
                .setCancelable(true)
                .setPositiveButton("Create") { dialog, _ ->
                    dialog.dismiss()

                    val note = Note(input.text.toString())
                    note.owner = noteApp.currentUser()!!.id

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
        // Inflate the layout for this fragment
        return view
    }

    override fun onStart() {
        super.onStart()

        user = noteApp.currentUser()

        if(user == null) {
            startActivity(Intent(context, LoginActivity::class.java))
        }
        else {
            val config = SyncConfiguration.Builder(user, "Public")
                .build()

            Realm.getInstanceAsync(config, object : Realm.Callback() {
                override fun onSuccess(realm: Realm) {
                    this@HomeFragment.realm = realm
                    setUpRecyclerView(realm)
                }

            })
        }

    }

    /*
    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
     */

    private fun setUpRecyclerView(realm: Realm) {

        adapter = NoteAdapter(realm.where<Note>().sort("date").findAll())//.equalTo("owner", user!!.id)
        rv_tasks.layoutManager = LinearLayoutManager(context)
        rv_tasks.adapter = adapter
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}