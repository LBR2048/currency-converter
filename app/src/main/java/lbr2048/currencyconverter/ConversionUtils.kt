package lbr2048.currencyconverter

import android.util.Log
import androidx.annotation.VisibleForTesting
import lbr2048.currencyconverter.ui.Rate
import lbr2048.currencyconverter.ui.asMap

fun convertAll(input: Rate, rates: List<Rate>, orderedRates: List<Rate>): List<Rate> {
    Log.d("CONVERT_TAG", "Convert $input")
    return orderedRates.map {
        Rate(
            it.currencyCode,
            convert(input.value, input.currencyCode, it.currencyCode, rates.asMap())
        )
    }
}

@VisibleForTesting
fun convert(
    value: Double?,
    inputCurrency: String,
    outputCurrency: String,
    ratesMap: Map<String, Double?>
): Double? {
    return when {
        value == null -> null
        inputCurrency == outputCurrency -> value
        else -> {
            val inputRate = ratesMap[inputCurrency]
            val outputRate = ratesMap[outputCurrency]
            if (inputRate == null || outputRate == null) return null
            value / inputRate * outputRate
        }
    }
}