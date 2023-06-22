package com.zexceed.skripsiehapp.ui.view.fragment.peminjaman

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.zexceed.skripsiehapp.R
import com.zexceed.skripsiehapp.data.model.Peminjaman
import com.zexceed.skripsiehapp.databinding.FragmentPeminjamanAddBinding
import com.zexceed.skripsiehapp.ui.viewmodel.AuthViewModel
import com.zexceed.skripsiehapp.ui.adapter.ImageListingAdapter
import com.zexceed.skripsiehapp.ui.viewmodel.PeminjamanViewModel
import com.zexceed.skripsiehapp.util.UiState
import com.zexceed.skripsiehapp.util.hide
import com.zexceed.skripsiehapp.util.show
import com.zexceed.skripsiehapp.util.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PeminjamanAddFragment : Fragment(R.layout.fragment_peminjaman_add) {

    val TAG: String = "PeminjamanAddFragment"
    private var _binding: FragmentPeminjamanAddBinding? = null
    private val binding get() = _binding!!
    private val peminjamanViewModel: PeminjamanViewModel by viewModels()
    var isEdit = false
    private var objPeminjaman: Peminjaman? = null
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
                adapter.updateList(imageUris)
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPeminjamanAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            if (imageUris.isNotEmpty()) {
                peminjamanViewModel.onUploadSingleFile(imageUris.first()) { state ->
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
                            peminjamanViewModel.addPeminjaman(getPeminjaman())
                        }
                    }
                }
            } else {
                btnAddImage.setText("Please input image!")
            }
        }
    }

    private fun getPeminjaman(): Peminjaman {
        binding.apply {
            return Peminjaman(
                id = objPeminjaman?.id ?: "",
                namaBarang = etNamaBarang.text.toString(),
                kodeBarang = etKodeBarang.text.toString(),
                namaPeminjam = etNamaPeminjam.text.toString(),
                organisasiPeminjam = etOrganisasiPeminjam.text.toString(),
                deskripsiBarang = etDeskripsiBarang.text.toString(),
                kondisiAwal = etKondisiAwal.text.toString(),
                tanggalPeminjaman = etTanggalPeminjaman.text.toString(),
                tanggalPengembalian = etTanggalPengembalian.text.toString(),
                jumlah = etJumlah.text.toString(),
                jaminan = etJaminan.text.toString(),
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

    private fun getImageUrls(): List<String> {
        if (imageUris.isNotEmpty()) {
            return imageUris.map { it.toString() }
        } else {
            return objPeminjaman?.foto ?: arrayListOf()
        }
    }

    private fun onRemoveImage(pos: Int, item: Uri) {
        adapter.removeItem(pos)
        if (objPeminjaman != null) {
            binding.rvImages.performClick()
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
