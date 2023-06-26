package com.zexceed.skripsiehapp.ui.view.fragment.inventory

import InventoryAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.zexceed.skripsiehapp.R
import com.zexceed.skripsiehapp.data.model.User
import com.zexceed.skripsiehapp.databinding.FragmentInventoryBinding
import com.zexceed.skripsiehapp.ui.viewmodel.AuthViewModel
import com.zexceed.skripsiehapp.ui.viewmodel.InventoryViewModel
import com.zexceed.skripsiehapp.util.UiState
import com.zexceed.skripsiehapp.util.hide
import com.zexceed.skripsiehapp.util.show
import com.zexceed.skripsiehapp.util.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InventoryFragment : Fragment() {

    val TAG: String = "InventoryFragment"
    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.rotate_open_anim
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.rotate_close_anim
        )
    }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.from_bottom_anim
        )
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.to_bottom_anim
        )
    }
    private var clicked = true
    private var _binding: FragmentInventoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InventoryViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    var objUser: User? = null
    val adapter by lazy {
        InventoryAdapter { pos, item ->
            findNavController().navigate(
                R.id.action_InventoryFragment_to_InventoryDetailFragment,
                Bundle().apply {
                    putParcelable("inventory", item)
                })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.e(TAG, "onCreateView: ")
        _binding = FragmentInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")
        observer()
        binding.apply {
            fabBase.setOnClickListener {
                onAddButtonClicked()
            }
            fabSearch.setOnClickListener {
                findNavController().navigate(R.id.action_InventoryFragment_to_InventorySearchFragment)
            }
            fabProfile.setOnClickListener {
                findNavController().navigate(
                    R.id.action_InventoryFragment_to_profileFragment,
                    Bundle().apply {
                        putParcelable("user", objUser)
                    })
            }
            Log.d(TAG, "error bang observer utama")
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = LinearLayoutManager(activity)
            recyclerView.adapter = adapter
            fabAdd.setOnClickListener {
                findNavController().navigate(
                    R.id.action_InventoryFragment_to_InventoryAddFragment
                )
            }
        }
        authViewModel.getSession {
            viewModel.getInventory(it)
            Log.e(TAG, "getSession: ")
        }
    }

    private fun observer() {
        binding.apply {

            viewModel.inventory.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is UiState.Loading -> {
                        Log.e(TAG, "Loading")
                        progressBar.show()
                        recyclerView.hide()
                        fabBase.hide()
                        fabAdd.hide()
                        fabSearch.hide()
                        fabProfile.hide()
                    }

                    is UiState.Failure -> {
                        Log.e(TAG, state.error.toString())
                        progressBar.visibility = View.GONE
                        fabBase.hide()
                        fabAdd.hide()
                        fabSearch.hide()
                        fabProfile.hide()
                        recyclerView.show()
//                    binding.recyclerView.hide()
                        toast(state.error)
                        Log.e(TAG, "error bang observer utama")
                    }

                    is UiState.Success -> {
                        state.data.forEach {
                            Log.e(TAG, it.toString())
                        }
                        progressBar.visibility = View.GONE
                        fabBase.show()
                        fabAdd.hide()
                        fabSearch.hide()
                        fabProfile.hide()
                        recyclerView.show()
                        adapter.updateInventory(state.data.toMutableList())
                        Log.d(TAG, "success bang observer utama")
                    }
                }

            }
        }
    }

    private fun onAddButtonClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        setClickable(clicked)
        clicked = !clicked

    }

    private fun setVisibility(clicked: Boolean) {
        binding.apply {
            if (clicked) {
                fabProfile.visibility = View.VISIBLE
                fabSearch.visibility = View.VISIBLE
                fabAdd.visibility = View.VISIBLE
            } else {
                fabProfile.visibility = View.INVISIBLE
                fabSearch.visibility = View.INVISIBLE
                fabAdd.visibility = View.INVISIBLE
            }


        }
    }

    private fun setAnimation(clicked: Boolean) {

        binding.apply {
            if (clicked) {
                fabProfile.startAnimation(toBottom)
                fabAdd.startAnimation(toBottom)
                fabSearch.startAnimation(toBottom)
                fabBase.startAnimation(rotateOpen)
            } else {
                fabProfile.startAnimation(fromBottom)
                fabAdd.startAnimation(fromBottom)
                fabSearch.startAnimation(fromBottom)
                fabBase.startAnimation(rotateClose)
            }

        }

    }

    private fun setClickable(clicked: Boolean) {

        binding.apply {
            if (clicked) {
                fabSearch.isClickable = true
                fabProfile.isClickable = true
                fabAdd.isClickable = true
            } else {
                fabSearch.isClickable = false
                fabProfile.isClickable = false
                fabAdd.isClickable = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
