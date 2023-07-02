package com.zexceed.skripsiehapp.ui.view.fragment.peminjaman

import android.app.Activity
import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
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
import com.zexceed.skripsiehapp.data.model.Peminjaman
import com.zexceed.skripsiehapp.databinding.FragmentPeminjamanDetailBinding
import com.zexceed.skripsiehapp.ui.viewmodel.PeminjamanViewModel
import com.zexceed.skripsiehapp.util.UiState
import com.zexceed.skripsiehapp.util.disable
import com.zexceed.skripsiehapp.util.enabled
import com.zexceed.skripsiehapp.util.hide
import com.zexceed.skripsiehapp.util.show
import com.zexceed.skripsiehapp.util.toast
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class PeminjamanDetailFragment : Fragment() {
    val TAG: String = "PeminjamanDetailFragment"
    private var _binding: FragmentPeminjamanDetailBinding? = null
    private val binding get() = _binding!!
    val peminjamanViewModel: PeminjamanViewModel by viewModels()
    var objPeminjaman: Peminjaman? = null
    var imageUri: Uri? = null
    private var selectedDate: Date? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPeminjamanDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDatePickers()
        updateUI()
        observer()
        binding.icBack.setOnClickListener {
            findNavController().navigate(R.id.action_PeminjamanDetailFragment_to_homeFragment)
        }
    }

    private fun observer() {
        binding.apply {
            peminjamanViewModel.updatePeminjaman.observe(viewLifecycleOwner) { state ->
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

            peminjamanViewModel.deletePeminjaman.observe(viewLifecycleOwner) { state ->
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
//            tvTanggalPengembalian.setOnClickListener {
//                isMakeEnableUI(true)
//            }
//            tvTanggalPeminjaman.setOnClickListener {
//                isMakeEnableUI(true)
//            }
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
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(resources.getString(R.string.title_hapus))
                    .setMessage(resources.getString(R.string.content_hapus))
                    .setNegativeButton(resources.getString(R.string.btn_cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(resources.getString(R.string.btn_logout)) { _, _ ->
                        objPeminjaman?.let { peminjamanViewModel.deletePeminjaman(it) }
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
                ImagePicker.with(this@PeminjamanDetailFragment)
                    .crop()
                    .compress(1024)
                    .createIntent { intent ->
                        startForProfileImageResult.launch(intent)
                    }

            }

            // =========

            tvNamaBarang.doAfterTextChanged {
                btnSave.show()
                btnAddImage.show()
                btnEdit.hide()
            }
            tvKodeBarang.doAfterTextChanged {
                btnSave.show()
                btnAddImage.show()
                btnEdit.hide()
            }
            tvNamaPeminjam.doAfterTextChanged {
                btnSave.show()
                btnAddImage.show()
                btnEdit.hide()
            }
            tvOrganisasiPeminjam.doAfterTextChanged {
                btnSave.show()
                btnAddImage.show()
                btnEdit.hide()
            }
            tvDeskripsiBarang.doAfterTextChanged {
                btnSave.show()
                btnAddImage.show()
                btnEdit.hide()
            }
            tvKondisiAwal.doAfterTextChanged {
                btnSave.show()
                btnAddImage.show()
                btnEdit.hide()
            }
            tvTanggalPengembalian.doAfterTextChanged {
                btnSave.show()
                btnAddImage.show()
                btnEdit.hide()
            }
            tvTanggalPeminjaman.doAfterTextChanged {
                btnSave.show()
                btnAddImage.show()
                btnEdit.hide()
            }
            tvJumlah.doAfterTextChanged {
                btnSave.show()
                btnAddImage.show()
                btnEdit.hide()
            }
            tvJaminan.doAfterTextChanged {
                btnSave.show()
                btnAddImage.show()
                btnEdit.hide()
            }
            tvCatatan.doAfterTextChanged {
                btnSave.show()
                btnAddImage.show()
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
                tvNamaPeminjam.text.toString().isEmpty() ||
                tvOrganisasiPeminjam.text.toString().isEmpty() ||
                tvDeskripsiBarang.text.toString().isEmpty() ||
                tvKondisiAwal.text.toString().isEmpty() ||
                tvTanggalPeminjaman.text.toString().isEmpty() ||
                tvTanggalPengembalian.text.toString().isEmpty() ||
                tvJumlah.text.toString().isEmpty() ||
                tvJaminan.text.toString().isEmpty() ||
                tvCatatan.text.toString().isEmpty() ||
                ivItem.toString().isEmpty()
            ) {
                isValid = false
                toast("Enter message")
            }

            return isValid
        }
    }

    private fun getPeminjaman(callback: (Peminjaman) -> Unit) {
        binding.apply {
            peminjamanViewModel.getImageUrl.observe(viewLifecycleOwner) { urlLink ->
                val peminjaman = Peminjaman(
                    id = objPeminjaman?.id ?: "",
                    namaBarang = tvNamaBarang.text?.toString() ?: "",
                    kodeBarang = tvKodeBarang.text?.toString() ?: "",
                    namaPeminjam = tvNamaPeminjam.text?.toString() ?: "",
                    organisasiPeminjam = tvOrganisasiPeminjam.text?.toString() ?: "",
                    deskripsiBarang = tvDeskripsiBarang.text?.toString() ?: "",
                    kondisiAwal = tvKondisiAwal.text?.toString() ?: "",
                    tanggalPeminjaman = tvTanggalPeminjaman.text?.toString() ?: "",
                    tanggalPengembalian = tvTanggalPengembalian.text?.toString() ?: "",
                    jumlah = tvJumlah.text?.toString() ?: "",
                    jaminan = tvJaminan.text?.toString() ?: "",
                    catatan = tvCatatan.text?.toString() ?: "",
                    foto = urlLink
                )
                callback.invoke(peminjaman)
            }
        }
    }

    private fun onDonePressed() {
        binding.apply {
            if (imageUri !== null) {
                peminjamanViewModel.onUploadSingleFile(imageUri!!) { state ->
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
                            getPeminjaman { peminjaman ->
                                peminjamanViewModel.updatePeminjaman(peminjaman)
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
            tvNamaPeminjam.isEnabled = isDisable
            tvOrganisasiPeminjam.isEnabled = isDisable
            tvDeskripsiBarang.isEnabled = isDisable
            tvKondisiAwal.isEnabled = isDisable
            tvTanggalPeminjaman.isEnabled = isDisable
            tvTanggalPengembalian.isEnabled = isDisable
            tvJumlah.isEnabled = isDisable
            tvJaminan.isEnabled = isDisable
            tvCatatan.isEnabled = isDisable
            ivItem.isEnabled = isDisable
        }
    }

    private fun showDatePicker(callback: (Date) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(requireContext(), { _: DatePicker, y: Int, m: Int, d: Int ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(Calendar.YEAR, y)
                selectedCalendar.set(Calendar.MONTH, m)
                selectedCalendar.set(Calendar.DAY_OF_MONTH, d)
                selectedDate = selectedCalendar.time
                callback.invoke(selectedDate!!)
            }, year, month, day)

        datePickerDialog.show()
    }

    private fun initTanggalPeminjaman() {
        binding.tvTanggalPeminjaman.setOnClickListener {
            showDatePicker { date ->
                val formattedDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(date)
                binding.tvTanggalPeminjaman.setText(formattedDate)
                validateDates()
            }
        }
    }

    private fun initTanggalPengembalian() {
        binding.tvTanggalPengembalian.setOnClickListener {
            showDatePicker { date ->
                val formattedDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(date)
                binding.tvTanggalPengembalian.setText(formattedDate)
                validateDates()
            }
        }
    }

    private fun initDatePickers() {
        initTanggalPeminjaman()
        initTanggalPengembalian()
    }

    private fun validateDates() {
        binding.apply {
            val peminjamanDate = tvTanggalPeminjaman.text?.toString()
            val pengembalianDate = tvTanggalPengembalian.text?.toString()
            if (!peminjamanDate.isNullOrEmpty() && !pengembalianDate.isNullOrEmpty()) {
                val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val peminjaman = dateFormatter.parse(peminjamanDate)
                val pengembalian = dateFormatter.parse(pengembalianDate)
                if (peminjaman != null && pengembalian != null && peminjaman.after(pengembalian)) {
                    toast("Pengembalian lebih awal!")
                    tvTanggalPeminjaman.setText("")
                    tvTanggalPengembalian.setText("")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}