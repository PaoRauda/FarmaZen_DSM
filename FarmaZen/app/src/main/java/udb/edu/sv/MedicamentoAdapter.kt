package udb.edu.sv

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import udb.edu.sv.datos.Medicamento

class MedicamentoAdapter(
    private val medicamentoList: List<Medicamento>,
    private val onAddToCartClick: (Medicamento) -> Unit
) : RecyclerView.Adapter<MedicamentoAdapter.MedicamentoViewHolder>() {

    private var buttonText: String = "Añadir"

    inner class MedicamentoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreTextView: TextView = itemView.findViewById(R.id.medicamentoNombre)
        private val precioTextView: TextView = itemView.findViewById(R.id.medicamentoPrecio)
        private val cantidadTextView: TextView = itemView.findViewById(R.id.medicamentoCantidad)
        private val addToCartButton: Button = itemView.findViewById(R.id.btnAgregar)


        fun bind(medicamento: Medicamento) {
            nombreTextView.text = medicamento.nombre
            precioTextView.text = "${medicamento.precio} USD"
            cantidadTextView.text  = "Unidades:" + medicamento.cantidad.toString()

            addToCartButton.text = buttonText

            addToCartButton.setOnClickListener {
                onAddToCartClick(medicamento)
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicamentoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medicamento, parent, false)
        return MedicamentoViewHolder(view)
    }

    override fun onBindViewHolder(holder: MedicamentoViewHolder, position: Int) {
        val medicamento = medicamentoList[position]
        holder.bind(medicamento)
    }

    override fun getItemCount(): Int {
        return medicamentoList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAddToCartButtonText(text: String) {
        buttonText = text
        this.notifyDataSetChanged()
    }
}
