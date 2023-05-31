package com.zexceed.skripsiehapp.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zexceed.skripsiehapp.R
import com.zexceed.skripsiehapp.databinding.FragmentInventoryBinding
import com.zexceed.skripsiehapp.databinding.FragmentSearchInventoryBinding

class SearchInventoryFragment : Fragment() {

    private var _binding: FragmentSearchInventoryBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}