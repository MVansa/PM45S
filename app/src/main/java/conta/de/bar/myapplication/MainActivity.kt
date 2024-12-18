package conta.de.bar.myapplication

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.content.Intent
import android.widget.CheckBox
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import conta.de.bar.myapplication.database.DatabaseHandler
import conta.de.bar.myapplication.databinding.ActivityMainBinding
import conta.de.bar.myapplication.entity.Pessoa


class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var banco : DatabaseHandler



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate( layoutInflater )
        setContentView( binding.root )

        binding.etPessoa.setText( intent.getStringExtra( "nome" ) )

        initDatabase()

        binding.btAdicionar.setOnClickListener {
            btAdicionarOnClcik()
        }

        binding.btGoProduto.setOnClickListener {
            btGotoProduto()
        }

        binding.btDeleteDb.setOnClickListener {
            deleteDatabase()
        }
    }

     private fun deleteDatabase() {
        banco.limparBanco()
        Toast.makeText(this, "Conta cancelada e dados Excluídos", Toast.LENGTH_SHORT).show()
    }

    private fun initDatabase() {

        banco = DatabaseHandler( this )

    }

    private fun btAdicionarOnClcik() {

        if (binding.etPessoa.text.toString().isNotEmpty()) {

            val pessoa = Pessoa(
                0,
                binding.etPessoa.text.toString(),
            )
            banco.insertPessoa(pessoa)
        }

        Toast.makeText( this, "Sucesso", Toast.LENGTH_LONG ).show()

        displayPessoas()
    }

    private fun btGotoProduto() {

        val intent = Intent(this, PagProdutosActivity::class.java)
        startActivity(intent)
    }

    private fun displayPessoas() {
        val cursor = banco.listPessoa()
        val stringBuilder = StringBuilder()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val nome = cursor.getString(cursor.getColumnIndexOrThrow("nome"))

                stringBuilder.append("Nome: $nome\n")
            } while (cursor.moveToNext())
        }

        cursor.close()

        // Update the TextView using binding
        binding.tvListaNomes.text = stringBuilder.toString()
    }

}
