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

        with(holder.view) {
            setOnClickListener {
                Log.i("CLICK_TAG", "$item item clicked")
                viewModel.setInput(item)
                viewModel.moveItemToTop(holder.adapterPosition)
            }
        }

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.i("TEXT_TAG", "Item value is $s")
                val input = if (s.isNullOrEmpty()) {
                    Rate(item.currencyCode, null)
                } else {
                    Rate(item.currencyCode, s.toString().toDouble())
                }
                viewModel.setInput(input)
            }
        }
        with(holder.valueView) {
            setOnFocusChangeListener { view, b ->
                if (b) {
                    Log.i("TEXT_TAG", "$item gained focus")
                    this.addTextChangedListener(textWatcher)
                } else {
                    Log.i("TEXT_TAG", "$item lost focus")
                    this.removeTextChangedListener(textWatcher)
                }
            }
        }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val flagView: ImageView = view.flag
        val idView: TextView = view.item_number
        val contentView: TextView = view.content
        val valueView: EditText = view.value

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
}