package lbr2048.currencyconverter.data.local

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import lbr2048.currencyconverter.ui.Rate

@Dao
interface RateDao {

    @Query("SELECT * FROM rate")
    fun getRates(): LiveData<List<Rate>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(rates: List<Rate>)
}

@Database(entities = [Rate::class], version = 1)
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