package lbr2048.currencyconverter

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lbr2048.currencyconverter.database.RatesDatabase

class CurrenciesRepository(private val database: RatesDatabase) {

    val rates: LiveData<List<Currency>> = database.rateDao.getRates()

    suspend fun refreshRates() {
        withContext(Dispatchers.IO) {
            val ratesResponse = CurrenciesWeb.retrofitService.getCurrencies().await()
            database.rateDao.insertAll(ratesResponse.rates.getRates())
        }
    }
}