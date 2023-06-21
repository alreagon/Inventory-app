package com.zexceed.skripsiehapp.ui.adapter

import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zexceed.skripsiehapp.data.model.Inventory
import com.zexceed.skripsiehapp.data.model.Peminjaman
import com.zexceed.skripsiehapp.databinding.ItemListInventoryBinding
import com.zexceed.skripsiehapp.databinding.ItemListPeminjamanBinding
import java.io.File


class InventoryAdapter(
    val onItemClicked: (Int, Inventory) -> Unit
) : RecyclerView.Adapter<InventoryAdapter.MyViewHolder>() {

    private var list: MutableList<Inventory> = arrayListOf()
    private lateinit var suggestions: List<String>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            ItemListInventoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }


    fun updateInventory(list: MutableList<Inventory>) {
        this.list = list
        notifyDataSetChanged()
    }

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

    inner class MyViewHolder(val binding: ItemListInventoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Inventory) {
            binding.apply {
                tvItemName.text = item.namaBarang
                tvKodeBarang.text = item.kodeBarang
                tvStatus.text = item.status

                val folder = Environment.getExternalStorageDirectory().path
                val files = folder
                Log.d("Your Tag", "onCreate: ${folder}")
                Log.d("Your Tag", "onCreate: Your files are ${files}")
                val imgFileeee = item.foto[0].replace("file://","")

                val imgBitmap = BitmapFactory.decodeFile(imgFileeee)
                ivList.setImageBitmap(imgBitmap)

                cardInventory.setOnClickListener {
                    onItemClicked.invoke(
                        bindingAdapterPosition,
                        item
                    )
                }
            }
        }
    }
}