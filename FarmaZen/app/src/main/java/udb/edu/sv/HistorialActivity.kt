package udb.edu.sv

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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

        //Configuración de la Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbarHistorial)
        setSupportActionBar(toolbar)


        //Inicializar el adaptador
        adapter = CompraAdapter(emptyList())
        recyclerView.adapter = adapter

        val database = FirebaseDatabase.getInstance().reference
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: "default_user_id"
        val historialRef = database.child("usuarios").child(userId).child("historial")

        //Leer datos de Firebase
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
                Toast.makeText(this@HistorialActivity, "Error al cargar el historial: ${error.message}", Toast.LENGTH_SHORT).show()
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