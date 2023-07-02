package com.zexceed.skripsiehapp.ui.view.fragment.peminjaman

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.zexceed.skripsiehapp.R
import com.zexceed.skripsiehapp.databinding.FragmentPeminjamanSearchBinding
import com.zexceed.skripsiehapp.ui.adapter.PeminjamanAdapter
import com.zexceed.skripsiehapp.ui.viewmodel.PeminjamanViewModel
import com.zexceed.skripsiehapp.util.UiState
import com.zexceed.skripsiehapp.util.show
import com.zexceed.skripsiehapp.util.toast
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale


@AndroidEntryPoint
class PeminjamanSearchFragment : Fragment() {

    val TAG: String = "PeminjamanSearchFragment"
    private var _binding: FragmentPeminjamanSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PeminjamanViewModel by viewModels()
    val adapterSearch by lazy {
        PeminjamanAdapter(
            onItemClicked = { pos, item ->
                findNavController().navigate(
                    R.id.action_PeminjamanSearchFragment_to_PeminjamanDetailFragment,
                    Bundle().apply {
                        putParcelable("peminjaman", item)
                    })
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("TAG", "onCreateView: ")
        _binding = FragmentPeminjamanSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("TAG", "onViewCreated: ")
        percobaanKeempat()
        binding.icBack.setOnClickListener {
            findNavController().navigate(R.id.action_PeminjamanSearchFragment_to_homeFragment)
        }

    }

    private fun percobaanKeempat() {

        binding.apply {
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = LinearLayoutManager(activity)
            recyclerView.adapter = adapterSearch
            //=================
            etSearchPeminjaman.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(p0: String): Boolean {
                    val query = p0.trim()
                    observerPercobaanKeempat(query)
                    return true
                }

            })

        }

    }

    private fun observerPercobaanKeempat(query: String) {
        viewModel.searchPeminjaman(query.toLowerCase(Locale.getDefault()))
        viewModel.searchResult.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    Log.d("TAG", "Loading")
                    binding.progressBar.show()
                    binding.recyclerView.show()

                }

                is UiState.Failure -> {
                    Log.e("TAG", state.error.toString())
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerView.show()
                    toast(state.error)
                    Log.d("TAG", "error bang observer utama")
                }

                is UiState.Success -> {
                    state.data.forEach {
                        Log.e("TAG", it.toString())
                    }
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerView.show()
                    adapterSearch.updatePeminjaman(state.data.toMutableList())
                    Log.d("TAG", "success bang observer utama")
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

