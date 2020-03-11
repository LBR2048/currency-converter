package lbr2048.currencyconverter

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import lbr2048.currencyconverter.network.RatesResponse
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://hiring.revolut.codes/api/android/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
//    .addConverterFactory(ScalarsConverterFactory.create())
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

interface CurrenciesWebService {
    @GET("latest?base=EUR")
    fun getCurrencies(): Deferred<RatesResponse>
//    fun getCurrencies(): Deferred<String>
}

object CurrenciesWeb {
    val retrofitService : CurrenciesWebService by lazy {
        retrofit.create(CurrenciesWebService::class.java)
    }
}