package lbr2048.currencyconverter.ui

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Rate(
    @PrimaryKey val id: String,
    val value: Double = -1.0
)

fun List<Rate>.asMap(): Map<String, Double> {
    val result = mutableMapOf<String, Double>()
    map {
        result[it.id] = it.value
    }
    return result
}