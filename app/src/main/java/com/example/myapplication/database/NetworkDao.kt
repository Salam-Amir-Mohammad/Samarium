package com.example.myapplication.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NetworkInfoDao {
    @Insert
    suspend fun insert(networkInfo: NetworkInfo)

    @Query("SELECT * FROM network_info")
    suspend fun getAll(): List<NetworkInfo>
}
