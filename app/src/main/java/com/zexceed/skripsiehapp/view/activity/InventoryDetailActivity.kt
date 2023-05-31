package com.zexceed.skripsiehapp.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zexceed.skripsiehapp.R

class InventoryDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory_detail)
    }


    companion object {
        const val INVENTORY_ID = "inventory_id"
    }
}