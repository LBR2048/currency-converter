package lbr2048.currencyconverter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_currency.*

class CurrenciesFragment : Fragment() {

    private lateinit var viewModel: CurrenciesViewModel
    private lateinit var currenciesAdapter: CurrenciesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_currency, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(CurrenciesViewModel::class.java)
        currenciesAdapter = CurrenciesAdapter(viewModel)

        viewModel.inputValue.observe(viewLifecycleOwner, Observer {
            value.setText(it.toString())
        })

        viewModel.inputCurrency.observe(viewLifecycleOwner, Observer {
            currency.setText(it)
        })

        viewModel.currencies.observe(viewLifecycleOwner, Observer {
            currenciesAdapter.replace(it)
        })

        // Set the adapter
        with(list) {
            layoutManager = LinearLayoutManager(context)
            adapter = currenciesAdapter
        }

        convertButton.setOnClickListener {
            viewModel.setInputValue(value.text.toString().toDouble())
            viewModel.setInputCurrency(currency.text.toString())
            viewModel.convert()
        }
    }
}
