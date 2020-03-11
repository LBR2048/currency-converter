package lbr2048.currencyconverter.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import lbr2048.currencyconverter.Currency

@Dao
interface RateDao {

    @Query("SELECT * FROM currency")
    fun getRates(): LiveData<List<Currency>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(rates: List<Currency>)
}

@Database(entities = [Currency::class], version = 1)
abstract class RatesDatabase: RoomDatabase() {
    abstract val rateDao: RateDao
}

private lateinit var INSTANCE: RatesDatabase

fun getDatabase(context: Context): RatesDatabase {
    synchronized(RatesDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                RatesDatabase::class.java,
                "videos").build()
        }
    }
    return INSTANCE
}