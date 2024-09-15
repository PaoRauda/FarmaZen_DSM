package udb.edu.sv

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import udb.edu.sv.datos.Compra

class HistorialActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CompraAdapter
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        recyclerView = findViewById(R.id.recyclerViewHistorial)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inicializar el adaptador con una lista vacía
        adapter = CompraAdapter(emptyList())
        recyclerView.adapter = adapter

        // Referencia a la base de datos de Firebase
        val database = FirebaseDatabase.getInstance().reference
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: "default_user_id"
        val historialRef = database.child("usuarios").child(userId).child("historial")

        // Leer datos de Firebase
        historialRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val historialList = mutableListOf<Compra>()
                for (dataSnapshot in snapshot.children) {
                    val compra = dataSnapshot.getValue(Compra::class.java)
                    compra?.let { historialList.add(it) }
                }
                // Actualiza el RecyclerView con la lista de compras
                adapter = CompraAdapter(historialList)
                recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo de errores
                Toast.makeText(this@HistorialActivity, "Error al cargar el historial: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}