package lbr2048.currencyconverter

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Currency(
    @PrimaryKey val id: String,
    val name: String = "Unknown",
    val value: Double = -1.0
)

fun List<Currency>.asMap(): Map<String, Double> {
    val result = mutableMapOf<String, Double>()
    map {
        result[it.id] = it.value
    }
    return result
}