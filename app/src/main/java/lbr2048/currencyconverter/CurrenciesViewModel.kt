package lbr2048.currencyconverter

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

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

    private val exchangeRates = mapOf(
        "EUR" to 1,
        "PLN" to 2,
        "BRL" to 3
    )

    init {
        _inputValue.value = 10.0

        _inputCurrency.value = "EUR"

        _currencies.value = listOf(
            Currency("EUR", "Euro", 1.0),
            Currency("PLN", "Zloty", 2.0),
            Currency("BRL", "Real", 3.0)
        )

        convert()
    }

    fun setInputValue(value: Double) {
        if (_inputValue.value != value) {
            _inputValue.value = value
        }
        convert()
    }

    fun setInputCurrency(currency: String) {
        if (_inputCurrency.value != currency) {
            _inputCurrency.value = currency
        }
        convert()
    }

    fun convert() {
        convert(_inputValue.value!!, _inputCurrency.value!!)
    }

    // TODO improve code, it is confusing
    private fun convert(value: Double, inputCurrency: String) {
        Log.i("TAG", "Convert $value from $inputCurrency")

        val newCurrencies: MutableList<Currency> = mutableListOf()
        _currencies.value?.map {
            Log.i("TAG", convert(value, inputCurrency, it.id).toString())
            newCurrencies.add(Currency(it.id, it.name, convert(value, inputCurrency, it.id)))
        }

        _currencies.value = newCurrencies
    }

    private fun convert(value: Double, inputCurrency: String, outputCurrency: String): Double {
        return value / exchangeRates[inputCurrency]!! * exchangeRates[outputCurrency]!!
    }
}