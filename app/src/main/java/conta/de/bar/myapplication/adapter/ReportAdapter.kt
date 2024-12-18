package conta.de.bar.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import conta.de.bar.myapplication.models.InfoProduto
import conta.de.bar.myapplication.models.PessoaReport
import conta.de.bar.myapplication.databinding.ItemReportBinding


class ReportAdapter(private val dadosReport: List<PessoaReport>) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding = ItemReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val pessoaReport = dadosReport[position]
        holder.bind(pessoaReport)
    }

    override fun getItemCount(): Int {
        return dadosReport.size
    }

    inner class ReportViewHolder(private val binding: ItemReportBinding) : RecyclerView.ViewHolder(binding.root) {

        val reportTextView: TextView = binding.textViewReport

        fun bind(pessoaReport: PessoaReport) {
            reportTextView.text = "${pessoaReport.nome}: R$ ${pessoaReport.totalPago}"
        }
    }
}
