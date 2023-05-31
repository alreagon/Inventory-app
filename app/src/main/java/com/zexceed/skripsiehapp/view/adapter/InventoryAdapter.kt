package com.zexceed.skripsiehapp.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zexceed.skripsiehapp.databinding.ItemListInventoryBinding
import com.zexceed.skripsiehapp.model.Inventory

class InventoryAdapter(
    private val items: ArrayList<Inventory>,
    private val listSavedInventoryId: ArrayList<String>,
    private val onItemClicked: (Inventory) -> Unit,
    private val onSaveIconClicked: (Inventory) -> Unit
) : RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder>() {


    class InventoryViewHolder(val binding: ItemListInventoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder =
        InventoryViewHolder(
            ItemListInventoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        val inventoryResponse = items[position]
        val namaBarang = inventoryResponse.namaBarang
        val kodeBarang = inventoryResponse.kodeBarang
        val status = inventoryResponse.status
        val foto = inventoryResponse.foto

        holder.apply {
            itemView.setOnClickListener {
                onItemClicked(inventoryResponse)
            }

            Log.d("adapter", "nama inventory Adapter : $namaBarang")
            holder.binding.apply {

                Glide.with(holder.itemView.context).load(foto).into(ivList)
                tvItemName.text = namaBarang
                tvKodeBarang.text = kodeBarang
                tvStatus.text = status

//            cardInventory.setOnClickListener {
//                mListener.onItemClick(mgnList[holder.adapterPosition], position)
//            }
            }
        }
    }

    override fun getItemCount() = items.size
}