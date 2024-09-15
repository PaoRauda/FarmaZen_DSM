package udb.edu.sv

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var buttonRegister: Button
    private lateinit var buttonLogin: Button

    //Escuchador de FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        buttonRegister = findViewById<Button>(R.id.btnRegister)
        buttonRegister.setOnClickListener {
            val email = findViewById<EditText>(R.id.etRegisterEmail).text.toString()
            val password = findViewById<EditText>(R.id.etRegisterPassword).text.toString()
            this.register(email, password)
        }
        buttonLogin = findViewById<Button>(R.id.btnLogin)
        buttonLogin.setOnClickListener {
            this.goToLogin()
        }

        //Validar si existe un usuario activo
        this.checkUser()

    }

    override fun onResume(){
        super.onResume()
        auth.addAuthStateListener(authStateListener)
    }

    override fun onPause(){
        super.onPause()
        auth.removeAuthStateListener(authStateListener)
    }

    private fun register(email: String, password: String){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if(task.isSuccessful){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

    private fun goToLogin(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun checkUser(){
        //Verificacion del usuario
        authStateListener = FirebaseAuth.AuthStateListener { auth ->
            if(auth.currentUser != null) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}