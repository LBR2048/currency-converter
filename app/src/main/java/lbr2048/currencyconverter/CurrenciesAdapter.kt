package lbr2048.currencyconverter

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

import kotlinx.android.synthetic.main.fragment_currency_item.view.*
import lbr2048.currencyconverter.remote.CurrenciesViewModel

class CurrenciesAdapter(viewModel: CurrenciesViewModel) : ListAdapter<Currency, CurrenciesAdapter.ViewHolder>(CurrencyDiffCallback()) {

    private val onClickListener: View.OnClickListener
    private val onConvertClickListener: View.OnClickListener
    private val textWatcher: TextWatcher

    init {
        onClickListener = View.OnClickListener { v ->
            val item = v.tag as Currency
            Log.i("CLICK", "$item clicked")
            viewModel.setInputValueAndCurrency(item.value, item.id)
        }

        onConvertClickListener = View.OnClickListener { v ->
            val item = v.tag as Currency
            Log.i("CONVERT_TAG", "Convert item ${item.id} ${item.value}")
            viewModel.setInputValueAndCurrency(item.value, item.id)
        }

        textWatcher = object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.i("TEXT_TAG", "Item value is $s")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_currency_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.idView.text = item.id
        holder.contentView.text = item.name
        holder.valueView.setText(item.value.toString())

        with(holder.view) {
            tag = item
            setOnClickListener(onClickListener)
        }

        with(holder.convertButton) {
            tag = item
            setOnClickListener(onConvertClickListener)
        }

        with(holder.valueView) {
            tag = item
            addTextChangedListener(textWatcher)
        }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val idView: TextView = view.item_number
        val contentView: TextView = view.content
        val valueView: EditText = view.value
        val convertButton: Button = view.itemConvertButton

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }
}

class CurrencyDiffCallback : DiffUtil.ItemCallback<Currency>() {
    override fun areItemsTheSame(oldItem: Currency, newItem: Currency): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Currency, newItem: Currency): Boolean {
        return oldItem == newItem
    }
}