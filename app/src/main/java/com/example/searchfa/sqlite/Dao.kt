package com.example.searchfa.sqlite

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {

    @Insert
    fun insertUser(user: UserTable)

    @Query("SELECT * FROM user")
    fun getUser(): List<UserTable>

    @Query("DELETE FROM user")
    fun deleteUser()
}