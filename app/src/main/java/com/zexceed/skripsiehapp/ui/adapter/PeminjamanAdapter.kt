package com.zexceed.skripsiehapp.ui.adapter

import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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
//                Picasso.get().load(item.foto).into(ivList)
//                Glide.with(ivList.context).load(item.foto).into(ivList)

                val folder = Environment.getExternalStorageDirectory().path
                val files = folder
                Log.d("Your Tag", "onCreate: ${folder}")
                Log.d("Your Tag", "onCreate: Your files are ${files}")

//                val uri = Uri.parse(item.foto[0])
//                val pdf = File(uri.path!!)
//                ivList.setImageBitmap(pdf)

                val imgFile = File(item.foto[0])
                val imgFileeee = item.foto[0].replace("file://","")

                val imgBitmap = BitmapFactory.decodeFile(imgFileeee)
                ivList.setImageBitmap(imgBitmap)

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