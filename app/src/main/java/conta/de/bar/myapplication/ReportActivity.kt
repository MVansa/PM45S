package conta.de.bar.myapplication

import android.R
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.database.sqlite.SQLiteDatabase
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import conta.de.bar.myapplication.adapter.ReportAdapter
import conta.de.bar.myapplication.database.DatabaseHandler
import conta.de.bar.myapplication.databinding.ActivityReportBinding
import conta.de.bar.myapplication.models.InfoProduto
import conta.de.bar.myapplication.models.PessoaReport

class ReportActivity  : AppCompatActivity() {

    private lateinit var binding: ActivityReportBinding
    private lateinit var banco: DatabaseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        banco = DatabaseHandler(this)

        binding.btReset.setOnClickListener {
            deleteDatabaseReport()
        }

        val reportList = banco.calcularPagamentoPorPessoa()

        val lista = binding.listViewReport

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, reportList)
        lista.adapter = adapter
    }

    private fun deleteDatabaseReport() {

        banco.limparBanco()
        Toast.makeText(this, "Conta finalizada e dados Excluídos", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

}
