package lbr2048.currencyconverter.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lbr2048.currencyconverter.data.local.RatesDatabase
import lbr2048.currencyconverter.data.network.RatesWebService
import lbr2048.currencyconverter.ui.Rate

class RatesRepository(private val database: RatesDatabase) : IRatesRepository {

    private var _refreshState = MutableLiveData<RefreshState>()
    override val refreshState: LiveData<RefreshState> = _refreshState

    override val rates: LiveData<List<Rate>> = database.rateDao.getRates()

    override suspend fun refreshRates() {
        _refreshState.value = RefreshState.LOADING
        try {
            withContext(Dispatchers.IO) {
                val ratesResponse = RatesWebService.retrofitService.getRatesResponseAsync().await()
                database.rateDao.insertAll(ratesResponse.getRates())
            }
            _refreshState.value = RefreshState.SUCCESS
        } catch (e: Exception) {
            _refreshState.value = RefreshState.ERROR
        }
    }

    enum class RefreshState { LOADING, SUCCESS, ERROR }
}