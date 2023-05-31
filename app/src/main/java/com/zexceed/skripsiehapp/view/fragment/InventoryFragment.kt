package com.zexceed.skripsiehapp.view.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.zexceed.skripsiehapp.R
import com.zexceed.skripsiehapp.databinding.FragmentInventoryBinding
import com.zexceed.skripsiehapp.databinding.FragmentPeminjamanBinding
import com.zexceed.skripsiehapp.view.activity.InventoryDetailActivity
import com.zexceed.skripsiehapp.view.activity.LoginActivity
import com.zexceed.skripsiehapp.view.adapter.InventoryAdapter
import com.zexceed.skripsiehapp.viewmodel.InventoryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InventoryFragment : Fragment() {
    private var _binding: FragmentInventoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var inventoryViewModel: InventoryViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inventoryViewModel =
            ViewModelProvider(requireActivity())[InventoryViewModel::class.java]

        binding.apply {
            inventoryViewModel.savedInventoryId.observe(viewLifecycleOwner) { listSavedInventoryId ->
                inventoryViewModel.allInventory.observe(viewLifecycleOwner) { allInventory ->
                    rvAllInventory.apply {
                        adapter = InventoryAdapter(
                            allInventory,
                            listSavedInventoryId,
                            { inventory ->
                                startActivity(
                                    Intent(
                                        requireContext(),
                                        InventoryDetailActivity::class.java
                                    ).putExtra(InventoryDetailActivity.INVENTORY_ID, inventory.id)
                                )
                            },
                            { inventory ->
                                inventoryViewModel.currentUserLiveData.observe(viewLifecycleOwner) { user ->
                                    if (user != null) {
                                        if (listSavedInventoryId.contains(inventory.id)) {
                                            lifecycleScope.launch(Dispatchers.IO) {
                                                val message =
                                                    inventory.id?.let {
                                                        inventoryViewModel.removeInventoryFromSaved(
                                                            it
                                                        )
                                                    }
                                                lifecycleScope.launch(Dispatchers.Main) {
                                                    message!!.observe(viewLifecycleOwner) { mIt ->
                                                        when (mIt) {
                                                            "SUCCESS" -> {
                                                                Toast.makeText(
                                                                    requireContext(),
                                                                    "Success",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }

                                                            "LOADING" -> {
                                                                Toast.makeText(
                                                                    requireContext(),
                                                                    "Loading",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }

                                                            else -> {
                                                                Toast.makeText(
                                                                    requireContext(),
                                                                    mIt,
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            lifecycleScope.launch(Dispatchers.IO) {
                                                val message = inventory.id?.let {
                                                    inventoryViewModel.saveInventory(
                                                        it
                                                    )
                                                }
                                                lifecycleScope.launch(Dispatchers.Main) {
                                                    message!!.observe(viewLifecycleOwner) { mIt ->
                                                        when (mIt) {
                                                            "SUCCESS" -> {
                                                                Toast.makeText(
                                                                    requireContext(),
                                                                    "Success",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }

                                                            "LOADING" -> {
                                                                Toast.makeText(
                                                                    requireContext(),
                                                                    "Loading",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }

                                                            else -> {
                                                                Toast.makeText(
                                                                    requireContext(),
                                                                    mIt,
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        startActivity(
                                            Intent(
                                                requireContext(),
                                                LoginActivity::class.java
                                            )
                                        )
                                    }
                                }
                            })
                        setHasFixedSize(true)
                    }
                }
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}