package conta.de.bar.myapplication.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import conta.de.bar.myapplication.entity.Pessoa
import conta.de.bar.myapplication.entity.Produto
import conta.de.bar.myapplication.models.InfoProduto

class DatabaseHandler(private val context : Context) : SQLiteOpenHelper( context, DATABASE_NAME, null, DATABASE_VERSION ) {

    companion object {
        private const val DATABASE_NAME = "dbfile.sqlite"
        private const val DATABASE_VERSION = 2
        private const val TABLE_PRODUTO = "produto"
        private const val TABLE_PESSOA = "pessoa"
        private const val TABLE_PRODUTO_PESSOA = "produto_pessoa"

    }

    override fun onCreate(db: SQLiteDatabase) {
        //criação de tabelas
       val createPessoaTable =
            """CREATE TABLE IF NOT EXISTS $TABLE_PESSOA (
                id INTEGER PRIMARY KEY,
                nome TEXT
            )
        """
        db.execSQL(createPessoaTable)

        val createProdutoTable = """
            CREATE TABLE IF NOT EXISTS $TABLE_PRODUTO (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT,
                valor REAL
            )
        """
        db.execSQL(createProdutoTable)


        val createProdutoPessoaTable = """
            CREATE TABLE IF NOT EXISTS $TABLE_PRODUTO_PESSOA (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                produto_id INTEGER,
                pessoa_id INTEGER,
                FOREIGN KEY (produto_id) REFERENCES $TABLE_PRODUTO(id),
                FOREIGN KEY (pessoa_id) REFERENCES $TABLE_PESSOA(id)
            )
        """
        db.execSQL(createProdutoPessoaTable)

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL( "DROP TABLE IF EXISTS $TABLE_PESSOA " )
        db.execSQL( "DROP TABLE IF EXISTS $TABLE_PRODUTO " )
        db.execSQL( "DROP TABLE IF EXISTS $TABLE_PRODUTO_PESSOA " )
        onCreate( db )
    }

    fun insertPessoa( pessoa : Pessoa) {
        val registro = ContentValues()
        registro.put( "nome", pessoa.nome )

        val banco = this.writableDatabase

        banco.insert( TABLE_PESSOA, null, registro )
    }

    fun insertProduto(nome: String, valor: Double): Int {
        val registro = ContentValues()
        registro.put( "nome", nome )
        registro.put( "valor", valor )

        val banco = this.writableDatabase

        val id = banco.insert( TABLE_PRODUTO, null, registro )

        return id.toInt()
    }

    fun insertPessoaProduto(produtoId: Int, pessoaId: Int) {
        val registro = ContentValues()
        registro.put( "produto_id", produtoId )
        registro.put( "pessoa_id", pessoaId )

        val banco = this.writableDatabase

        banco.insert( TABLE_PRODUTO_PESSOA, null, registro )
    }

    fun calcularPagamentoPorPessoa(): MutableList<String> {
        val resultado = mutableListOf<String>()
        val banco = this.readableDatabase

        val listaDePessoas = listarNomes()

        val pessoaIdToNomeMap = mutableMapOf<String, String>()

        val queryPessoas = "SELECT id, nome FROM pessoa"
        val cursorPessoas = banco.rawQuery(queryPessoas, null)

        while (cursorPessoas.moveToNext()) {
            val pessoaId = cursorPessoas.getString(cursorPessoas.getColumnIndexOrThrow("id"))
            val pessoaNome = cursorPessoas.getString(cursorPessoas.getColumnIndexOrThrow("nome"))
            pessoaIdToNomeMap[pessoaId] = pessoaNome
        }
        cursorPessoas.close()

        val valoresPorPessoa = mutableMapOf<String, Double>()
        listaDePessoas.forEach { pessoa ->
            valoresPorPessoa[pessoa] = 0.0
        }

        val query = """
    SELECT pr.id, pr.nome, pr.valor, pp.pessoa_id, COUNT(pp.pessoa_id) AS qnt_pessoa
    FROM produto pr
    JOIN produto_pessoa pp ON pr.id = pp.produto_id
    GROUP BY pr.id
"""

        val cursor = banco.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val produtoId = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val produtoValor = cursor.getDouble(cursor.getColumnIndexOrThrow("valor"))
            val qntPessoas = cursor.getInt(cursor.getColumnIndexOrThrow("qnt_pessoa"))

            val valorPorPessoa = produtoValor / qntPessoas

            val produtoPessoas = mutableListOf<String>()

            val subQuery = "SELECT pessoa_id FROM produto_pessoa WHERE produto_id = ?"
            val subCursor = banco.rawQuery(subQuery, arrayOf(produtoId.toString()))

            while (subCursor.moveToNext()) {
                val pessoaId = subCursor.getString(subCursor.getColumnIndexOrThrow("pessoa_id"))
                produtoPessoas.add(pessoaId)
            }
            subCursor.close()

            produtoPessoas.forEach { pessoaId ->
                val nomePessoa = pessoaIdToNomeMap[pessoaId]
                if (nomePessoa != null) {
                    valoresPorPessoa[nomePessoa] = valoresPorPessoa.getOrDefault(nomePessoa, 0.0) + valorPorPessoa
                }
            }
        }
        cursor.close()



        listaDePessoas.forEach { pessoa ->
            val valorPago = valoresPorPessoa[pessoa] ?: 0.0
            resultado.add("${pessoa} R$ %.2f".format(valorPago))
        }

        return resultado
    }

    fun listarNomes(): List<String> {
        val listaNomes = mutableListOf<String>()
        val cursor = this.readableDatabase.rawQuery("SELECT nome FROM pessoa", null)
        while (cursor.moveToNext()) {
            val nome = cursor.getString(cursor.getColumnIndexOrThrow("nome"))
            listaNomes.add(nome)
        }
        cursor.close()
        return listaNomes
    }

    fun listPessoa() : Cursor {
        val banco = this.writableDatabase

        val registros = banco.query(
            TABLE_PESSOA, arrayOf("id", "nome"), null, null, null, null, null
        )
        return registros
    }

    fun limparBanco() {
        val dbFile = context.getDatabasePath(DATABASE_NAME) // Get the database path
        if (dbFile.exists()) {
            context.deleteDatabase(DATABASE_NAME)  // Deletes the current database
            Toast.makeText(context, "Registros Excluído", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Registros Excluído", Toast.LENGTH_SHORT).show()
        }
    }

}