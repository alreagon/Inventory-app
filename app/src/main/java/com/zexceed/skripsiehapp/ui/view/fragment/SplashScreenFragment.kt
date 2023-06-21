package com.zexceed.skripsiehapp.ui.view.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.zexceed.skripsiehapp.R
import com.zexceed.skripsiehapp.data.model.User

class SplashScreenFragment : Fragment() {
    var objUser: User? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Firebase.auth.currentUser?.uid != null) {
            Handler(Looper.getMainLooper()).postDelayed({
                findNavController().navigate(
                    R.id.action_splashScreenFragment_to_homeNavigation,
                    Bundle().apply {
                        putParcelable("user", objUser)
                    })
            }, 3000)
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
//                findNavController().navigate(
//                    R.id.action_splashScreenFragment_to_loginFragment,
//                    Bundle().apply {
//                        putParcelable("user", objUser)
//                    })
                findNavController().navigate(R.id.action_splashScreenFragment_to_loginFragment)
            }, 3000)
        }
    }
}