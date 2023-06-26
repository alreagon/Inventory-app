package com.zexceed.skripsiehapp.ui.view.fragment.peminjaman

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.zexceed.skripsiehapp.R
import com.zexceed.skripsiehapp.data.model.Peminjaman
import com.zexceed.skripsiehapp.databinding.FragmentPeminjamanDetailBinding
import com.zexceed.skripsiehapp.ui.adapter.ImageListingAdapter
import com.zexceed.skripsiehapp.ui.viewmodel.PeminjamanViewModel
import com.zexceed.skripsiehapp.util.UiState
import com.zexceed.skripsiehapp.util.hide
import com.zexceed.skripsiehapp.util.show
import com.zexceed.skripsiehapp.util.toast
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class PeminjamanDetailFragment : Fragment() {
    val TAG: String = "PeminjamanDetailFragment"
    private var _binding: FragmentPeminjamanDetailBinding? = null
    private val binding get() = _binding!!
    val peminjamanViewModel: PeminjamanViewModel by viewModels()
    var objPeminjaman: Peminjaman? = null
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
        _binding = FragmentPeminjamanDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI()
        observer()
        binding.icBack.setOnClickListener {
            findNavController().navigate(R.id.action_PeminjamanDetailFragment_to_homeFragment)
        }
    }

    private fun observer() {
        peminjamanViewModel.updatePeminjaman.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.pbBtnSaveEdit.show()
                    binding.btnSave.hide()
                    binding.btnEdit.hide()
                    binding.btnEmpty.show()
                }

                is UiState.Failure -> {
                    binding.progressBar.hide()
                    binding.btnEdit.show()
                    toast(state.error)
                }

                is UiState.Success -> {
//                    binding.progressBar.hide()
                    toast(state.data)
                    binding.pbBtnSaveEdit.hide()
                    binding.btnSave.hide()
                    binding.btnEdit.show()
                    binding.btnEmpty.hide()
                    isMakeEnableUI(false)
                    Handler(Looper.getMainLooper()).postDelayed({
                        findNavController().navigateUp()
                    }, 1500)
                }
            }
        }

        peminjamanViewModel.deletePeminjaman.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.btnHapus.hide()
                    binding.pbBtnDelete.show()
                    binding.btnHapusEmpty.show()
                }

                is UiState.Failure -> {
                    binding.progressBar.hide()
                    binding.btnHapus.show()
                    toast(state.error)
                }

                is UiState.Success -> {
                    binding.btnHapus.show()
                    binding.pbBtnDelete.hide()
                    binding.btnHapusEmpty.hide()
                    toast(state.data)
                    Handler(Looper.getMainLooper()).postDelayed({
                        findNavController().navigateUp()
                    }, 1500)
                }
            }
        }
    }

    private fun updateUI() {
        binding.apply {
            objPeminjaman = arguments?.getParcelable("peminjaman")
            objPeminjaman?.let { data ->
                tvNamaBarang.setText(data.namaBarang)
                tvKodeBarang.setText(data.kodeBarang)
                tvNamaPeminjam.setText(data.namaPeminjam)
                tvOrganisasiPeminjam.setText(data.organisasiPeminjam)
                tvDeskripsiBarang.setText(data.deskripsiBarang)
                tvKondisiAwal.setText(data.kondisiAwal)
                tvTanggalPeminjaman.setText(data.tanggalPeminjaman)
                tvTanggalPengembalian.setText(data.tanggalPengembalian)
                tvJumlah.setText(data.jumlah)
                tvJaminan.setText(data.jaminan)
                tvCatatan.setText(data.catatan)
                val foto = data.foto[0].replace("file://", "")
                if (isValidLocalUrl(foto)) {
                    val imgBitmap = BitmapFactory.decodeFile(foto)
                    if (imgBitmap != null) {
                        ivItem.setImageBitmap(imgBitmap)
                    } else {
                        // Gambar tidak valid, tampilkan gambar acak dari drawable
                        ivItem.setImageResource(getRandomDrawable())
                    }
                } else {
                    // URL tidak valid, tampilkan gambar acak dari drawable
                    ivItem.setImageResource(getRandomDrawable())
                }
                isMakeEnableUI(false)
                btnSave.hide()
                btnEdit.show()
            } ?: run {
                tvNamaBarang.setText("")
                tvKodeBarang.setText("")
                tvNamaPeminjam.setText("")
                tvOrganisasiPeminjam.setText("")
                tvDeskripsiBarang.setText("")
                tvKondisiAwal.setText("")
                tvTanggalPeminjaman.setText("")
                tvTanggalPengembalian.setText("")
                tvJumlah.setText("")
                tvJaminan.setText("")
                tvCatatan.setText("")
                isMakeEnableUI(true)
                btnSave.hide()
                btnEdit.hide()
            }
            buttonSetup()
        }
    }

    private fun isValidLocalUrl(url: String): Boolean {
        val file = File(url.replace("file://", ""))
        return file.exists() && file.isFile
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
            tvNamaPeminjam.setOnClickListener {
                isMakeEnableUI(true)
            }
            tvOrganisasiPeminjam.setOnClickListener {
                isMakeEnableUI(true)
            }
            tvDeskripsiBarang.setOnClickListener {
                isMakeEnableUI(true)
            }
            tvKondisiAwal.setOnClickListener {
                isMakeEnableUI(true)
            }
            tvTanggalPengembalian.setOnClickListener {
                isMakeEnableUI(true)
            }
            tvTanggalPeminjaman.setOnClickListener {
                isMakeEnableUI(true)
            }
            tvJumlah.setOnClickListener {
                isMakeEnableUI(true)
            }
            tvJaminan.setOnClickListener {
                isMakeEnableUI(true)
            }
            tvCatatan.setOnClickListener {
                isMakeEnableUI(true)
            }
            //=============

            btnHapus.setOnClickListener {
                objPeminjaman?.let { peminjamanViewModel.deletePeminjaman(it) }
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
            tvNamaPeminjam.doAfterTextChanged {
                btnSave.show()
                btnEdit.hide()
            }
            tvOrganisasiPeminjam.doAfterTextChanged {
                btnSave.show()
                btnEdit.hide()
            }
            tvDeskripsiBarang.doAfterTextChanged {
                btnSave.show()
                btnEdit.hide()
            }
            tvKondisiAwal.doAfterTextChanged {
                btnSave.show()
                btnEdit.hide()
            }
            tvTanggalPengembalian.doAfterTextChanged {
                btnSave.show()
                btnEdit.hide()
            }
            tvTanggalPeminjaman.doAfterTextChanged {
                btnSave.show()
                btnEdit.hide()
            }
            tvJumlah.doAfterTextChanged {
                btnSave.show()
                btnEdit.hide()
            }
            tvJaminan.doAfterTextChanged {
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
        var isValid = true

        if (
            binding.tvNamaBarang.text.toString().isEmpty() ||
            binding.tvKodeBarang.text.toString().isEmpty() ||
            binding.tvNamaPeminjam.text.toString().isEmpty() ||
            binding.tvOrganisasiPeminjam.text.toString().isEmpty() ||
            binding.tvDeskripsiBarang.text.toString().isEmpty() ||
            binding.tvKondisiAwal.text.toString().isEmpty() ||
            binding.tvTanggalPeminjaman.text.toString().isEmpty() ||
            binding.tvTanggalPengembalian.text.toString().isEmpty() ||
            binding.tvJumlah.text.toString().isEmpty() ||
            binding.tvJaminan.text.toString().isEmpty() ||
            binding.tvCatatan.text.toString().isEmpty() ||
            binding.ivItem.toString().isEmpty()
        ) {
            isValid = false
            toast("Enter message")
        }

        return isValid
    }

    private fun getPeminjaman(): Peminjaman {
        return Peminjaman(
            id = objPeminjaman?.id ?: "",
            namaBarang = binding.tvNamaBarang.text.toString(),
            kodeBarang = binding.tvKodeBarang.text.toString(),
            namaPeminjam = binding.tvNamaPeminjam.text.toString(),
            organisasiPeminjam = binding.tvOrganisasiPeminjam.text.toString(),
            deskripsiBarang = binding.tvDeskripsiBarang.text.toString(),
            kondisiAwal = binding.tvKondisiAwal.text.toString(),
            tanggalPeminjaman = binding.tvTanggalPeminjaman.text.toString(),
            tanggalPengembalian = binding.tvTanggalPengembalian.text.toString(),
            jumlah = binding.tvJumlah.text.toString(),
            jaminan = binding.tvJaminan.text.toString(),
            catatan = binding.tvCatatan.text.toString(),
            foto = getImageUrls()
        )
//            .apply { authViewModel.getSession { this.user_id = it?.id ?: "" } }
    }

    private fun onRemoveImage(pos: Int, item: Uri) {
        adapter.removeItem(pos)
    }

    private fun getImageUrls(): List<String> {
        if (imageUris.isNotEmpty()) {
            return imageUris.map { it.toString() }
        } else {
            return objPeminjaman?.foto ?: arrayListOf()
        }
    }

    private fun onDonePressed() {
        if (imageUris.isNotEmpty()) {
            peminjamanViewModel.onUploadSingleFile(imageUris.first()) { state ->
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
                        peminjamanViewModel.updatePeminjaman(getPeminjaman())
                    }
                }
            }
        } else {
            peminjamanViewModel.updatePeminjaman(getPeminjaman())
        }
    }

    private fun isMakeEnableUI(isDisable: Boolean = false) {
        binding.apply {

            binding.tvNamaBarang.isEnabled = isDisable
            binding.tvKodeBarang.isEnabled = isDisable
            binding.tvNamaPeminjam.isEnabled = isDisable
            binding.tvOrganisasiPeminjam.isEnabled = isDisable
            binding.tvDeskripsiBarang.isEnabled = isDisable
            binding.tvKondisiAwal.isEnabled = isDisable
            binding.tvTanggalPeminjaman.isEnabled = isDisable
            binding.tvTanggalPengembalian.isEnabled = isDisable
            binding.tvJumlah.isEnabled = isDisable
            binding.tvJaminan.isEnabled = isDisable
            binding.tvCatatan.isEnabled = isDisable
            binding.ivItem.isEnabled = isDisable

        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}