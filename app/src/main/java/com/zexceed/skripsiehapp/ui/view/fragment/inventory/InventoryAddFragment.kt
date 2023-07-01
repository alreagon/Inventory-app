package com.zexceed.skripsiehapp.ui.view.fragment.inventory

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.zexceed.skripsiehapp.R
import com.zexceed.skripsiehapp.data.model.Inventory
import com.zexceed.skripsiehapp.databinding.FragmentInventoryAddBinding
import com.zexceed.skripsiehapp.ui.adapter.ImageListingAdapter
import com.zexceed.skripsiehapp.ui.viewmodel.AuthViewModel
import com.zexceed.skripsiehapp.ui.viewmodel.InventoryViewModel
import com.zexceed.skripsiehapp.util.UiState
import com.zexceed.skripsiehapp.util.hide
import com.zexceed.skripsiehapp.util.show
import com.zexceed.skripsiehapp.util.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InventoryAddFragment : Fragment(R.layout.fragment_inventory_add) {

    val TAG: String = "InventoryAddFragment"
    private var _binding: FragmentInventoryAddBinding? = null
    private val binding get() = _binding!!
    private val inventoryViewModel: InventoryViewModel by viewModels()
    private var objInventory: Inventory? = null
    var uriee: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInventoryAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUI()
        observer()
        binding.icBack.setOnClickListener {
            findNavController().navigate(R.id.action_inventoryAddFragment_to_InventoryFragment)
        }

    }

    private fun observer() {
        binding.apply {
            inventoryViewModel.addInventory.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is UiState.Loading -> {
                        progressBar.show()
                        btnSave.text = ""
                    }

                    is UiState.Failure -> {
                        progressBar.hide()
                        btnSave.text = "ERROR"
                        toast(state.error)
                    }

                    is UiState.Success -> {
                        progressBar.hide()
                        btnSave.text = "Saved!"
                        toast(state.data.second)
                        objInventory = state.data.first
                        Handler(Looper.getMainLooper()).postDelayed({
                            findNavController().navigateUp()
                        }, 1500)
                    }
                }
            }
        }
    }

    private fun updateUI() {
        binding.apply {
            objInventory = arguments?.getParcelable("inventory")
            objInventory?.let { data ->
                etNamaBarang.setText(data.namaBarang)
                etKodeBarang.setText(data.kodeBarang)
                etDeskripsiBarang.setText(data.deskripsiBarang)
                etCatatan.setText(data.catatan)
            }
            btnSave.setOnClickListener {
                if (validation()) {
                    onDonePressed()
                }
            }
        }
        binding.btnAddImage.setOnClickListener {
            binding.pbAddImage.show()
            binding.btnAddImage.setText("")
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
//                    getContext.launch(intent)
                }

        }

    }

    private fun onDonePressed() {
        binding.apply {
            if (uriee !== null) {
                inventoryViewModel.onUploadSingleFile(uriee!!) { state ->
                    when (state) {
                        is UiState.Loading -> {
                            progressBar.show()
                            btnSave.setText("")
                        }

                        is UiState.Failure -> {
                            progressBar.hide()
                            btnSave.setText("Error!")
                            toast(state.error)
                        }

                        is UiState.Success -> {
                            progressBar.hide()
                            btnSave.setText("Data Saved!")
                            getInventory { inventory ->
                                inventoryViewModel.addInventory(inventory)
                            }
                        }
                    }
                }
            } else {
                btnAddImage.setText("Please input image!")
            }
        }
    }

    private fun getInventory(callback: (Inventory) -> Unit) {
        binding.apply {
            inventoryViewModel.getImageUrl.observe(viewLifecycleOwner) { urlLink ->
                val inventory = Inventory(
                    id = objInventory?.id ?: "",
                    namaBarang = etNamaBarang.text?.toString() ?: "",
                    kodeBarang = etKodeBarang.text?.toString() ?: "",
                    status = etStatus.text?.toString() ?: "",
                    asalBarang = etAsalBarang.text?.toString() ?: "",
                    deskripsiBarang = etDeskripsiBarang.text?.toString() ?: "",
                    catatan = etCatatan.text?.toString() ?: "",
                    foto = urlLink
                )
                callback.invoke(inventory)
            }
        }
    }

    private fun validation(): Boolean {
        binding.apply {
            var isValid = true

            if (
                etNamaBarang.text.toString().isEmpty() ||
                etKodeBarang.text.toString().isEmpty() ||
                etStatus.text.toString().isEmpty() ||
                etAsalBarang.text.toString().isEmpty() ||
                etDeskripsiBarang.text.toString().isEmpty() ||
                etCatatan.text.toString().isEmpty() ||
                ivFoto.toString().isEmpty()
            ) {
                isValid = false
                toast("Enter message")
            }

            return isValid
        }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            if (resultCode == Activity.RESULT_OK) {
                val fileUri: Uri = data?.data!!
                uriee = fileUri
                binding.ivFoto.setImageURI(fileUri)
                binding.pbAddImage.hide()
                binding.btnAddImage.setText("Image ready!")
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                binding.pbAddImage.hide()
                binding.btnAddImage.setText("Error!")
                toast(ImagePicker.getError(data))
            } else {
                binding.pbAddImage.show()
                Log.e("TAG", "Task Cancelled")
            }
        }


//    private fun getDateInventory(): String {
//        binding.apply {
//            var date = ""
//            tvTanggalPengembalian.setOnClickListener {
//                //create new instance
//                val datePickerFragment = DatePickerFragment()
//                val supportFragmentManager = requireActivity().supportFragmentManager
//
//                //set fragment result listener
//                supportFragmentManager.setFragmentResultListener(
//                    "REQUEST_KEY", viewLifecycleOwner
//                ) { resultKey, bundle ->
//                    if (resultKey == "REQUEST_KEY") {
//                        date = bundle.getString("SELECTED_DATE").toString()
//                        tvTanggalPengembalian.text = date
//                    }
//                }
//                // show
//                datePickerFragment.show(supportFragmentManager, "DatePickerFragment")
//            }
//            return date
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}