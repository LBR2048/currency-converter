package lbr2048.currencyconverter

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception

class CurrenciesViewModel(private val repository: CurrenciesRepository) : ViewModel() {

    private val _inputValue = MutableLiveData<Double>()
    val inputValue: LiveData<Double>
        get() = _inputValue

    private val _inputCurrency = MutableLiveData<String>()
    val inputCurrency: LiveData<String>
        get() = _inputCurrency
    
    private val _currencies = MutableLiveData<List<Currency>>()
    val currencies: LiveData<List<Currency>>
        get() = _currencies

    private lateinit var exchangeRates: Map<String, Double?>

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        _inputValue.value = 10.0

        _inputCurrency.value = "EUR"

        getExchangeRatesFromNetwork()
    }

    fun setInputValue(value: Double) {
        if (_inputValue.value != value) {
            _inputValue.value = value
            convert()
        }
    }

    fun setInputCurrency(currency: String) {
        if (_inputCurrency.value != currency) {
            _inputCurrency.value = currency
            convert()
        }
    }

    fun setInputValueAndCurrency(value: Double, currency: String) {
        var isValueChanged = false
        var isCurrencyChanged = false
        if (_inputValue.value != value) {
            _inputValue.value = value
            isValueChanged = true
        }
        if (_inputCurrency.value != currency) {
            _inputCurrency.value = currency
            isCurrencyChanged = true
        }
        if (isValueChanged or isCurrencyChanged) {
            convert()
        }
    }

    fun convert() {
        convert(_inputValue.value!!, _inputCurrency.value!!)
    }

    // TODO improve code, it is confusing
    private fun convert(value: Double, inputCurrency: String) {
        Log.i("CONVERT_TAG", "Convert $value from $inputCurrency")

        val newCurrencies: MutableList<Currency> = mutableListOf()
        _currencies.value?.map {
            Log.i("CONVERT_TAG", convert(value, inputCurrency, it.id).toString())
            newCurrencies.add(Currency(it.id, it.name, convert(value, inputCurrency, it.id)))
        }

        _currencies.value = newCurrencies
    }

    // TODO Fix rounding error when fixing from the same conversion to itself
    private fun convert(value: Double, inputCurrency: String, outputCurrency: String): Double {
        return value / exchangeRates[inputCurrency]!! * exchangeRates[outputCurrency]!!
    }

    private fun getExchangeRatesFromRepository() {
        coroutineScope.launch {
            try {
                repository.refreshRates()
            } catch (e: Exception) {

            }
        }
    }

    private fun getExchangeRatesFromNetwork() {
        coroutineScope.launch {
            val getCurrenciesDeferred = CurrenciesWeb.retrofitService.getCurrencies()
            try {
                val result = getCurrenciesDeferred.await()
                Log.i("REMOTE_TAG", result.toString())

                // TODO put these calls outside of this function
                _currencies.value = result.rates.getRates()
                exchangeRates = result.rates.getRatesMap()
                convert()
            } catch (e: Exception) {
                Log.e("REMOTE_TAG", e.toString())
                // TODO show error message
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    /**
     * Factory for constructing DevByteViewModel with parameter
     */
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