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

        peminjamanViewModel.addPeminjaman.observe(viewLifecycleOwner) { state ->
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
                    objPeminjaman = state.data.first
                    Handler(Looper.getMainLooper()).postDelayed({
                        findNavController().navigateUp()
                    }, 1500)
                }
            }
        }
    }

    private fun updateUI() {
        objPeminjaman = arguments?.getParcelable("peminjaman")
        objPeminjaman?.let { data ->
            binding.etNamaBarang.setText(data.namaBarang)
            binding.etKodeBarang.setText(data.kodeBarang)
            binding.etNamaPeminjam.setText(data.namaPeminjam)
            binding.etOrganisasiPeminjam.setText(data.organisasiPeminjam)
            binding.etDeskripsiBarang.setText(data.deskripsiBarang)
            binding.etKondisiAwal.setText(data.kondisiAwal)
            binding.etTanggalPeminjaman.setText(data.tanggalPeminjaman)
            binding.etTanggalPengembalian.setText(data.tanggalPengembalian)
            binding.etJumlah.setText(data.jumlah)
            binding.etJaminan.setText(data.jaminan)
            binding.etCatatan.setText(data.catatan)
        }
        binding.rvImages.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvImages.adapter = adapter
        binding.rvImages.itemAnimator = null
        binding.btnAddImage.setOnClickListener {
            binding.pbAddImage.show()
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }

        }
        binding.btnSave.setOnClickListener {
            if (validation()) {
                onDonePressed()
            }
        }
    }

    private fun onDonePressed() {
        if (imageUris.isNotEmpty()) {
            peminjamanViewModel.onUploadSingleFile(imageUris.first()) { state ->
                when (state) {
                    is UiState.Loading -> {
                        binding.progressBar.show()
                    }

                    is UiState.Failure -> {
                        binding.progressBar.hide()
                        toast(state.error)
                    }

                    is UiState.Success -> {
                        binding.progressBar.hide()
                        peminjamanViewModel.addPeminjaman(getPeminjaman())
                    }
                }
            }
        } else {
            peminjamanViewModel.addPeminjaman(getPeminjaman())
        }
    }

    private fun getPeminjaman(): Peminjaman {
        return Peminjaman(
            id = objPeminjaman?.id ?: "",
            namaBarang = binding.etNamaBarang.text.toString(),
            kodeBarang = binding.etKodeBarang.text.toString(),
            namaPeminjam = binding.etNamaPeminjam.text.toString(),
            organisasiPeminjam = binding.etOrganisasiPeminjam.text.toString(),
            deskripsiBarang = binding.etDeskripsiBarang.text.toString(),
            kondisiAwal = binding.etKondisiAwal.text.toString(),
            tanggalPeminjaman = binding.etTanggalPeminjaman.text.toString(),
            tanggalPengembalian = binding.etTanggalPengembalian.text.toString(),
            jumlah = binding.etJumlah.text.toString(),
            jaminan = binding.etJaminan.text.toString(),
            catatan = binding.etCatatan.text.toString(),
            foto = getImageUrls()
        )
//            .apply { authViewModel.getSession { this.user_id = it?.id ?: "" } }
    }

    private fun validation(): Boolean {
        var isValid = true

        if (
            binding.etNamaBarang.text.toString().isEmpty() ||
            binding.etKodeBarang.text.toString().isEmpty() ||
            binding.etNamaPeminjam.text.toString().isEmpty() ||
            binding.etOrganisasiPeminjam.text.toString().isEmpty() ||
            binding.etDeskripsiBarang.text.toString().isEmpty() ||
            binding.etKondisiAwal.text.toString().isEmpty() ||
            binding.etTanggalPeminjaman.text.toString().isEmpty() ||
            binding.etTanggalPeminjaman.text.toString().isEmpty() ||
            binding.etJumlah.text.toString().isEmpty() ||
            binding.etJaminan.text.toString().isEmpty() ||
            binding.etCatatan.text.toString().isEmpty() ||
            binding.ivFoto.toString().isEmpty()
        ) {
            isValid = false
            toast("Enter message")
        }

        return isValid
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
