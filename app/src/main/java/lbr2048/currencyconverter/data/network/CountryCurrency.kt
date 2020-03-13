package lbr2048.currencyconverter.data.network

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RegionCurrency(
    @PrimaryKey val regionCode: String,
    val currencyCode: String
)