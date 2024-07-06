package com.example.myapplication.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.myapplication.MainActivity

@Dao
interface NetworkInfoDao {
    @Insert
    suspend fun insert(networkInfo: NetworkInfo)
    @Query("SELECT COUNT(*) FROM network_info WHERE batchId = :batchId AND latitude = :latitude AND longitude = :longitude")
    fun countByBatchAndLocation(batchId: Int, latitude: Double, longitude: Double): Int

    @Query("SELECT * FROM network_info")
    suspend fun getAll(): List<NetworkInfo>

    @Query("DELETE FROM network_info")
    suspend fun clearAll()

    @Query("UPDATE network_info SET batchId = :batchId WHERE id = :id")
    suspend fun updateBatchId(id: Long, batchId: Int)

    @Query("SELECT MAX(batchId) FROM network_info")
    suspend fun getMaxBatchId(): Int?

    @Query("SELECT DISTINCT batchId FROM network_info")
    suspend fun getAllBatchIds(): List<Int>

    @Query("SELECT * FROM network_info WHERE batchId = :batchId")
    suspend fun getNetworkInfosByBatchId(batchId: Int): List<NetworkInfo>


    @Query("""
    SELECT batchId, GROUP_CONCAT(qualityOfService) as qualities, GROUP_CONCAT(cellTechnology) as technologies
    FROM network_info
    GROUP BY batchId
    ORDER BY batchId DESC
    LIMIT 5
""")
    fun getTopBatchIdsWithDetails(): List<MainActivity.BatchDetails>

    @Query("""
    SELECT batchId, GROUP_CONCAT(qualityOfService) as qualities, GROUP_CONCAT(cellTechnology) as technologies
    FROM network_info
    GROUP BY batchId
    ORDER BY batchId DESC
    LIMIT 50
""")
    fun getTopBatchIdsWithDetails_50(): List<MainActivity.BatchDetails>



}
