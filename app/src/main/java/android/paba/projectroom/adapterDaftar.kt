package android.paba.projectroom

import android.content.Intent
import android.paba.projectroom.database.daftarBelanja
import android.paba.projectroom.database.daftarBelanjaDAO
import android.paba.projectroom.database2.historyBarang
import android.paba.projectroom.database2.historyBarangDAO
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class adapterDaftar(private val daftarBelanja: MutableList<daftarBelanja>,
                    private val historyBarangDAO: historyBarangDAO):
RecyclerView.Adapter<adapterDaftar.ListViewHolder>(){

    private lateinit var onItemClickCallback : OnItemClickCallback

    interface OnItemClickCallback {
        fun delData(dtBelanja: daftarBelanja)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    class ListViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        var _tvItem = itemView.findViewById<TextView>(R.id.tvItem)
        var _tvJumlah = itemView.findViewById<TextView>(R.id.tvJumlah)
        var _tvTanggal = itemView.findViewById<TextView>(R.id.tvTanggal)

        var _btnEdit = itemView.findViewById<ImageButton>(R.id.btnEdit)
        var _btnDelete = itemView.findViewById<ImageButton>(R.id.btnDelete)
        var _btnSelesai = itemView.findViewById<ImageButton>(R.id.btnSelesai)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(
            R.layout.item_recycler, parent,
            false
        )
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return daftarBelanja.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var daftar = daftarBelanja[position]

        holder._tvTanggal.setText(daftar.tanggal)
        holder._tvItem.setText(daftar.item)
        holder._tvJumlah.setText(daftar.jumlah)

        holder._btnEdit.setOnClickListener {
            val intent = Intent(it.context, TambahDaftar::class.java)
            intent.putExtra("id", daftar.id)
            intent.putExtra("addEdit", 1)
            it.context.startActivity(intent)
        }

        holder._btnDelete.setOnClickListener {
            onItemClickCallback.delData(daftar)
        }

        holder._btnSelesai.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                // Move item to history and delete from current list
                val historyItem = historyBarang(
                    id = daftar.id,
                    item = daftar.item,
                    jumlah = daftar.jumlah,
                    tanggal = daftar.tanggal,
                )

                // Insert into history and delete from daftar
                historyBarangDAO.insert(historyItem)
                onItemClickCallback.delData(daftar)

                withContext(Dispatchers.Main) {
                    daftarBelanja.removeAt(holder.adapterPosition)
                    notifyItemRemoved(holder.adapterPosition)
                }
            }
        }

    }

    fun isiData(daftar: List<daftarBelanja>){
        daftarBelanja.clear()
        daftarBelanja.addAll(daftar)
        notifyDataSetChanged()
    }

    fun isiDataHistory(daftar: List<daftarBelanja>){
        daftarBelanja.clear()
        daftarBelanja.addAll(daftar)
        notifyDataSetChanged()
    }
}