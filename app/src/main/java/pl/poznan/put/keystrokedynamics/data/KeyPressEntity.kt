package pl.poznan.put.keystrokedynamics.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "keypress_table")
data class KeyPressEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val key: String,
    val pressTime: Long,
    val duration: Long,
    val accelX: Float,  // Accelerometer X axis
    val accelY: Float,  // Accelerometer Y axis
    val accelZ: Float,  // Accelerometer Z axis
)
