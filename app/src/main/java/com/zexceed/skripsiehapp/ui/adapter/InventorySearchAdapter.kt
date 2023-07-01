package com.zexceed.skripsiehapp.ui.adapter

import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zexceed.skripsiehapp.data.model.Inventory
import com.zexceed.skripsiehapp.databinding.ItemListInventoryBinding
import java.io.File


class InventorySearchAdapter(
    val onItemClicked: (Int, Inventory) -> Unit,
) : RecyclerView.Adapter<InventorySearchAdapter.MyViewHolder>() {

    private var list: MutableList<Inventory> = arrayListOf()
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
//                val imgFileeee = item.foto[0].replace("file://", "")
//                val imgBitmap = BitmapFactory.decodeFile(imgFileeee)
//                ivList.setImageBitmap(imgBitmap)

                Glide.with(ivListBorder.context).load(item.foto).into(ivListBorder)

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