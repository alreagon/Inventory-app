package com.zexceed.skripsiehapp.ui.adapter

import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zexceed.skripsiehapp.R
import com.zexceed.skripsiehapp.data.model.Peminjaman
import com.zexceed.skripsiehapp.databinding.ItemListPeminjamanBinding
import java.io.File


class PeminjamanAdapter(
    val onItemClicked: (Int, Peminjaman) -> Unit
) : RecyclerView.Adapter<PeminjamanAdapter.MyViewHolder>() {

    private var list: MutableList<Peminjaman> = arrayListOf()
    private lateinit var suggestions: List<String>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            ItemListPeminjamanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }


    fun updatePeminjaman(list: MutableList<Peminjaman>) {
        this.list = list
        notifyDataSetChanged()
    }

    //    fun updatePeminjamanString(list: MutableList<Char>) {
//        this.list = list
//        notifyDataSetChanged()
//    }
    fun setList(suggestionList: List<String>) {
        suggestions = suggestionList
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        list.removeAt(position)
        notifyItemChanged(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(val binding: ItemListPeminjamanBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Peminjaman) {
            binding.apply {
                tvItemName.text = item.namaBarang
                tvTanggalKembali.text = item.tanggalPengembalian
                tvPeminjam.text = item.namaPeminjam
                val foto = item.foto[0].replace("file://", "")
                if (isValidLocalUrl(foto)) {
                    val imgBitmap = BitmapFactory.decodeFile(foto)
                    if (imgBitmap != null) {
                        ivList.setImageBitmap(imgBitmap)
                    } else {
                        // Gambar tidak valid, tampilkan gambar acak dari drawable
                        ivList.setImageResource(getRandomDrawable())
                    }
                } else {
                    // URL tidak valid, tampilkan gambar acak dari drawable
                    ivList.setImageResource(getRandomDrawable())
                }
                cardPeminjaman.setOnClickListener {
                    onItemClicked.invoke(
                        bindingAdapterPosition,
                        item
                    )
                }
            }
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
}