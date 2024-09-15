package udb.edu.sv

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
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
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import udb.edu.sv.datos.Medicamento

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MedicamentoAdapter
    private lateinit var database: DatabaseReference
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        //Configuración de la Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        // Inicializar el adaptador
        adapter = MedicamentoAdapter(emptyList()) { medicamento ->
            addToCart(medicamento)
        }

        recyclerView.adapter = adapter

        //Inicializar Firebase Database
        database = FirebaseDatabase.getInstance().reference
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: "default_user_id"
        val medicamentosRef = database.child("medicamentos")

        //Leer datos de Firebase
        medicamentosRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val medicamentoList = mutableListOf<Medicamento>()
                for (dataSnapshot in snapshot.children) {
                    val medicamento = dataSnapshot.getValue(Medicamento::class.java)
                    medicamento?.let { medicamentoList.add(it) }
                }

                adapter = MedicamentoAdapter(medicamentoList) { medicamento ->
                    addToCart(medicamento)
                }
                recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error al cargar medicamentos: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })


    }

    private fun addToCart(medicamento: Medicamento) {
        Log.d("MainActivity", "Intentando agregar a la canasta: ${medicamento.nombre}")

        val canastaRef = database.child("usuarios").child(userId).child("canasta").child(medicamento.nombre)

        canastaRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val currentItem = currentData.getValue(Medicamento::class.java)

                if (currentItem == null) {
                    currentData.value = medicamento
                } else {
                    currentItem.cantidad += medicamento.cantidad
                    currentData.value = currentItem
                }

                return Transaction.success(currentData)
            }

            override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {
                if (error != null) {
                    Toast.makeText(this@MainActivity, "Error al añadir medicamento a la canasta: ${error.message}", Toast.LENGTH_SHORT).show()
                } else if (committed) {
                    Toast.makeText(this@MainActivity, "Medicamento añadido a la canasta", Toast.LENGTH_SHORT).show()
                }
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