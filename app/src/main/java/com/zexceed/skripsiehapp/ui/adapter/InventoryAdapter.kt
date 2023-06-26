import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zexceed.skripsiehapp.R
import com.zexceed.skripsiehapp.data.model.Inventory
import com.zexceed.skripsiehapp.databinding.ItemListInventoryBinding
import java.io.File

class InventoryAdapter(
    private val onItemClicked: (Int, Inventory) -> Unit
) : RecyclerView.Adapter<InventoryAdapter.MyViewHolder>() {

    private var list: MutableList<Inventory> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemListInventoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateInventory(list: MutableList<Inventory>) {
        this.list = list
        notifyDataSetChanged()
    }

    inner class MyViewHolder(private val binding: ItemListInventoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var currentItem: Inventory? = null

        init {
            binding.cardInventory.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    currentItem?.let { onItemClicked(position, it) }
                }
            }
        }

        fun bind(item: Inventory) {
            currentItem = item
            with(binding) {
                tvItemName.text = item.namaBarang
                tvKodeBarang.text = item.kodeBarang
                tvStatus.text = item.status
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
