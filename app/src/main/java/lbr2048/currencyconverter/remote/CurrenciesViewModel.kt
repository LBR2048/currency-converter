package lbr2048.currencyconverter.remote

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import lbr2048.currencyconverter.CurrenciesWeb
import lbr2048.currencyconverter.Currency
import java.lang.Exception

class CurrenciesViewModel : ViewModel() {
    
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

        getExchangeRates()
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

    private fun convert(value: Double, inputCurrency: String, outputCurrency: String): Double {
        return value / exchangeRates[inputCurrency]!! * exchangeRates[outputCurrency]!!
    }

    private fun getExchangeRates() {
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
}