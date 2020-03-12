package lbr2048.currencyconverter

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.schedule

class CurrenciesViewModel(private val repository: CurrenciesRepository) : ViewModel() {

    private lateinit var timer: Timer
    private val _inputValue = MutableLiveData<Double>()

    val inputValue: LiveData<Double>
        get() = _inputValue
    private val _inputCurrency = MutableLiveData<String>()

    val inputCurrency: LiveData<String>
        get() = _inputCurrency

    private val rates: LiveData<List<Currency>> = repository.rates

    val result = MediatorLiveData<List<Currency>>()
    private val orderedCurrencies = MutableLiveData<List<Currency>>()

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {

        setInputValueAndCurrency(1.0, "EUR")

        orderedCurrencies.value = listOf(Currency(id = "AUD"), Currency(id = "EUR"), Currency(id = "BGN"), Currency(id = "BRL"), Currency(id = "CAD"), Currency(id = "CHF"), Currency(id = "CNY"), Currency(id = "CZK"), Currency(id = "DKK"), Currency(id = "GBP"), Currency(id = "HKD"), Currency(id = "HRK"), Currency(id = "HUF"), Currency(id = "IDR"), Currency(id = "ILS"), Currency(id = "INR"), Currency(id = "ISK"), Currency(id = "JPY"), Currency(id = "KRW"), Currency(id = "MXN"), Currency(id = "MYR"), Currency(id = "NOK"), Currency(id = "NZD"), Currency(id = "PHP"), Currency(id = "PLN"), Currency(id = "RON"), Currency(id = "RUB"), Currency(id = "SEK"), Currency(id = "SGD"), Currency(id = "THB"), Currency(id = "USD"), Currency(id = "ZAR"))

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
        ratesResult: LiveData<List<Currency>>,
        orderedCurrenciesResult: LiveData<List<Currency>>
    ): List<Currency> {
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
        rates:  List<Currency>,
        orderedCurrencies: List<Currency>
    ): MutableList<Currency> {
        Log.i("CONVERT_TAG", "Convert $value from $inputCurrency")

        val newCurrencies: MutableList<Currency> = mutableListOf()
        orderedCurrencies.map {
            newCurrencies.add(Currency(it.id, it.name, convert(
                value,
                inputCurrency,
                it.id,
                rates.asMap()
            )))
        }

        return newCurrencies
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

    class Factory(val repository: CurrenciesRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CurrenciesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CurrenciesViewModel(repository) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}