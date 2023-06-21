package com.zexceed.skripsiehapp.ui.view.fragment.inventory

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
import com.zexceed.skripsiehapp.databinding.FragmentInventorySearchBinding
import com.zexceed.skripsiehapp.ui.adapter.InventoryAdapter
import com.zexceed.skripsiehapp.ui.adapter.InventorySearchAdapter
import com.zexceed.skripsiehapp.ui.viewmodel.InventoryViewModel
import com.zexceed.skripsiehapp.util.UiState
import com.zexceed.skripsiehapp.util.show
import com.zexceed.skripsiehapp.util.toast
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class InventorySearchFragment : Fragment() {

    val TAG: String = "InventorySearchFragment"
    private var _binding: FragmentInventorySearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InventoryViewModel by viewModels()
    val adapterSearch by lazy {
        InventorySearchAdapter(
            onItemClicked = { pos, item ->
                findNavController().navigate(
                    R.id.action_inventorySearchFragment_to_inventoryDetailFragment,
                    Bundle().apply {
                        putParcelable("inventory", item)
                    })
            }
        )
    }
    val adapter by lazy {
        InventoryAdapter(
            onItemClicked = { pos, item ->
                findNavController().navigate(
                    R.id.action_inventorySearchFragment_to_inventoryDetailFragment,
                    Bundle().apply {
                        putParcelable("inventory", item)
                    })
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("TAG", "onCreateView: ")
        _binding = FragmentInventorySearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("TAG", "onViewCreated: ")
        percobaanKeempat()
        binding.icBack.setOnClickListener {
            findNavController().navigate(R.id.action_inventorySearchFragment_to_homeFragment)
        }

    }

    private fun percobaanKeempat() {

        binding.apply {
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = LinearLayoutManager(activity)
            recyclerView.adapter = adapterSearch
            //=================
            etSearchInventory.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(p0: String): Boolean {
                    observerPercobaanKeempat(p0)
                    return true
                }

            })

        }

    }

    private fun observerPercobaanKeempat(str: String) {
        viewModel.searchInventory(str)
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
                    adapterSearch.updateInventory(state.data.toMutableList())
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

