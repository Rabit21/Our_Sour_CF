package cl.oursour.database

import android.content.Context
import androidx.room.*
import cl.oursour.dao.FrequentIngredientDao
import cl.oursour.model.FrequentIngredient

@Database(entities = [FrequentIngredient::class], version = 1, exportSchema = false)
abstract class OurSourDatabase : RoomDatabase() {
    abstract fun frequentIngredientDao(): FrequentIngredientDao
    companion object {
        @Volatile private var instance: OurSourDatabase? = null
        fun getInstance(context: Context): OurSourDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context.applicationContext, OurSourDatabase::class.java, "oursour.db")
                .build().also { instance = it }
        }
    }
}
