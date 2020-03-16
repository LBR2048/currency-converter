package lbr2048.currencyconverter.ui

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_currency_item.view.*
import lbr2048.currencyconverter.R
import java.util.*

class RatesAdapter(
    private val context: Context,
    private val viewModel: RatesViewModel
)
    : ListAdapter<Rate, RatesAdapter.ViewHolder>(
    RateDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_currency_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        Glide.with(context).load(item.getFlagUrl()).into(holder.flagView)
        holder.idView.text = item.currencyCode
        holder.contentView.text = Currency.getInstance(item.currencyCode).displayName

        if (item.value == null) {
            holder.valueView.setText("")
        } else {
            val digits = Currency.getInstance(item.currencyCode).defaultFractionDigits
            holder.valueView.setText("%.${digits}f".format(item.value))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val combinedChange = createCombinedPayload(payloads as List<Change<Rate>>)
            val oldData = combinedChange.oldData
            val newData = combinedChange.newData

            if (oldData.value != newData.value) {
                if (newData.value == null) {
                    holder.valueView.setText("")
                } else {
                    val digits = Currency.getInstance(newData.currencyCode).defaultFractionDigits
                    holder.valueView.setText("%.${digits}f".format(newData.value))
                }
            }
        }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val flagView: ImageView = view.flag
        val idView: TextView = view.item_number
        val contentView: TextView = view.content
        val valueView: EditText = view.value

        init {
            with(view) {
                setOnClickListener {
                    val item = getItem(adapterPosition)
                    Log.i("CLICK_TAG", "$item item clicked")
                    valueView.requestFocus()
                }
            }

            val textWatcher = object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    Log.i("TEXT_TAG", "Item value is $s")
                    val item = getItem(adapterPosition)
                    val input = if (s.isNullOrEmpty()) {
                        Rate(item.currencyCode, null)
                    } else {
                        Rate(item.currencyCode, s.toString().toDouble())
                    }
                    viewModel.setInput(input, adapterPosition)
                }
            }
            with(valueView) {
                setOnFocusChangeListener { _, isFocused ->
                    val item = getItem(adapterPosition)
                    if (isFocused) {
                        Log.i("TEXT_TAG", "$item gained focus")
                        viewModel.setInput(item, adapterPosition)
                        this.addTextChangedListener(textWatcher)
                    } else {
                        Log.i("TEXT_TAG", "$item lost focus")
                        this.removeTextChangedListener(textWatcher)
                    }
                }
            }
        }

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }
}

class RateDiffCallback : DiffUtil.ItemCallback<Rate>() {
    override fun areItemsTheSame(oldItem: Rate, newItem: Rate): Boolean {
        return oldItem.currencyCode == newItem.currencyCode
    }

    override fun areContentsTheSame(oldItem: Rate, newItem: Rate): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: Rate, newItem: Rate): Any? {
        return Change(oldItem, newItem)
    }
}