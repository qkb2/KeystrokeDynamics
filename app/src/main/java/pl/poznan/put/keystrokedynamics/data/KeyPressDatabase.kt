package pl.poznan.put.keystrokedynamics.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [KeyPressEntity::class], version = 1, exportSchema = false)
abstract class KeyPressDatabase : RoomDatabase() {

    abstract fun keyPressDao(): KeyPressDao

    companion object {
        @Volatile
        private var INSTANCE: KeyPressDatabase? = null

        fun getDatabase(context: Context): KeyPressDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KeyPressDatabase::class.java,
                    "keypress_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}