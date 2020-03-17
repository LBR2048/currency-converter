package lbr2048.currencyconverter

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.math.PI

class ConversionUtilsTest {

    private lateinit var ratesMap: Map<String, Double>

    @Before
    fun init() {
        ratesMap = mapOf("CURRENCY1" to 1.234, "CURRENCY2" to 2.345)
    }

    @Test
    fun convert_toSameCurrency_returnsSameValue() {
        val value = PI
        val inputCurrency = "CURRENCY1"
        val result = convert(value, inputCurrency, inputCurrency, ratesMap)

        assertEquals(result, value)
    }

    @Test
    fun convert_toAnotherCurrency_returnsCorrectValue() {
        val value = 1.234
        val result = convert(value, "CURRENCY1", "CURRENCY2", ratesMap)

        assertEquals(result, 2.345)
    }

    @Test
    fun convert_toNonExistentCurrency_returnsError() {
        val value = 3.14
        val result = convert(value, "CURRENCY1", "NON_EXISTENT", ratesMap)

        assertEquals(result, 2 * value)
    }

    @Test
    fun convert_nullValue_returnsNull() {
        val value = null
        val result = convert(value, "CURRENCY1", "CURRENCY2", ratesMap)

        assertEquals(result, null)
    }
}