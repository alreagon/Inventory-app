package com.zexceed.skripsiehapp.ui.view.fragment.inventory

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.zexceed.skripsiehapp.R
import com.zexceed.skripsiehapp.data.model.Inventory
import com.zexceed.skripsiehapp.databinding.FragmentInventoryDetailBinding
import com.zexceed.skripsiehapp.ui.adapter.ImageListingAdapter
import com.zexceed.skripsiehapp.ui.viewmodel.InventoryViewModel
import com.zexceed.skripsiehapp.util.UiState
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
    var imageUris: MutableList<Uri> = arrayListOf()
    val adapter by lazy {
        ImageListingAdapter(
            onCancelClicked = { pos, item -> onRemoveImage(pos, item) }
        )
    }

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
                        btnEmpty.show()
                    }

                    is UiState.Failure -> {
                        progressBar.hide()
                        btnEdit.show()
                        toast(state.error)
                    }

                    is UiState.Success -> {
//                    progressBar.hide()
                        toast(state.data)
                        pbBtnSaveEdit.hide()
                        btnSave.hide()
                        btnEdit.show()
                        btnEmpty.hide()
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
                        progressBar.hide()
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
                val imgFileeee = data.foto[0].replace("file://", "")
                val imgBitmap = BitmapFactory.decodeFile(imgFileeee)
                ivItem.setImageBitmap(imgBitmap)
                isMakeEnableUI(false)
                btnSave.hide()
                btnEdit.show()
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
            }
            buttonSetup()
        }
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
                objInventory?.let { inventoryViewModel.deleteInventory(it) }
            }
            btnEdit.setOnClickListener {
                isMakeEnableUI(true)
                btnSave.show()
                btnEdit.hide()
                tvNamaBarang.requestFocus()
            }
            btnSave.setOnClickListener {
                if (validation()) {
                    onDonePressed()
                }
            }

            // =========

            tvNamaBarang.doAfterTextChanged {
                btnSave.show()
                btnEdit.hide()
            }
            tvKodeBarang.doAfterTextChanged {
                btnSave.show()
                btnEdit.hide()
            }
            tvStatus.doAfterTextChanged {
                btnSave.show()
                btnEdit.hide()
            }
            tvAsalBarang.doAfterTextChanged {
                btnSave.show()
                btnEdit.hide()
            }
            tvDeskripsiBarang.doAfterTextChanged {
                btnSave.show()
                btnEdit.hide()
            }
            tvCatatan.doAfterTextChanged {
                btnSave.show()
                btnEdit.hide()
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

    private fun getInventory(): Inventory {
        binding.apply {
            return Inventory(
                id = objInventory?.id ?: "",
                namaBarang = tvNamaBarang.text.toString(),
                kodeBarang = tvKodeBarang.text.toString(),
                status = tvStatus.text.toString(),
                asalBarang = tvAsalBarang.text.toString(),
                deskripsiBarang = tvDeskripsiBarang.text.toString(),
                catatan = tvCatatan.text.toString(),
                foto = getImageUrls()
            )
//            .apply { authViewModel.getSession { this.user_id = it?.id ?: "" } }
        }
    }

    private fun onRemoveImage(pos: Int, item: Uri) {
        adapter.removeItem(pos)
    }

    private fun getImageUrls(): List<String> {
        if (imageUris.isNotEmpty()) {
            return imageUris.map { it.toString() }
        } else {
            return objInventory?.foto ?: arrayListOf()
        }
    }

    private fun onDonePressed() {
        if (imageUris.isNotEmpty()) {
            inventoryViewModel.onUploadSingleFile(imageUris.first()) { state ->
                when (state) {
                    is UiState.Loading -> {
                        binding.pbBtnSaveEdit.show()
                    }

                    is UiState.Failure -> {
                        binding.pbBtnSaveEdit.hide()
                        toast(state.error)
                    }

                    is UiState.Success -> {
                        binding.pbBtnSaveEdit.hide()
                        inventoryViewModel.updateInventory(getInventory())
                    }
                }
            }
        } else {
            inventoryViewModel.updateInventory(getInventory())
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

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}