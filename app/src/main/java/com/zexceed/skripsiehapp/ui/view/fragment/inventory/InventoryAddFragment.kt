package com.zexceed.skripsiehapp.ui.view.fragment.inventory

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.imagepicker.ImagePicker
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

@AndroidEntryPoint
class InventoryAddFragment : Fragment(R.layout.fragment_inventory_add) {

    val TAG: String = "InventoryAddFragment"
    private var _binding: FragmentInventoryAddBinding? = null
    private val binding get() = _binding!!
    private val inventoryViewModel: InventoryViewModel by viewModels()
    var isEdit = false
    private var objInventory: Inventory? = null
    val authViewModel: AuthViewModel by viewModels()

    val adapter by lazy {
        ImageListingAdapter(
            onCancelClicked = { pos, item -> onRemoveImage(pos, item) }
        )
    }

    var imageUris: MutableList<Uri> = arrayListOf()

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            if (resultCode == Activity.RESULT_OK) {
                val fileUri = data?.data!!
                imageUris.add(fileUri)
//                imageUris = fileUri
                adapter.updateList(imageUris)
                binding.ivFoto.setImageURI(fileUri)
//                binding.ivFoto.hide()
                binding.pbAddImage.hide()
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                binding.pbAddImage.hide()
                toast(ImagePicker.getError(data))
            } else {
                binding.pbAddImage.hide()
                Log.e("TAG", "Task Cancelled")
            }
        }

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
            findNavController().navigate(R.id.action_inventoryAddFragment_to_homeFragment)
        }

    }

    private fun observer() {

        inventoryViewModel.addInventory.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.show()
                    binding.btnSave.text = ""
                }

                is UiState.Failure -> {
                    binding.progressBar.hide()
                    binding.btnSave.text = "ERROR"
                    toast(state.error)
                }

                is UiState.Success -> {
                    binding.progressBar.hide()
                    binding.btnSave.text = "Saved!"
                    toast(state.data.second)
                    objInventory = state.data.first
                    Handler(Looper.getMainLooper()).postDelayed({
                        findNavController().navigateUp()
                    }, 1500)
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
            rvImages.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            rvImages.adapter = adapter
            rvImages.itemAnimator = null
            btnSave.setOnClickListener {
                if (validation()) {
                    onDonePressed()
                }
            }
        }
        binding.btnAddImage.setOnClickListener {
            binding.pbAddImage.show()
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }

        }

    }

    private fun onDonePressed() {
        binding.apply {
            if (imageUris.isNotEmpty()) {
                inventoryViewModel.onUploadSingleFile(imageUris.first()) { state ->
                    when (state) {
                        is UiState.Loading -> {
                            progressBar.show()
                        }

                        is UiState.Failure -> {
                            progressBar.hide()
                            toast(state.error)
                        }

                        is UiState.Success -> {
                            progressBar.hide()
                            inventoryViewModel.addInventory(getInventory())
                        }
                    }
                }
            } else {
                inventoryViewModel.addInventory(getInventory())
            }
        }
    }

    private fun getInventory(): Inventory {
        binding.apply {
            return Inventory(
                id = objInventory?.id ?: "",
                namaBarang = etNamaBarang.text.toString(),
                kodeBarang = etKodeBarang.text.toString(),
                status = etStatus.text.toString(),
                asalBarang = etAsalBarang.text.toString(),
                deskripsiBarang = etDeskripsiBarang.text.toString(),
                catatan = etCatatan.text.toString(),
                foto = getImageUrls()
            )
//            .apply { authViewModel.getSession { this.user_id = it?.id ?: "" } }
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

    private fun getImageUrls(): List<String> {
        if (imageUris.isNotEmpty()) {
            return imageUris.map { it.toString() }
        } else {
            return objInventory?.foto ?: arrayListOf()
        }
    }

    private fun onRemoveImage(pos: Int, item: Uri) {
        adapter.removeItem(pos)
        if (objInventory != null) {
            binding.rvImages.performClick()
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