package udb.edu.sv

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import udb.edu.sv.datos.Compra
import udb.edu.sv.datos.Medicamento
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CanastaActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MedicamentoAdapter
    private lateinit var textViewTotal: TextView
    private lateinit var buttonCompra: Button
    private lateinit var database: DatabaseReference
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_canasta)

        recyclerView = findViewById(R.id.recyclerViewCanasta)
        textViewTotal = findViewById(R.id.textViewTotal)
        buttonCompra = findViewById(R.id.buttonCompra)


        recyclerView.layoutManager = LinearLayoutManager(this)

        //Configuración de la Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbarCanasta)
        setSupportActionBar(toolbar)

        //Inicializar el adaptador
        adapter = MedicamentoAdapter(emptyList()) { medicamento ->
        }
        recyclerView.adapter = adapter

        //Referencia a Firebase
        database = FirebaseDatabase.getInstance().reference
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: "default_user_id"
        val canastaRef = database.child("usuarios").child(userId).child("canasta")

        //Leer datos de Firebase
        canastaRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val canastaList = mutableListOf<Medicamento>()
                var total = 0
                for (dataSnapshot in snapshot.children) {
                    val medicamento = dataSnapshot.getValue(Medicamento::class.java)
                    medicamento?.let {
                        canastaList.add(it)
                        total += it.precio * it.cantidad
                    }
                }
                // Actualiza el RecyclerView con la lista de la canasta y el precio total
                adapter = MedicamentoAdapter(canastaList) { medicamento ->
                    eliminarDeCanasta(medicamento)
                }
                recyclerView.adapter = adapter
                textViewTotal.text = "Total: $total USD"

                adapter.updateAddToCartButtonText("Eliminar")

            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo de errores
                Toast.makeText(this@CanastaActivity, "Error al cargar la canasta: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        buttonCompra.setOnClickListener {
            realizarCompra()
        }
    }

    private fun eliminarDeCanasta(medicamento: Medicamento) {
        val canastaRef = database.child("usuarios").child(userId).child("canasta").child(medicamento.nombre)

        canastaRef.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Medicamento eliminado de la canasta", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error al eliminar el medicamento: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun realizarCompra() {
        //se obtener la referencia a la base de datos de Firebase
        val database = FirebaseDatabase.getInstance().reference
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: "default_user_id"
        val canastaRef = database.child("usuarios").child(userId).child("canasta")
        val historialRef = database.child("usuarios").child(userId).child("historial")

        //Se obtiene la fecha actual para la compra
        val fecha = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        //Se obtiene la lista de medicamentos en la canasta
        canastaRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableMapOf<String, Int>()
                var total = 0

                for (dataSnapshot in snapshot.children) {
                    val medicamento = dataSnapshot.getValue(Medicamento::class.java)
                    medicamento?.let {
                        items[it.nombre] = it.cantidad
                        total += it.precio * it.cantidad
                    }
                }

                //Se crea un objeto con los datos
                val compra = Compra(
                    fecha = fecha,
                    total = total,
                    items = items
                )

                //Guardar la compra en el historial del usuario
                historialRef.push().setValue(compra).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //Limpiar la canasta
                        canastaRef.removeValue().addOnCompleteListener { canastaTask ->
                            if (canastaTask.isSuccessful) {
                                Toast.makeText(this@CanastaActivity, "Compra realizada con éxito", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@CanastaActivity, "Error al limpiar la canasta: ${canastaTask.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this@CanastaActivity, "Error al guardar la compra: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CanastaActivity, "Error al obtener los datos de la canasta: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.activity_canasta -> {
                goToCanasta()
                true
            }
            R.id.action_view_history -> {
                goToHistorial()
                true
            }
            R.id.action_sign_out -> {
                FirebaseAuth.getInstance().signOut().also {
                    Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun goToCanasta() {
        val intent = Intent(this, CanastaActivity::class.java)
        startActivity(intent)
    }
    private fun goToHistorial() {
        val intent = Intent(this, HistorialActivity::class.java)
        startActivity(intent)
    }

}
