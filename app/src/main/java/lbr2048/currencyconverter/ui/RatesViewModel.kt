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
import lbr2048.currencyconverter.data.RatesRepository.RefreshState
import java.util.*
import kotlin.concurrent.schedule

class RatesViewModel(private val repository: IRatesRepository) : ViewModel() {

    val result = MediatorLiveData<Result<List<Rate>>>()
    val refreshState: LiveData<RefreshState> = repository.refreshState

    private val rates: LiveData<List<Rate>> = repository.rates
    private val orderedRates = MutableLiveData<List<Rate>>()
    private val inputRate = MutableLiveData<Rate>()

    private var viewModelJob = Job()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private lateinit var timer: Timer

    init {

        inputRate.value = Rate("EUR", 1.0)

        result.addSource(inputRate) {
            result.value = Result(combineLatestData(inputRate, rates, orderedRates), false)
        }
        result.addSource(rates) {
            result.value = Result(combineLatestData(inputRate, rates, orderedRates), false)
        }
        result.addSource(orderedRates) {
            result.value = Result(combineLatestData(inputRate, rates, orderedRates), true)
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
        Log.d("POSITION_TAG", "Item $rate clicked")
        if (inputRate.value != rate) {
            inputRate.value = rate
        }
        if (position != 0) moveItemToTop(position)
    }

    private fun moveItemToTop(position: Int) {
        Log.d("POSITION_TAG", "Move item $position to top")
        orderedRates.value?.toMutableList()?.let {
            it.add(0, it.removeAt(position))
            orderedRates.value = it
        }
    }

    private fun combineLatestData(
        inputResult: LiveData<Rate>,
        ratesResult: LiveData<List<Rate>>,
        orderedRatesResult: LiveData<List<Rate>>
    ): List<Rate> {
        val input = inputResult.value
        val rates = ratesResult.value
        val orderedRates = orderedRatesResult.value

        if (input == null || rates.isNullOrEmpty()) {
            return emptyList()
        }

        if (orderedRates.isNullOrEmpty()) {
            this.orderedRates.value = rates
            return emptyList()
        }

        return convertAll(input, rates, orderedRates)
    }

    private fun getExchangeRatesFromRepository() {
        viewModelScope.launch {
            repository.refreshRates()
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        timer.cancel()
    }

    class Factory(private val repository: RatesRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RatesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return RatesViewModel(repository) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}