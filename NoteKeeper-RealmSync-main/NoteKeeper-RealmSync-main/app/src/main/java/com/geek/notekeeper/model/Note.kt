package com.geek.notekeeper.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.bson.types.ObjectId
import java.util.*

open class Note(
    var noteName: String = "",
    var date: Date = Date(),
    var _partition: String = "Public",
    var owner: String = ""
):RealmObject() {

    @PrimaryKey
    var _id: ObjectId = ObjectId()
}
