package com.zexceed.skripsiehapp.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.zexceed.skripsiehapp.R
import com.zexceed.skripsiehapp.databinding.ActivityInventoryDetailBinding
import com.zexceed.skripsiehapp.viewmodel.InventoryDetailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InventoryDetailActivity : AppCompatActivity() {
    private val inventoryDetailViewModel : InventoryDetailViewModel by viewModels()
    private lateinit var binding: ActivityInventoryDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        val inventoryId = intent.getStringExtra(INVENTORY_ID)
//        inventoryDetailViewModel.currentInventory(inventoryId!!).observe(this) { mInventory ->
//            binding.apply {
//                ivRecipe.loadImage(mInventory.imageUrl)
//                tvEstimatedPrice.text =
//                    "Total harga : ± " + mInventory.estimatedPrice.toString().toCurrencyFormat()
//                tvEstimatedTime.text = "Estimasi waktu memasak : ± " + mInventory.estimatedTime
//                tvListIngredient.text = mInventory.listIngredient
//                tvListIngredientPrice.text = mInventory.listIngredientPrice
//                tvSteps.text = mInventory.steps
//                tvRecipeName.text = mInventory.name
//                tvRating.text = rating.to1Digit().toString()
//                btnBack.setOnClickListener {
//                    finish()
//                }
//                rvReview.apply {
//                    adapter = ReviewAdapter(mInventory.listReview)
//                    setHasFixedSize(true)
//                }
//                recipeDetailViewModel.currentUserLiveData.observe(this@RecipeDetailActivity) {
//                    if (it == null) {
//                        btnToLogin.visibility = View.VISIBLE
//                        clLoggedIn.visibility = View.GONE
//                        btnToLogin.setOnClickListener {
//                            startActivity(
//                                Intent(
//                                    this@RecipeDetailActivity,
//                                    LoginActivity::class.java
//                                )
//                            )
//                        }
//                        btnSave.setOnClickListener {
//                            startActivity(
//                                Intent(
//                                    this@RecipeDetailActivity,
//                                    LoginActivity::class.java
//                                )
//                            )
//                        }
//                    } else {
//                        btnToLogin.visibility = View.GONE
//                        clLoggedIn.visibility = View.VISIBLE
//                        for (i in mInventory.listReview) {
//                            if (i.userId == it.uid) {
//                                when (i.rating) {
//                                    1.0 -> {
//                                        setStar(1.0)
//                                    }
//                                    2.0 -> {
//                                        setStar(2.0)
//                                    }
//                                    3.0 -> {
//                                        setStar(3.0)
//                                    }
//                                    4.0 -> {
//                                        setStar(4.0)
//                                    }
//                                    5.0 -> {
//                                        setStar(5.0)
//                                    }
//                                }
//                                etReview.setText(i.review)
//                            }
//                        }
//                        ivStar1.setOnClickListener {
//                            setStar(1.0)
//                        }
//                        ivStar2.setOnClickListener {
//                            setStar(2.0)
//                        }
//                        ivStar3.setOnClickListener {
//                            setStar(3.0)
//                        }
//                        ivStar4.setOnClickListener {
//                            setStar(4.0)
//                        }
//                        ivStar5.setOnClickListener {
//                            setStar(5.0)
//                        }
//                        recipeDetailViewModel.getPersonalNote(mInventory.id)
//                            .observe(this@RecipeDetailActivity) { mNote ->
//                                etPersonalNote.setText(mNote)
//                            }
//                        btnSaveNote.setOnClickListener {
//                            lifecycleScope.launch(Dispatchers.IO) {
//                                val message = recipeDetailViewModel.addPersonalNote(
//                                    mInventory.id,
//                                    etPersonalNote.text.toString()
//                                )
//                                lifecycleScope.launch(Dispatchers.Main) {
//                                    message.observe(this@RecipeDetailActivity) {
//                                        when (it) {
//                                            "SUCCESS" -> {
//                                                Toast.makeText(
//                                                    this@RecipeDetailActivity,
//                                                    "Success",
//                                                    Toast.LENGTH_SHORT
//                                                ).show()
//                                            }
//                                            "LOADING" -> {
//                                                Toast.makeText(
//                                                    this@RecipeDetailActivity,
//                                                    "Loading",
//                                                    Toast.LENGTH_SHORT
//                                                ).show()
//                                            }
//                                            else -> {
//                                                Toast.makeText(
//                                                    this@RecipeDetailActivity,
//                                                    it,
//                                                    Toast.LENGTH_SHORT
//                                                ).show()
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                        btnSaveReview.setOnClickListener {
//                            val mStar=recipeDetailViewModel.star.value
//                            Log.w("TEZZ", mStar.toString())
//                            if (mStar != null) {
//                                lifecycleScope.launch(Dispatchers.IO) {
//                                    val message = recipeDetailViewModel.addRatingAndReview(
//                                        mInventory.id,
//                                        mStar!!,
//                                        etReview.text.toString()
//                                    )
//                                    lifecycleScope.launch(Dispatchers.Main) {
//                                        message.observe(this@RecipeDetailActivity) {mIt->
//                                            when (mIt) {
//                                                "SUCCESS" -> {
//                                                    Toast.makeText(
//                                                        this@RecipeDetailActivity,
//                                                        "Success",
//                                                        Toast.LENGTH_SHORT
//                                                    ).show()
//                                                }
//                                                "LOADING" -> {
//                                                    Toast.makeText(
//                                                        this@RecipeDetailActivity,
//                                                        "Loading",
//                                                        Toast.LENGTH_SHORT
//                                                    ).show()
//                                                }
//                                                else -> {
//                                                    Toast.makeText(
//                                                        this@RecipeDetailActivity,
//                                                        mIt,
//                                                        Toast.LENGTH_SHORT
//                                                    ).show()
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            } else {
//                                Toast.makeText(
//                                    this@RecipeDetailActivity,
//                                    "Beri nilai",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//
//                        }
//                        recipeDetailViewModel.savedRecipeId.observe(this@RecipeDetailActivity) { savedRecipeId ->
//                            // Drawables for save
//                            val saveDrawable =
//                                this@RecipeDetailActivity.resources.getDrawable(R.drawable.ic_bookmark)
//                            val saveBorderDrawable =
//                                this@RecipeDetailActivity.resources.getDrawable(R.drawable.ic_bookmark_border_white)
//                            Log.w("TEZZ", savedRecipeId.toString())
//                            if (savedRecipeId.contains(mInventory.id)) {
//                                btnSave.setImageDrawable(saveDrawable)
//                                btnSave.setOnClickListener {
//                                    lifecycleScope.launch(Dispatchers.IO) {
//                                        val message=recipeDetailViewModel.removeRecipeFromSaved(mInventory.id)
//                                        lifecycleScope.launch(Dispatchers.Main) {
//                                            message.observe(this@RecipeDetailActivity) {mIt->
//                                                when (mIt) {
//                                                    "SUCCESS" -> {
//                                                        Toast.makeText(
//                                                            this@RecipeDetailActivity,
//                                                            "Success",
//                                                            Toast.LENGTH_SHORT
//                                                        ).show()
//                                                    }
//                                                    "LOADING" -> {
//                                                        Toast.makeText(
//                                                            this@RecipeDetailActivity,
//                                                            "Loading",
//                                                            Toast.LENGTH_SHORT
//                                                        ).show()
//                                                    }
//                                                    else -> {
//                                                        Toast.makeText(
//                                                            this@RecipeDetailActivity,
//                                                            mIt,
//                                                            Toast.LENGTH_SHORT
//                                                        ).show()
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            } else {
//                                btnSave.setImageDrawable(saveBorderDrawable)
//                                btnSave.setOnClickListener {
//                                    lifecycleScope.launch(Dispatchers.IO) {
//                                        val message=recipeDetailViewModel.saveRecipe(mInventory.id)
//                                        lifecycleScope.launch(Dispatchers.Main) {
//                                            message.observe(this@RecipeDetailActivity) {mIt->
//                                                when (mIt) {
//                                                    "SUCCESS" -> {
//                                                        Toast.makeText(
//                                                            this@RecipeDetailActivity,
//                                                            "Success",
//                                                            Toast.LENGTH_SHORT
//                                                        ).show()
//                                                    }
//                                                    "LOADING" -> {
//                                                        Toast.makeText(
//                                                            this@RecipeDetailActivity,
//                                                            "Loading",
//                                                            Toast.LENGTH_SHORT
//                                                        ).show()
//                                                    }
//                                                    else -> {
//                                                        Toast.makeText(
//                                                            this@RecipeDetailActivity,
//                                                            mIt,
//                                                            Toast.LENGTH_SHORT
//                                                        ).show()
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//
//                    }
//
//
//                }
//            }
//        }
    }


    companion object {
        const val INVENTORY_ID = "inventory_id"
    }
}