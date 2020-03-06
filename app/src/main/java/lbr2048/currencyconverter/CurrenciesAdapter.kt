package lbr2048.currencyconverter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import kotlinx.android.synthetic.main.fragment_currency_item.view.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class CurrenciesAdapter(viewModel: CurrenciesViewModel) : RecyclerView.Adapter<CurrenciesAdapter.ViewHolder>() {

    private var values: List<Currency> = emptyList()

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Currency
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
//            mListener?.onListFragmentInteraction(item)
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
            setOnClickListener(mOnClickListener)
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
