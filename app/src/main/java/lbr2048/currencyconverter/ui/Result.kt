package lbr2048.currencyconverter.ui

data class Result<out T>(val data: T, val shouldScrollToTop: Boolean)