package com.zexceed.skripsiehapp.ui.view.fragment.peminjaman

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.zexceed.skripsiehapp.R
import com.zexceed.skripsiehapp.data.model.User
import com.zexceed.skripsiehapp.databinding.FragmentPeminjamanBinding
import com.zexceed.skripsiehapp.ui.adapter.PeminjamanAdapter
import com.zexceed.skripsiehapp.ui.view.fragment.inventory.InventoryFragment
import com.zexceed.skripsiehapp.ui.viewmodel.AuthViewModel
import com.zexceed.skripsiehapp.ui.viewmodel.PeminjamanViewModel
import com.zexceed.skripsiehapp.util.UiState
import com.zexceed.skripsiehapp.util.hide
import com.zexceed.skripsiehapp.util.show
import com.zexceed.skripsiehapp.util.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PeminjamanFragment : Fragment() {

    val TAG: String = "PeminjamanFragment"
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
    private var _binding: FragmentPeminjamanBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PeminjamanViewModel by viewModels()
    var objUser: User? = null
    val adapter by lazy {
        PeminjamanAdapter(
            onItemClicked = { pos, item ->
                findNavController().navigate(
                    R.id.action_PeminjamanFragment_to_PeminjamanDetailFragment,
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
        _binding = FragmentPeminjamanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()
        binding.apply {
            fabBase.setOnClickListener {
                onAddButtonClicked()
            }
            fabSearch.setOnClickListener {
                findNavController().navigate(R.id.action_PeminjamanFragment_to_PeminjamanSearchFragment)
            }
            fabProfile.setOnClickListener {
                findNavController().navigate(
                    R.id.action_PeminjamanFragment_to_PeminjamanprofileFragment,
                    Bundle().apply {
                        putParcelable("user", objUser)
                    })
            }
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = LinearLayoutManager(activity)
            recyclerView.adapter = adapter
            fabAdd.setOnClickListener {
                findNavController().navigate(
                    R.id.action_PeminjamanFragment_to_PeminjamanAddFragment
                )
            }
        }
//        authViewModel.getSession {
            viewModel.getPeminjaman()
            Log.e(TAG, "getSession: ")
//        }
    }

    private fun observer() {
        binding.apply {

            viewModel.peminjaman.observe(viewLifecycleOwner) { state ->
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
                        adapter.updatePeminjaman(state.data.toMutableList())
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
