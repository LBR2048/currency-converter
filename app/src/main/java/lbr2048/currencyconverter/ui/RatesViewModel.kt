package lbr2048.currencyconverter.ui

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import lbr2048.currencyconverter.data.RatesRepository
import java.util.*
import kotlin.concurrent.schedule

class RatesViewModel(private val repository: RatesRepository) : ViewModel() {

    private lateinit var timer: Timer

    private val _input = MutableLiveData<Rate>()
    private val input: LiveData<Rate>
        get() = _input

    private val rates: LiveData<List<Rate>> = repository.rates

    val result = MediatorLiveData<List<Rate>>()
    private val orderedCurrencies = MutableLiveData<List<Rate>>()

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {

        _input.value = Rate("EUR", 1.0)

        orderedCurrencies.value = listOf(
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

        result.addSource(input) {
            result.value =  combineLatestData(input, rates, orderedCurrencies)
        }
        result.addSource(rates) {
            result.value =  combineLatestData(input, rates, orderedCurrencies)
        }
        result.addSource(orderedCurrencies) {
            result.value =  combineLatestData(input, rates, orderedCurrencies)
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

    fun moveItemToTop(position: Int) {
        Log.i("POSITION_TAG", "Move item $position to top")
        val toMutableList = orderedCurrencies.value?.toMutableList()
        toMutableList?.let {
            val removeAt = toMutableList.removeAt(position)
            toMutableList.add(0, removeAt)
        }
        orderedCurrencies.value = toMutableList
    }

    fun setInput(rate: Rate) {
        Log.i("POSITION_TAG", "Item $rate clicked")
        if (_input.value != rate) {
            _input.value = rate
        }
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

    // TODO improve code, it is confusing
    private fun convertAll(
        input: Rate,
        rates:  List<Rate>,
        orderedRates: List<Rate>
    ): MutableList<Rate> {
        Log.i("CONVERT_TAG", "Convert $input")

        val newRates: MutableList<Rate> = mutableListOf()
        orderedRates.map {
            newRates.add(
                Rate(
                    it.currencyCode, convert(
                        input.value,
                        input.currencyCode,
                        it.currencyCode,
                        rates.asMap()
                    )
                )
            )
        }

        return newRates
    }

    private fun convert(
        value: Double?,
        inputCurrency: String,
        outputCurrency: String,
        ratesMap: Map<String, Double?>
    ): Double? {
        return when {
            value == null -> null
            inputCurrency == outputCurrency -> value
            else -> value / ratesMap[inputCurrency]!! * ratesMap[outputCurrency]!!
        }
    }

    private fun getExchangeRatesFromRepository() {
        coroutineScope.launch {
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