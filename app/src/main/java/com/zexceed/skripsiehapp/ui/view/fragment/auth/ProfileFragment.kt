package com.zexceed.skripsiehapp.ui.view.fragment.auth

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.zexceed.skripsiehapp.R
import com.zexceed.skripsiehapp.data.model.User
import com.zexceed.skripsiehapp.ui.viewmodel.AuthViewModel
import com.zexceed.skripsiehapp.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    val TAG: String = "ProfileFragment"
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var myDialog: Dialog
    var objUser: User? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            llEditProfile.setOnClickListener {
                findNavController().navigate(
                    R.id.action_profileFragment2_to_editProfileFragment,
                    Bundle().apply {
                        putParcelable("user", objUser)
                    })
            }

            llLogoutProfile.setOnClickListener {
//                showDialog()
                authViewModel.logout {
                    findNavController().navigate(R.id.action_profileFragment2_to_loginFragment)
                }
            }
            icBackProfile.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment2_to_homeFragment)
            }

        }
    }

    private fun showDialog() {
//        val inflater: LayoutInflater = this.layoutInflater
//        val customView: View = inflater.inflate(R.layout.dialog_logout, null)
//        val btnYes: Button = customView.findViewById(R.id.btnYes)
//        val btnNo: Button = customView.findViewById(R.id.btnNo)


        myDialog = Dialog(requireContext())
        myDialog.setContentView(R.layout.dialog_logout)
        val btnNo = myDialog.findViewById<Button>(R.id.btnNo)
        val btnYes = myDialog.findViewById<Button>(R.id.btnYes)

        btnYes.setOnClickListener {
            authViewModel.logout {
                findNavController().navigate(R.id.action_profileFragment2_to_loginFragment)
            }
        }
        btnNo.setOnClickListener {
            fun onClick(v: View) {
                myDialog.dismiss()
            }
            onClick(it)
        }
//        AlertDialog.Builder(requireContext(), R.style.MyDialogStyle).apply {
//            setCancelable(true)
//            setView(customView)
//            create()
//            show()
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
//class MyDialog private constructor(
//    private val context: Context,
//    private val confirm: (() -> Unit)? = null,
//) {
//    private lateinit var dialog: Dialog
//
//    data class Builder(
//        internal val context: Context,
//        var confirm: (() -> Unit)? = null,
//    ) {
//        fun setOnConfirm(confirm: () -> Unit) =
//            apply {
//                this.confirm = confirm
//            }
//
//        fun build() = MyDialog(
//            context = context,
//            confirm = confirm
//        )
//    }
//
//    init {
//        showMyDialog()
//    }
//
//    private fun showMyDialog() {
//        dialog = Dialog(context)
//        dialog.apply {
//            requestWindowFeature(Window.FEATURE_NO_TITLE)
//            setCancelable(true)
//            setContentView(R.layout.your_dialog)
//            findViewById<CardView>(R.id.your_item).setOnClickListener {
//                dismiss()
//            }
//            findViewById<CustomTextView>(R.id.text_yes).setOnClickListener {
//                confirm?.invoke()
//                dismiss()
//            }
//            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            val lp = WindowManager.LayoutParams()
//            lp.copyFrom(dialog.window!!.attributes)
//            lp.width = context.resources.getDimension(R.dimen.size_320).toInt()
//            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
//            show()
//            window!!.attributes = lp
//        }
//    }
//}