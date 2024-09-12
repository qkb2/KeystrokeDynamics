package pl.poznan.put.keystrokedynamics.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface KeyPressDao {
    @Insert
    suspend fun insert(keyPressEntity: KeyPressEntity)

    @Query("SELECT * FROM keypress_table")
    suspend fun getAllKeyPresses(): List<KeyPressEntity>
}
