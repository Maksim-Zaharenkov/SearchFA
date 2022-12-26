package com.example.searchfa.sqlite

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserTable::class], version = 1)
abstract class UserDatabase: RoomDatabase() {

    abstract fun getDao(): Dao

    companion object{
         fun getDatabase(context: Context): UserDatabase {
            return Room.databaseBuilder(context.applicationContext,
            UserDatabase::class.java,
            "UserDatabase.db").build()
        }
    }
}