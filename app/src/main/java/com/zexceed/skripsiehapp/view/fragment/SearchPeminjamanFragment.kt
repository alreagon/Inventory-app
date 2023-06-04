package com.zexceed.skripsiehapp.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.zexceed.skripsiehapp.R
import com.zexceed.skripsiehapp.databinding.FragmentInventoryBinding
import com.zexceed.skripsiehapp.databinding.FragmentSearchPeminjamanBinding
import com.zexceed.skripsiehapp.viewmodel.PeminjamanViewModel

class SearchPeminjamanFragment : Fragment() {

    val TAG : String = "SearchPeminjamanFragment"

    private var _binding: FragmentSearchPeminjamanBinding? = null
    private lateinit var peminjamanViewModel: PeminjamanViewModel
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchPeminjamanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        peminjamanViewModel = ViewModelProvider(requireActivity())[PeminjamanViewModel::class.java]

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}