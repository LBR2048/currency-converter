package lbr2048.currencyconverter.data.network

import com.squareup.moshi.Json
import lbr2048.currencyconverter.ui.Rate

data class RatesResponse(
    val baseCurrency: String,
    @Json(name = "rates") val ratesMap: Map<String, Double>
) {
    fun getRates(): List<Rate> {
        val rates = ratesMap.map { Rate(it.key, it.value) }.toMutableList()
        rates.add(0, Rate(baseCurrency, 1.0))
        return rates
    }
}