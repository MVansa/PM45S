package conta.de.bar.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import conta.de.bar.myapplication.databinding.ItemProdutoBinding
import conta.de.bar.myapplication.models.InfoProduto

class ProdutoAdapter(private val produtos: List<InfoProduto>) : RecyclerView.Adapter<ProdutoAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProdutoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val produto = produtos[position]
        holder.bind(produto)
    }

    override fun getItemCount(): Int = produtos.size

    inner class ProductViewHolder(private val binding: ItemProdutoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(produto: InfoProduto) {
            binding.tvNomeProduto.text = produto.nomeProduto
            binding.tvPrecoProduto.text = "Preço: ${produto.valor}"
            binding.tvValorPago.text = "Valor Pago: ${produto.valorPago}"
        }
    }
}