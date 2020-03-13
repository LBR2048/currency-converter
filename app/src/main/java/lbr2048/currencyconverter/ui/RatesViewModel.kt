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
    private val _inputValue = MutableLiveData<Double>()

    val inputValue: LiveData<Double>
        get() = _inputValue
    private val _inputCurrency = MutableLiveData<String>()

    val inputCurrency: LiveData<String>
        get() = _inputCurrency

    private val rates: LiveData<List<Rate>> = repository.rates

    val result = MediatorLiveData<List<Rate>>()
    private val orderedCurrencies = MutableLiveData<List<Rate>>()

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {

        setInputValueAndCurrency(1.0, "EUR")

        orderedCurrencies.value = listOf(
            Rate(id = "AUD"),
            Rate(id = "EUR"),
            Rate(id = "BGN"),
            Rate(id = "BRL"),
            Rate(id = "CAD"),
            Rate(id = "CHF"),
            Rate(id = "CNY"),
            Rate(id = "CZK"),
            Rate(id = "DKK"),
            Rate(id = "GBP"),
            Rate(id = "HKD"),
            Rate(id = "HRK"),
            Rate(id = "HUF"),
            Rate(id = "IDR"),
            Rate(id = "ILS"),
            Rate(id = "INR"),
            Rate(id = "ISK"),
            Rate(id = "JPY"),
            Rate(id = "KRW"),
            Rate(id = "MXN"),
            Rate(id = "MYR"),
            Rate(id = "NOK"),
            Rate(id = "NZD"),
            Rate(id = "PHP"),
            Rate(id = "PLN"),
            Rate(id = "RON"),
            Rate(id = "RUB"),
            Rate(id = "SEK"),
            Rate(id = "SGD"),
            Rate(id = "THB"),
            Rate(id = "USD"),
            Rate(id = "ZAR")
        )

        result.addSource(inputValue) {
            result.value =  combineLatestData(inputValue, inputCurrency, rates, orderedCurrencies)
        }
        result.addSource(inputCurrency) {
            result.value =  combineLatestData(inputValue, inputCurrency, rates, orderedCurrencies)
        }
        result.addSource(rates) {
            result.value =  combineLatestData(inputValue, inputCurrency, rates, orderedCurrencies)
        }
        result.addSource(orderedCurrencies) {
            result.value =  combineLatestData(inputValue, inputCurrency, rates, orderedCurrencies)
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

    fun setInputValue(value: Double) {
        if (_inputValue.value != value) {
            _inputValue.value = value
        }
    }

    fun setInputCurrency(currency: String) {
        if (_inputCurrency.value != currency) {
            _inputCurrency.value = currency
        }
    }

    fun setInputValueAndCurrency(value: Double, currency: String) {
        Log.i("POSITION_TAG", "Item $currency clicked")
        if (_inputValue.value != value) {
            _inputValue.value = value
        }
        if (_inputCurrency.value != currency) {
            _inputCurrency.value = currency
        }
    }

    private fun combineLatestData(
        inputValueResult: LiveData<Double>,
        inputCurrencyResult: LiveData<String>,
        ratesResult: LiveData<List<Rate>>,
        orderedCurrenciesResult: LiveData<List<Rate>>
    ): List<Rate> {
        val inputValue = inputValueResult.value
        val inputCurrency = inputCurrencyResult.value
        val rates = ratesResult.value
        val orderedCurrencies = orderedCurrenciesResult.value

        if (inputValue == null || inputCurrency == null || rates.isNullOrEmpty() || orderedCurrencies.isNullOrEmpty()) {
            // TODO show error
            return emptyList()
        }

        return convertAll(inputValue, inputCurrency, rates, orderedCurrencies)
    }

    // TODO improve code, it is confusing
    private fun convertAll(
        value: Double,
        inputCurrency: String,
        rates:  List<Rate>,
        orderedRates: List<Rate>
    ): MutableList<Rate> {
        Log.i("CONVERT_TAG", "Convert $value from $inputCurrency")

        val newRates: MutableList<Rate> = mutableListOf()
        orderedRates.map {
            newRates.add(
                Rate(
                    it.id, convert(
                        value,
                        inputCurrency,
                        it.id,
                        rates.asMap()
                    )
                )
            )
        }

        return newRates
    }

    // TODO Fix rounding error when fixing from the same conversion to itself
    private fun convert(
        value: Double,
        inputCurrency: String,
        outputCurrency: String,
        ratesMap: Map<String, Double>
    ): Double {
        return value / ratesMap[inputCurrency]!! * ratesMap[outputCurrency]!!
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