package lbr2048.currencyconverter.data.network

data class RatesResponse(
    val baseCurrency: String,
    val rates: Rates
)