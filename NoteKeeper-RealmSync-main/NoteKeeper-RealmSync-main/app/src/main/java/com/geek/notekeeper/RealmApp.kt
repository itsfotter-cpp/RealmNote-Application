package com.geek.notekeeper

import android.app.Application
import io.realm.Realm
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration

const val appId ="notekeeper-pvwmt" //Enter your AppID here
lateinit var noteApp: App
inline fun <reified T> T.TAG(): String = T::class.java.simpleName


class RealmApp: Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)

        noteApp = App(AppConfiguration.Builder(appId).build())

    }
}