package com.zexceed.skripsiehapp.ui.adapter

import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zexceed.skripsiehapp.data.model.Peminjaman
import com.zexceed.skripsiehapp.databinding.ItemListPeminjamanBinding
import java.io.File


class PeminjamanSearchAdapter(
    val onItemClicked: (Int, Peminjaman) -> Unit,
) : RecyclerView.Adapter<PeminjamanSearchAdapter.MyViewHolder>() {

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
                Glide.with(ivListBorder.context).load(item.foto).into(ivListBorder)
                cardPeminjaman.setOnClickListener {
                    onItemClicked.invoke(
                        bindingAdapterPosition,
                        item
                    )
                }
            }
        }
    }
}