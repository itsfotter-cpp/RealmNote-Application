package com.geek.notekeeper.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmField

open class User(
    @PrimaryKey @RealmField("_id") var id: String = "",
    var _partition: String = "User",
    var name: String = "",
    var surname: String = "",
    var email: String = ""
): RealmObject() {

    override fun toString(): String {
        return "User [name=$name, surname=$surname, email=$email]"
    }
}
