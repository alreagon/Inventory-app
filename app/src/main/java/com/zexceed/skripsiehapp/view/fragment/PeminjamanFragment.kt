package com.zexceed.skripsiehapp.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zexceed.skripsiehapp.R
import com.zexceed.skripsiehapp.databinding.FragmentPeminjamanBinding

class PeminjamanFragment : Fragment() {
    private var _binding: FragmentPeminjamanBinding? = null
    private val binding get() = _binding!!
    private var eachFabVisible: Boolean? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPeminjamanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            val baseFab = baseFab
            val callFab = callFab
            val videoFab = videoCallFab
            val textOne = textOne
            val textTwo = textTwo
            videoFab.visibility = View.GONE
            callFab.visibility = View.GONE
            textOne.visibility = View.GONE
            textTwo.visibility = View.GONE

            eachFabVisible = false

            baseFab.setOnClickListener{
                if (!eachFabVisible!!) {
                    eachFabVisible = true
                    videoFab.show()
                    callFab.show()
                    textOne.visibility = View.VISIBLE
                    textTwo.visibility = View.VISIBLE
                } else {
                    videoFab.hide()
                    callFab.hide()
                    textOne.visibility = View.GONE
                    textTwo.visibility = View.GONE
                    eachFabVisible = false
                }
            }

        }
    }
}