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
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zexceed.skripsiehapp.R
import com.zexceed.skripsiehapp.data.model.Inventory
import com.zexceed.skripsiehapp.databinding.FragmentInventoryDetailBinding
import com.zexceed.skripsiehapp.ui.viewmodel.InventoryViewModel
import com.zexceed.skripsiehapp.util.UiState
import com.zexceed.skripsiehapp.util.disable
import com.zexceed.skripsiehapp.util.enabled
import com.zexceed.skripsiehapp.util.hide
import com.zexceed.skripsiehapp.util.show
import com.zexceed.skripsiehapp.util.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InventoryDetailFragment : Fragment() {
    val TAG: String = "InventoryDetailFragment"
    private var _binding: FragmentInventoryDetailBinding? = null
    private val binding get() = _binding!!
    val inventoryViewModel: InventoryViewModel by viewModels()
    var objInventory: Inventory? = null
    var imageUri: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInventoryDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI()
        observer()
        binding.icBack.setOnClickListener {
            findNavController().navigate(R.id.action_inventoryDetailFragment_to_homeFragment)
        }
    }

    private fun observer() {
        binding.apply {
            inventoryViewModel.updateInventory.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is UiState.Loading -> {
                        pbBtnSaveEdit.show()
                        btnSave.hide()
                        btnEdit.hide()
                        btnAddImage.hide()
                        llPoto.hide()
                        btnEmpty.show()
                        btnEmpty.setText("")
                    }

                    is UiState.Failure -> {
                        btnEdit.show()
                        btnAddImage.hide()
                        llPoto.hide()
                        toast(state.error)
                    }

                    is UiState.Success -> {
                        toast(state.data)
                        pbBtnSaveEdit.hide()
                        btnSave.hide()
                        btnEdit.hide()
                        btnAddImage.show()
                        llPoto.hide()
                        btnEmpty.show()
                        btnEmpty.setText("Saved!")
                        isMakeEnableUI(false)
                        Handler(Looper.getMainLooper()).postDelayed({
                            findNavController().navigateUp()
                        }, 1500)
                    }
                }
            }

            inventoryViewModel.deleteInventory.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is UiState.Loading -> {
                        btnHapus.hide()
                        pbBtnDelete.show()
                        btnHapusEmpty.show()
                    }

                    is UiState.Failure -> {
                        btnHapus.show()
                        toast(state.error)
                    }

                    is UiState.Success -> {
                        btnHapus.show()
                        pbBtnDelete.hide()
                        btnHapusEmpty.hide()
                        toast(state.data)
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
                tvNamaBarang.setText(data.namaBarang)
                tvKodeBarang.setText(data.kodeBarang)
                tvStatus.setText(data.status)
                tvAsalBarang.setText(data.asalBarang)
                tvDeskripsiBarang.setText(data.deskripsiBarang)
                tvCatatan.setText(data.catatan)
                if (data.foto.isNullOrEmpty()) {
                    ivItem.setImageResource(getRandomDrawable())
                } else {
                    Glide.with(ivItem.context).load(data.foto).into(ivItem)
                }
                isMakeEnableUI(false)
                btnSave.hide()
                btnEdit.show()
                btnAddImage.hide()
                llPoto.hide()
            } ?: run {
                tvNamaBarang.setText("")
                tvKodeBarang.setText("")
                tvStatus.setText("")
                tvAsalBarang.setText("")
                tvDeskripsiBarang.setText("")
                tvCatatan.setText("")
                isMakeEnableUI(true)
                btnSave.hide()
                btnEdit.hide()
                btnAddImage.hide()
                llPoto.hide()
            }
            buttonSetup()
        }
    }

    private fun getRandomDrawable(): Int {
        val drawableList = listOf(
            R.drawable.sapu,
            R.drawable.gambar_atk,
            R.drawable.gambar_basket,
            R.drawable.gambar_kursi,
            R.drawable.gambar_printer,
            R.drawable.gambar_proyektor,
            R.drawable.gambar_speaker
        )
        return drawableList.random()
    }

    private fun buttonSetup() {

        binding.apply {

            icBack.setOnClickListener {
                findNavController().navigateUp()
            }
            tvNamaBarang.setOnClickListener {
                isMakeEnableUI(true)
            }
            tvKodeBarang.setOnClickListener {
                isMakeEnableUI(true)
            }
            tvStatus.setOnClickListener {
                isMakeEnableUI(true)
            }
            tvAsalBarang.setOnClickListener {
                isMakeEnableUI(true)
            }
            tvDeskripsiBarang.setOnClickListener {
                isMakeEnableUI(true)
            }
            tvCatatan.setOnClickListener {
                isMakeEnableUI(true)
            }
            //=============

            btnHapus.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(resources.getString(R.string.title_hapus))
                    .setMessage(resources.getString(R.string.content_hapus))
                    .setNegativeButton(resources.getString(R.string.btn_cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(resources.getString(R.string.btn_logout)) { _, _ ->
                        objInventory?.let { inventoryViewModel.deleteInventory(it) }
                    }
                    .show()
            }
            btnEdit.setOnClickListener {
                isMakeEnableUI(true)
                btnSave.show()
                btnEdit.hide()
                btnAddImage.show()
                llPoto.show()
                tvNamaBarang.requestFocus()
            }
            btnSave.setOnClickListener {
                if (validation()) {
                    onDonePressed()
                }
            }
            binding.btnAddImage.setOnClickListener {
                binding.pbAddImage.show()

                btnEmpty.enabled()
                btnEmpty.show()
                btnEdit.disable()
                btnSave.disable()
                btnHapusEmpty.disable()
                btnHapus.disable()
                btnEmpty.setText("Image!")


                binding.btnAddImage.setText("")
                ImagePicker.with(this@InventoryDetailFragment)
                    .crop()
                    .compress(1024)
                    .createIntent { intent ->
                        startForProfileImageResult.launch(intent)
                    }

            }

            // =========

            tvNamaBarang.doAfterTextChanged {
                btnSave.show()
                btnEdit.hide()
                btnAddImage.show()
                llPoto.show()
            }
            tvKodeBarang.doAfterTextChanged {
                btnSave.show()
                btnEdit.hide()
                btnAddImage.show()
                llPoto.show()
            }
            tvStatus.doAfterTextChanged {
                btnSave.show()
                btnEdit.hide()
                btnAddImage.show()
                llPoto.show()
            }
            tvAsalBarang.doAfterTextChanged {
                btnSave.show()
                btnEdit.hide()
                btnAddImage.show()
                llPoto.show()
            }
            tvDeskripsiBarang.doAfterTextChanged {
                btnSave.show()
                btnEdit.hide()
                btnAddImage.show()
                llPoto.show()
            }
            tvCatatan.doAfterTextChanged {
                btnSave.show()
                btnEdit.hide()
                btnAddImage.show()
                llPoto.show()
            }
        }
    }

    private fun validation(): Boolean {
        binding.apply {
            var isValid = true
            if (
                tvNamaBarang.text.toString().isEmpty() ||
                tvKodeBarang.text.toString().isEmpty() ||
                tvStatus.text.toString().isEmpty() ||
                tvAsalBarang.text.toString().isEmpty() ||
                tvDeskripsiBarang.text.toString().isEmpty() ||
                tvCatatan.text.toString().isEmpty() ||
                ivItem.toString().isEmpty()
            ) {
                isValid = false
                toast("Enter message")
            }

            return isValid
        }
    }

    private fun getInventory(callback: (Inventory) -> Unit) {
        binding.apply {
            inventoryViewModel.getImageUrl.observe(viewLifecycleOwner) { urlLink ->
                val inventory = Inventory(
                    id = objInventory?.id ?: "",
                    namaBarang = tvNamaBarang.text?.toString() ?: "",
                    kodeBarang = tvKodeBarang.text?.toString() ?: "",
                    status = tvStatus.text?.toString() ?: "",
                    asalBarang = tvAsalBarang.text?.toString() ?: "",
                    deskripsiBarang = tvDeskripsiBarang.text?.toString() ?: "",
                    catatan = tvCatatan.text?.toString() ?: "",
                    foto = urlLink
                )
                callback.invoke(inventory)
            }
        }
    }

    private fun onDonePressed() {
        binding.apply {
            if (imageUri !== null) {
                inventoryViewModel.onUploadSingleFile(imageUri!!) { state ->
                    when (state) {
                        is UiState.Loading -> {
                            btnSave.hide()
                            btnEmpty.show()
                            pbBtnSaveEdit.show()
                            btnEmpty.setText("")
                        }

                        is UiState.Failure -> {
                            pbBtnSaveEdit.hide()
                            btnSave.hide()
                            btnEmpty.show()
                            btnEmpty.setText("Error!")
                            toast(state.error)
                        }

                        is UiState.Success -> {
                            btnSave.show()
                            btnSave.setText("Saved!")
                            getInventory { inventory ->
                                inventoryViewModel.updateInventory(inventory)
                            }
                        }
                    }
                }
            } else {
                btnAddImage.setText("Please input image!")
                pbAddImage.hide()
                btnSave.hide()
                btnEmpty.show()
                btnAddImage.show()
                btnEmpty.enabled()
                btnEdit.enabled()
                btnSave.enabled()
                btnHapusEmpty.disable()
                btnAddImage.enabled()
                btnHapus.enabled()
                btnEmpty.setText("Image!")
            }
        }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            if (resultCode == Activity.RESULT_OK) {
                val fileUri: Uri = data?.data!!
                imageUri = fileUri
                binding.ivFoto.setImageURI(fileUri)
                binding.pbAddImage.hide()
                binding.btnEmpty.enabled()
                binding.btnEdit.enabled()
                binding.btnSave.enabled()
                binding.btnHapusEmpty.enabled()
                binding.btnAddImage.enabled()
                binding.btnHapus.enabled()
                binding.btnEmpty.hide()
                binding.btnSave.show()
                binding.btnAddImage.setText("Image ready!")
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                binding.pbAddImage.hide()
                binding.btnAddImage.setText("Error!")
                binding.btnEmpty.show()
                binding.btnEmpty.enabled()
                binding.btnEdit.enabled()
                binding.btnSave.enabled()
                binding.btnHapusEmpty.enabled()
                binding.btnAddImage.enabled()
                binding.btnHapus.enabled()
                toast(ImagePicker.getError(data))
            } else {
                binding.btnEmpty.enabled()
                binding.btnEdit.disable()
                binding.btnSave.hide()
                binding.btnHapusEmpty.disable()
                binding.btnEmpty.setText("Image!")
                binding.btnHapus.disable()
                binding.btnEmpty.show()
                Log.e("TAG", "Task Cancelled")
            }
        }

    private fun isMakeEnableUI(isDisable: Boolean = false) {
        binding.apply {

            tvNamaBarang.isEnabled = isDisable
            tvKodeBarang.isEnabled = isDisable
            tvStatus.isEnabled = isDisable
            tvAsalBarang.isEnabled = isDisable
            tvDeskripsiBarang.isEnabled = isDisable
            tvCatatan.isEnabled = isDisable
            ivItem.isEnabled = isDisable
            btnAddImage.hide()
            llPoto.hide()

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}