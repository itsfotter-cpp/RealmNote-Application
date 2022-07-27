package com.geek.notekeeper.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.geek.notekeeper.LoginActivity
import com.geek.notekeeper.R
import com.geek.notekeeper.TAG
import com.geek.notekeeper.model.User
import com.geek.notekeeper.noteApp
import io.realm.mongodb.AppConfiguration
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.fragment_profile.*
import org.bson.Document
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.pojo.PojoCodecProvider

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var name: String? = null
    private var surname: String? = null


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
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_profile, container, false)

        retrieveUserData()

        view.btn_updateProfile.setOnClickListener {
            if(name != et_name.text.toString() || surname != et_surname.text.toString()) {

                name = et_name.text.toString()
                surname = et_surname.text.toString()
                updateUserProfile(name!!, surname!!)
            }

            else {
                Log.i("INFO_ON_UPDATE", "nothing is modified")
            }

            et_name.onEditorAction(EditorInfo.IME_ACTION_DONE)
            et_surname.onEditorAction(EditorInfo.IME_ACTION_DONE)

        }

        view.btn_logout.setOnClickListener {

            Log.i("USER_LOG", noteApp.currentUser().toString())

            noteApp.currentUser()!!.logOutAsync {
                if(it.isSuccess) {
                    Log.v(TAG(), "user logged out")
                    startActivity(Intent(context, LoginActivity::class.java))
                    activity!!.finish()
                }
                else {
                    Log.e(TAG(), "log out failed! Error: ${it.error}")
                }
            }



        }


        return view
    }

    private fun retrieveUserData() {
        val user = noteApp.currentUser()
        val mongoClient =
            user!!.getMongoClient("mongodb-atlas")
        val mongoDatabase =
            mongoClient.getDatabase("NoteDB")
        // registry to handle POJOs (Plain Old Java Objects)
        val pojoCodecRegistry = CodecRegistries.fromRegistries(
            AppConfiguration.DEFAULT_BSON_CODEC_REGISTRY,
            CodecRegistries.fromProviders(
                PojoCodecProvider.builder().automatic(true).build()))
        val mongoCollection =
            mongoDatabase.getCollection(
                "User",
                User::class.java).withCodecRegistry(pojoCodecRegistry)
        Log.v("EXAMPLE", "Successfully instantiated the MongoDB collection handle")

        val queryFilter = Document("_id", noteApp.currentUser()!!.id)
        mongoCollection.findOne(queryFilter)
            .getAsync { task ->
                if (task.isSuccess) {
                    val result = task.get()
                    name = result.name
                    surname = result.surname
                    et_email.setText(result.email)
                    et_name.setText(result.name)
                    et_surname.setText(result.surname)
                    Log.v("EXAMPLE_GOOD", "successfully found a document: $result")
                } else {
                    Log.v("EXAMPLE_ERROR", "failed to find document with: ${task.error}")
                }
            }
    }

    private fun updateUserProfile(name: String, surname: String) {
        val user = noteApp.currentUser()
        val mongoClient =
            user!!.getMongoClient("mongodb-atlas")
        val mongoDatabase =
            mongoClient.getDatabase("NoteDB")
        // registry to handle POJOs (Plain Old Java Objects)
        val pojoCodecRegistry = CodecRegistries.fromRegistries(
            AppConfiguration.DEFAULT_BSON_CODEC_REGISTRY,
            CodecRegistries.fromProviders(
                PojoCodecProvider.builder().automatic(true).build()))
        val mongoCollection =
            mongoDatabase.getCollection(
                "User",
                User::class.java).withCodecRegistry(pojoCodecRegistry)
        Log.v("EXAMPLE", "Successfully instantiated the MongoDB collection handle")

        val queryFilter = Document("_id", noteApp.currentUser()!!.id)
        val updateDocument = Document("\$set", Document("name", name))//.append("\$set", Document("surname", surname))
        mongoCollection.updateOne(queryFilter, updateDocument).getAsync { task ->
            if (task.isSuccess) {
                val count = task.get().modifiedCount
                if (count == 1L) {
                    Log.v("EXAMPLE", "successfully updated a document.")
                } else {
                    Log.v("EXAMPLE", "did not update a document.")
                }
            } else {
                Log.e("EXAMPLE", "failed to update document with: ${task.error}")
            }
        }
        val updateDocument2 = Document("\$set", Document("surname", surname))
        mongoCollection.updateOne(queryFilter, updateDocument2).getAsync { task ->
            if (task.isSuccess) {
                val count = task.get().modifiedCount
                if (count == 1L) {
                    Log.v("EXAMPLE", "successfully updated a document.")
                } else {
                    Log.v("EXAMPLE", "did not update a document.")
                }
            } else {
                Log.e("EXAMPLE", "failed to update document with: ${task.error}")
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}