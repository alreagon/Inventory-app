package com.zexceed.skripsiehapp.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zexceed.skripsiehapp.R
import com.zexceed.skripsiehapp.databinding.FragmentPeminjamanBinding

class PeminjamanFragment : Fragment() {

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
    private var clicked = false

    private var _binding: FragmentPeminjamanBinding? = null
    private val binding get() = _binding!!
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

            fabBase.setOnClickListener {
                onAddButtonClicked()
            }
            fabSearch.setOnClickListener {

            }
            fabProfile.setOnClickListener {

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
            if (!clicked) {
                fabProfile.visibility = View.VISIBLE
                fabSearch.visibility = View.VISIBLE
            } else {
                fabProfile.visibility = View.INVISIBLE
                fabSearch.visibility = View.INVISIBLE
            }


        }
    }

    private fun setAnimation(clicked: Boolean) {

        binding.apply {
            if (!clicked) {
                fabProfile.startAnimation(fromBottom)
                fabSearch.startAnimation(fromBottom)
                fabBase.startAnimation(rotateOpen)
            } else {
                fabProfile.startAnimation(toBottom)
                fabSearch.startAnimation(toBottom)
                fabBase.startAnimation(rotateClose)
            }

        }

    }

    private fun setClickable(clicked: Boolean) {

        binding.apply {
            if (!clicked) {
                fabSearch.isClickable = true
                fabProfile.isClickable = true
            } else {
                fabSearch.isClickable = false
                fabProfile.isClickable = false
            }
        }
    }

}