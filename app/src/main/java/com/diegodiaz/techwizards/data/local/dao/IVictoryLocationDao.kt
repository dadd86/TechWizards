package com.diegodiaz.techwizards.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diegodiaz.techwizards.data.local.entity.VictoryLocationEntity

@Dao
interface IVictoryLocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: VictoryLocationEntity): Long

    @Query(
        """
            SELECT * FROM victory_location
            WHERE matchId = :matchId
            ORDER BY timestampMillis DESC
        """
    )
    suspend fun getByMatch(matchId: Long): List<VictoryLocationEntity>
}
