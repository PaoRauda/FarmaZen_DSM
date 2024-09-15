package udb.edu.sv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import udb.edu.sv.datos.Compra

class CompraAdapter(private val compraList: List<Compra>) : RecyclerView.Adapter<CompraAdapter.CompraViewHolder>() {

    inner class CompraViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val fechaTextView: TextView = itemView.findViewById(R.id.compra_fecha)
        private val totalTextView: TextView = itemView.findViewById(R.id.compra_total)
        private val itemsTextView: TextView = itemView.findViewById(R.id.compra_items)

        fun bind(compra: Compra) {
            fechaTextView.text = compra.fecha
            totalTextView.text = "Total: ${compra.total} USD"
            itemsTextView.text = compra.items.entries.joinToString("\n") { "${it.key}: ${it.value}" }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompraViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_compra, parent, false)
        return CompraViewHolder(view)
    }

    override fun onBindViewHolder(holder: CompraViewHolder, position: Int) {
        val compra = compraList[position]
        holder.bind(compra)
    }

    override fun getItemCount(): Int {
        return compraList.size
    }
}
