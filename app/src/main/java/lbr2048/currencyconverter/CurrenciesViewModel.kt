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

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {

        setInputValueAndCurrency(1.0, "EUR")

        result.addSource(inputValue) {
            result.value =  combineLatestData(inputValue, inputCurrency, rates)
        }
        result.addSource(inputCurrency) {
            result.value =  combineLatestData(inputValue, inputCurrency, rates)
        }
        result.addSource(rates) {
            result.value =  combineLatestData(inputValue, inputCurrency, rates)
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
        ratesResult: LiveData<List<Currency>>
    ): List<Currency> {
        val inputValue = inputValueResult.value
        val inputCurrency = inputCurrencyResult.value
        val rates = ratesResult.value

        if (inputValue == null || inputCurrency == null || rates == null) {
            // TODO show error
            return emptyList()
        }

        return convertAll(inputValue, inputCurrency, rates.asMap(), rates)
    }

    // TODO improve code, it is confusing
    private fun convertAll(
        value: Double,
        inputCurrency: String,
        ratesMap: Map<String, Double?>,
        rates: List<Currency>?
    ): MutableList<Currency> {
        Log.i("CONVERT_TAG", "Convert $value from $inputCurrency")

        val newCurrencies: MutableList<Currency> = mutableListOf()
        rates?.map {
            newCurrencies.add(Currency(it.id, it.name, convert(
                value,
                inputCurrency,
                it.id,
                ratesMap
            )))
        }

        return newCurrencies
    }

    // TODO Fix rounding error when fixing from the same conversion to itself
    private fun convert(
        value: Double,
        inputCurrency: String,
        outputCurrency: String,
        ratesMap: Map<String, Double?>
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