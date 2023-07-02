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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.zexceed.skripsiehapp.R
import com.zexceed.skripsiehapp.data.model.Peminjaman
import com.zexceed.skripsiehapp.databinding.FragmentPeminjamanAddBinding
import com.zexceed.skripsiehapp.ui.viewmodel.PeminjamanViewModel
import com.zexceed.skripsiehapp.util.UiState
import com.zexceed.skripsiehapp.util.hide
import com.zexceed.skripsiehapp.util.show
import com.zexceed.skripsiehapp.util.toast
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class PeminjamanAddFragment : Fragment() {

    val TAG: String = "PeminjamanAddFragment"
    private var _binding: FragmentPeminjamanAddBinding? = null
    private val binding get() = _binding!!
    private val peminjamanViewModel: PeminjamanViewModel by viewModels()
    private var objPeminjaman: Peminjaman? = null
    var imageUri: Uri? = null
    private var selectedDate: Date? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPeminjamanAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDatePickers()
        updateUI()
        observer()
        binding.icBack.setOnClickListener {
            findNavController().navigate(R.id.action_PeminjamanAddFragment_to_homeFragment)
        }

    }

    private fun observer() {
        binding.apply {
            peminjamanViewModel.addPeminjaman.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is UiState.Loading -> {
                        progressBar.show()
                        binding.pbAddImage.hide()
                        binding.btnAddImage.setText("Image ready!")
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
                        binding.pbAddImage.hide()
                        binding.btnAddImage.setText("Saved!")
                        toast(state.data.second)
                        objPeminjaman = state.data.first
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
                etNamaBarang.setText(data.namaBarang)
                etKodeBarang.setText(data.kodeBarang)
                etNamaPeminjam.setText(data.namaPeminjam)
                etOrganisasiPeminjam.setText(data.organisasiPeminjam)
                etDeskripsiBarang.setText(data.deskripsiBarang)
                etKondisiAwal.setText(data.kondisiAwal)
                etTanggalPeminjaman.setText(data.tanggalPeminjaman)
                etTanggalPengembalian.setText(data.tanggalPengembalian)
                etJumlah.setText(data.jumlah)
                etJaminan.setText(data.jaminan)
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
                }

        }

    }

    private fun onDonePressed() {
        binding.apply {
            if (imageUri !== null) {
                peminjamanViewModel.onUploadSingleFile(imageUri!!) { state ->
                    when (state) {
                        is UiState.Loading -> {
                            progressBar.show()
                            btnSave.setText("")
                            pbAddImage.hide()
                            btnAddImage.setText("Image ready!")
                        }

                        is UiState.Failure -> {
                            progressBar.hide()
                            btnSave.setText("Error!")
                            toast(state.error)
                        }

                        is UiState.Success -> {
                            progressBar.hide()
                            btnSave.setText("Data Saved!")
                            getPeminjaman { peminjaman ->
                                peminjamanViewModel.addPeminjaman(peminjaman)
                            }
                        }
                    }
                }
            } else {
                btnAddImage.setText("Please input image!")
                pbAddImage.hide()
            }
        }
    }

    private fun getPeminjaman(callback: (Peminjaman) -> Unit) {
        binding.apply {
            peminjamanViewModel.getImageUrl.observe(viewLifecycleOwner) { urlLink ->
                val peminjaman = Peminjaman(
                    id = objPeminjaman?.id ?: "",
                    namaBarang = etNamaBarang.text?.toString() ?: "",
                    kodeBarang = etKodeBarang.text?.toString() ?: "",
                    namaPeminjam = etNamaPeminjam.text?.toString() ?: "",
                    organisasiPeminjam = etOrganisasiPeminjam.text?.toString() ?: "",
                    deskripsiBarang = etDeskripsiBarang.text?.toString() ?: "",
                    kondisiAwal = etKondisiAwal.text?.toString() ?: "",
                    tanggalPeminjaman = etTanggalPeminjaman.text?.toString() ?: "",
                    tanggalPengembalian = etTanggalPengembalian.text?.toString() ?: "",
                    jumlah = etJumlah.text?.toString() ?: "",
                    jaminan = etJaminan.text?.toString() ?: "",
                    catatan = etCatatan.text?.toString() ?: "",
                    foto = urlLink
                )
                callback.invoke(peminjaman)
            }
        }
    }

    private fun validation(): Boolean {
        binding.apply {
            var isValid = true
            if (
                etNamaBarang.text.toString().isEmpty() ||
                etKodeBarang.text.toString().isEmpty() ||
                etNamaPeminjam.text.toString().isEmpty() ||
                etOrganisasiPeminjam.text.toString().isEmpty() ||
                etDeskripsiBarang.text.toString().isEmpty() ||
                etKondisiAwal.text.toString().isEmpty() ||
                etTanggalPeminjaman.text.toString().isEmpty() ||
                etTanggalPeminjaman.text.toString().isEmpty() ||
                etJumlah.text.toString().isEmpty() ||
                etJaminan.text.toString().isEmpty() ||
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
                imageUri = fileUri
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
        binding.etTanggalPeminjaman.setOnClickListener {
            showDatePicker { date ->
                val formattedDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(date)
                binding.etTanggalPeminjaman.setText(formattedDate)
                validateDates()
            }
        }
    }

    private fun initTanggalPengembalian() {
        binding.etTanggalPengembalian.setOnClickListener {
            showDatePicker { date ->
                val formattedDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(date)
                binding.etTanggalPengembalian.setText(formattedDate)
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
            val peminjamanDate = etTanggalPeminjaman.text?.toString()
            val pengembalianDate = etTanggalPengembalian.text?.toString()
            if (!peminjamanDate.isNullOrEmpty() && !pengembalianDate.isNullOrEmpty()) {
                val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val peminjaman = dateFormatter.parse(peminjamanDate)
                val pengembalian = dateFormatter.parse(pengembalianDate)
                if (peminjaman != null && pengembalian != null && peminjaman.after(pengembalian)) {
                    toast("Pengembalian lebih awal!")
                    etTanggalPeminjaman.setText("")
                    etTanggalPengembalian.setText("")
                }
            }
        }
    }

//    private fun getDatePeminjaman(): String {
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
