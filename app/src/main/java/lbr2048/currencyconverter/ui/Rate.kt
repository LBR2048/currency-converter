package lbr2048.currencyconverter.ui

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Rate(
    @PrimaryKey val currencyCode: String,
    val value: Double? = null
) {
    fun getFlagUrl() = "https://www.countryflags.io/${getRegionCode()}/flat/64.png"

    private fun getRegionCode() = currencyCode.substring(0, 2)
}

fun List<Rate>.asMap(): Map<String, Double?> {
    val result = mutableMapOf<String, Double?>()
    map {
        result[it.currencyCode] = it.value
    }
    return result
}