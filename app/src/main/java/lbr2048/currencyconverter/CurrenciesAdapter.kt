package lbr2048.currencyconverter

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import kotlinx.android.synthetic.main.fragment_currency_item.view.*

class CurrenciesAdapter(viewModel: CurrenciesViewModel) : RecyclerView.Adapter<CurrenciesAdapter.ViewHolder>() {

    private var values: List<Currency> = emptyList()

    private val onClickListener: View.OnClickListener

    init {
        onClickListener = View.OnClickListener { v ->
            val item = v.tag as Currency
            Log.i("CLICK", "$item clicked")
            viewModel.setInputValue(item.value)
            viewModel.setInputCurrency(item.id)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_currency_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.id
        holder.contentView.text = item.name
        holder.valueView.text = item.value.toString()

        with(holder.view) {
            tag = item
            setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount(): Int = values.size

    fun replace(currencies: List<Currency>) {
        values = currencies
        notifyDataSetChanged()
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val idView: TextView = view.item_number
        val contentView: TextView = view.content
        val valueView: TextView = view.value

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }
}
