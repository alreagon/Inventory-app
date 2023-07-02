package com.zexceed.skripsiehapp.ui.adapter

import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zexceed.skripsiehapp.R
import com.zexceed.skripsiehapp.data.model.Peminjaman
import com.zexceed.skripsiehapp.databinding.ItemListPeminjamanBinding
import java.io.File


class PeminjamanAdapter(
    val onItemClicked: (Int, Peminjaman) -> Unit
) : RecyclerView.Adapter<PeminjamanAdapter.MyViewHolder>() {

    private var list: MutableList<Peminjaman> = arrayListOf()

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
                val foto = item.foto
                if (foto.isNullOrEmpty()) {
                    ivList.setImageResource(getRandomDrawable())
                } else {
                    Glide.with(ivList.context).load(foto).into(ivList)
                }
                cardPeminjaman.setOnClickListener {
                    onItemClicked.invoke(
                        bindingAdapterPosition, item
                    )
                }
            }
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
}