package cl.oursour

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import cl.oursour.database.OurSourDatabase
import cl.oursour.model.FrequentIngredient
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class FrequentPersistenceTest {
    @Test fun saveDeduplicateReopenAndDelete() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "frequents-test.db"
        context.deleteDatabase(name)
        var db = Room.databaseBuilder(context, OurSourDatabase::class.java, name).build()
        try {
            db.frequentIngredientDao().insert(FrequentIngredient(1))
            db.frequentIngredientDao().insert(FrequentIngredient(1))
            assertEquals(listOf(1), db.frequentIngredientDao().observeAll().first().map { it.ingredientId })
            db.close()
            db = Room.databaseBuilder(context, OurSourDatabase::class.java, name).build()
            assertEquals(listOf(1), db.frequentIngredientDao().observeAll().first().map { it.ingredientId })
            db.frequentIngredientDao().delete(1)
            assertTrue(db.frequentIngredientDao().observeAll().first().isEmpty())
        } finally { db.close(); context.deleteDatabase(name) }
    }
}
