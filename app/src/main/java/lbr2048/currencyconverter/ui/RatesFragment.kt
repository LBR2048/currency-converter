package lbr2048.currencyconverter.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_currency.*
import lbr2048.currencyconverter.R
import lbr2048.currencyconverter.data.RatesRepository
import lbr2048.currencyconverter.data.local.getDatabase

class RatesFragment : Fragment() {

    private lateinit var viewModel: RatesViewModel
    private lateinit var ratesAdapter: RatesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_currency, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = RatesRepository(
            getDatabase(requireContext())
        )
        viewModel = ViewModelProvider(this, RatesViewModel.Factory(repository))
            .get(RatesViewModel::class.java)
        ratesAdapter = RatesAdapter(requireContext(), viewModel)

        viewModel.result.observe(viewLifecycleOwner, Observer {
            ratesAdapter.submitList(it) {
                // TODO do not scroll to top when rates are updated, only when an item is clicked.
                //  Perhaps wrap result and an enum that identifies the type of change made to the list
                list.scrollToPosition(0)
            }
        })

        // Set the adapter
        with(list) {
            layoutManager = LinearLayoutManager(context)
            adapter = ratesAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.startTimer()
    }

    override fun onPause() {
        super.onPause()
        viewModel.cancelTimer()
    }
}
