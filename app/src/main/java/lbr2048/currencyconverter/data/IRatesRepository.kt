package lbr2048.currencyconverter.data

import androidx.lifecycle.LiveData
import lbr2048.currencyconverter.ui.Rate

interface IRatesRepository {

    val rates: LiveData<List<Rate>>

    val refreshState: LiveData<RatesRepository.RefreshState>

    suspend fun refreshRates()
}