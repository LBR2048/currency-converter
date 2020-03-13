package lbr2048.currencyconverter.data

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lbr2048.currencyconverter.data.local.RatesDatabase
import lbr2048.currencyconverter.data.network.CurrenciesWeb
import lbr2048.currencyconverter.ui.Rate

class RatesRepository(private val database: RatesDatabase) {

    val rates: LiveData<List<Rate>> = database.rateDao.getRates()

    suspend fun refreshRates() {
        withContext(Dispatchers.IO) {
            val ratesResponse = CurrenciesWeb.retrofitService.getCurrencies().await()
            database.rateDao.insertAll(ratesResponse.rates.getRates())
        }
    }
}