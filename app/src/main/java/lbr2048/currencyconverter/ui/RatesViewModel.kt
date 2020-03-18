package lbr2048.currencyconverter.ui

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import lbr2048.currencyconverter.convertAll
import lbr2048.currencyconverter.data.IRatesRepository
import lbr2048.currencyconverter.data.RatesRepository
import java.util.*
import kotlin.concurrent.schedule

class RatesViewModel(private val repository: IRatesRepository) : ViewModel() {

    val result = MediatorLiveData<Result<List<Rate>>>()

    private val rates: LiveData<List<Rate>> = repository.rates
    private val screenRates = MutableLiveData<List<Rate>>()
    private val inputRate = MutableLiveData<Rate>()

    private var viewModelJob = Job()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private lateinit var timer: Timer

    init {

        inputRate.value = Rate("EUR", 1.0)

        screenRates.value = listOf(
            Rate(currencyCode = "AUD"),
            Rate(currencyCode = "EUR"),
            Rate(currencyCode = "BGN"),
            Rate(currencyCode = "BRL"),
            Rate(currencyCode = "CAD"),
            Rate(currencyCode = "CHF"),
            Rate(currencyCode = "CNY"),
            Rate(currencyCode = "CZK"),
            Rate(currencyCode = "DKK"),
            Rate(currencyCode = "GBP"),
            Rate(currencyCode = "HKD"),
            Rate(currencyCode = "HRK"),
            Rate(currencyCode = "HUF"),
            Rate(currencyCode = "IDR"),
            Rate(currencyCode = "ILS"),
            Rate(currencyCode = "INR"),
            Rate(currencyCode = "ISK"),
            Rate(currencyCode = "JPY"),
            Rate(currencyCode = "KRW"),
            Rate(currencyCode = "MXN"),
            Rate(currencyCode = "MYR"),
            Rate(currencyCode = "NOK"),
            Rate(currencyCode = "NZD"),
            Rate(currencyCode = "PHP"),
            Rate(currencyCode = "PLN"),
            Rate(currencyCode = "RON"),
            Rate(currencyCode = "RUB"),
            Rate(currencyCode = "SEK"),
            Rate(currencyCode = "SGD"),
            Rate(currencyCode = "THB"),
            Rate(currencyCode = "USD"),
            Rate(currencyCode = "ZAR")
        )

        result.addSource(inputRate) {
            result.value = Result(combineLatestData(inputRate, rates, screenRates), false)
        }
        result.addSource(rates) {
            result.value = Result(combineLatestData(inputRate, rates, screenRates), false)
        }
        result.addSource(screenRates) {
            result.value = Result(combineLatestData(inputRate, rates, screenRates), true)
        }
    }

    fun startTimer() {
        timer = Timer()
        timer.schedule(0, 1000) {
            getExchangeRatesFromRepository()
        }
    }

    fun cancelTimer() {
        timer.cancel()
    }

    fun setInput(rate: Rate, position: Int) {
        Log.i("POSITION_TAG", "Item $rate clicked")
        if (inputRate.value != rate) {
            inputRate.value = rate
        }
        if (position != 0) moveItemToTop(position)
    }

    private fun moveItemToTop(position: Int) {
        Log.i("POSITION_TAG", "Move item $position to top")
        val toMutableList = screenRates.value?.toMutableList()
        toMutableList?.let {
            val removeAt = toMutableList.removeAt(position)
            toMutableList.add(0, removeAt)
        }
        screenRates.value = toMutableList
    }

    private fun combineLatestData(
        inputResult: LiveData<Rate>,
        ratesResult: LiveData<List<Rate>>,
        orderedCurrenciesResult: LiveData<List<Rate>>
    ): List<Rate> {
        val input = inputResult.value
        val rates = ratesResult.value
        val orderedCurrencies = orderedCurrenciesResult.value

        if (input == null || rates.isNullOrEmpty() || orderedCurrencies.isNullOrEmpty()) {
            // TODO show error
            return emptyList()
        }

        return convertAll(input, rates, orderedCurrencies)
    }

    // https://codelabs.developers.google.com/codelabs/advanced-android-kotlin-training-testing-test-doubles/#3
    private fun getExchangeRatesFromRepository() {
        viewModelScope.launch {
            try {
                repository.refreshRates()
            } catch (e: Exception) {

            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        timer.cancel()
    }

    class Factory(val repository: RatesRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RatesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return RatesViewModel(repository) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}