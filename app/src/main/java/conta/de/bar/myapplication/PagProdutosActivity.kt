package conta.de.bar.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import conta.de.bar.myapplication.database.DatabaseHandler
import conta.de.bar.myapplication.databinding.ActivityPagprodutosBinding

class PagProdutosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPagprodutosBinding
    private lateinit var banco: DatabaseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPagprodutosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initDatabase()

        carregarPessoa()

        binding.btnAddProduto.setOnClickListener {
            addProduto()
        }
        binding.btGoToReport.setOnClickListener {
            goToReport()
        }
        binding.btReset.setOnClickListener {
            deleteDatabase()
        }


    }

    private fun goToReport() {
        val intent = Intent(this, ReportActivity::class.java)
        startActivity(intent)
    }

    private fun initDatabase() {

        banco = DatabaseHandler( this )

    }

    private fun carregarPessoa() {
        val cursor = banco.listPessoa()

        if (cursor.moveToFirst()) {
            do {
                val pessoaId = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val pessoaNome = cursor.getString(cursor.getColumnIndexOrThrow("nome"))

                val checkBox = CheckBox(this)
                checkBox.text = pessoaNome
                checkBox.tag = pessoaId

                binding.containerPeople.addView(checkBox)

            } while (cursor.moveToNext())
        }

        cursor.close()
    }

    private fun addProduto() {
        val valorProduto = binding.etValorProduto.text.toString().toDoubleOrNull()
        val nomeProduto = binding.etProduto.text.toString()

        if (valorProduto == null || nomeProduto.isEmpty()) {
            Toast.makeText(this, "Por favor preencha os campos necessários", Toast.LENGTH_SHORT).show()
            return
        }

        val productId = banco.insertProduto(nomeProduto, valorProduto)

        val checkboxes = binding.containerPeople.children
        checkboxes.forEach { view ->
            if (view is CheckBox && view.isChecked) {
                val personId = view.tag as Int
                banco.insertPessoaProduto(productId, personId)
            }
        }

        Toast.makeText(this, "Produto adicionado com sucesso!", Toast.LENGTH_SHORT).show()
        resetPagina()
    }



    private fun deleteDatabase() {
        banco.limparBanco()
        Toast.makeText(this, "Conta cancelada e dados Excluídos", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun resetPagina() {
        binding.etValorProduto.text.clear()
        binding.etProduto.text.clear()

        val checkboxes = binding.containerPeople.children
        checkboxes.forEach { view ->
            if (view is CheckBox) {
                view.isChecked = false
            }
        }
    }
}