package lbr2048.currencyconverter

data class Currency(
    val id: String,
    val name: String = "Unknown",
    val value: Double = -1.0
)