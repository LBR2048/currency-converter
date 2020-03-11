package lbr2048.currencyconverter.network

data class RatesResponse(
    val baseCurrency: String,
    val rates: Rates
)