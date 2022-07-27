package com.geek.notekeeper.model

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.geek.notekeeper.R
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter

class NoteAdapter(var notes: OrderedRealmCollection<Note>) :
    RealmRecyclerViewAdapter<Note, NoteHolder>(notes, true) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.task_item_view, parent, false)
        return NoteHolder(view)

    }

    override fun getItemCount(): Int = notes.size


    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        val note = notes[position]
        holder.bindValues(note)
    }

    fun updateData(list: List<Note>) {
        notes = list as OrderedRealmCollection<Note>
        notifyDataSetChanged()
    }
}