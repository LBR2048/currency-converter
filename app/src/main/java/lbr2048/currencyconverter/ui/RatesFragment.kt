package lbr2048.currencyconverter.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_currency.*
import lbr2048.currencyconverter.R
import lbr2048.currencyconverter.data.RatesRepository
import lbr2048.currencyconverter.data.RatesRepository.RefreshState.*
import lbr2048.currencyconverter.data.local.getDatabase

class RatesFragment : Fragment() {

    private lateinit var viewModel: RatesViewModel
    private lateinit var ratesAdapter: RatesAdapter
    private lateinit var errorSnackbar: Snackbar

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

        viewModel.result.observe(viewLifecycleOwner, Observer { result ->
            if (result.shouldScrollToTop) {
                ratesAdapter.submitList(result.data) {
                    list.scrollToPosition(0)
                }
            } else {
                ratesAdapter.submitList(result.data)
            }
        })

        errorSnackbar = Snackbar.make(requireActivity().findViewById(android.R.id.content), getString(R.string.snackbar_refresh_error), Snackbar.LENGTH_INDEFINITE)
        viewModel.refreshState.observe(viewLifecycleOwner, Observer { refreshState ->
            when (refreshState) {
                LOADING -> {}
                SUCCESS -> {
                    if (errorSnackbar.isShown) {
                        errorSnackbar.dismiss()
                    }
                }
                ERROR -> {
                    if (!errorSnackbar.isShown) {
                        errorSnackbar.show()
                    }
                }
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
