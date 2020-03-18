package lbr2048.currencyconverter.data

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lbr2048.currencyconverter.data.local.RatesDatabase
import lbr2048.currencyconverter.data.network.RatesWebService
import lbr2048.currencyconverter.ui.Rate

class RatesRepository(private val database: RatesDatabase) : IRatesRepository {

    override val rates: LiveData<List<Rate>> = database.rateDao.getRates()

    override suspend fun refreshRates() {
        withContext(Dispatchers.IO) {
            val ratesResponse = RatesWebService.retrofitService.getRatesResponseAsync().await()
            database.rateDao.insertAll(ratesResponse.getRates())
            Log.i("JSON_TAG", ratesResponse.getRates().toString())
        }
    }
}