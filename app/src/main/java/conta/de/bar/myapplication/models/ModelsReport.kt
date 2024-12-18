package conta.de.bar.myapplication.models

data class PessoaReport(
    val nome: String,
    var totalPago: Double,
    val produtos: MutableList<InfoProduto>
)

data class InfoProduto(
    val nomeProduto: String,
    val valor: Double,
    val valorPago: Double
)
