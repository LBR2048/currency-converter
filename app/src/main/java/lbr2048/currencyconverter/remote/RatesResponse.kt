package lbr2048.currencyconverter.remote

data class RatesResponse(
    val baseCurrency: String,
    val rates: Rates
)