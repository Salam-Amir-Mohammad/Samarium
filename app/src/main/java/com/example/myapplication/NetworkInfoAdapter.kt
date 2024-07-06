package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.database.NetworkInfo

class NetworkInfoAdapter : RecyclerView.Adapter<NetworkInfoAdapter.NetworkInfoViewHolder>() {

    private var dataList = listOf<NetworkInfo>()

    class NetworkInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCellTechnology: TextView = itemView.findViewById(R.id.cellTechnologyTextView)
        val tvLatitude: TextView = itemView.findViewById(R.id.latitudeTextView)
        val tvLongitude: TextView = itemView.findViewById(R.id.longitudeTextView)
        val tvEventTime: TextView = itemView.findViewById(R.id.eventTimeTextView)
        val tvcellTechnology: TextView = itemView.findViewById(R.id.cellTechnologyTextView)
        val tvCellId: TextView = itemView.findViewById(R.id.cellIdTextView)
        val tvPlmnId: TextView = itemView.findViewById(R.id.plmnIdTextView)
        val tvRac: TextView = itemView.findViewById(R.id.racTextView)
        val tvTac: TextView = itemView.findViewById(R.id.tacTextView)
        val tvLac: TextView = itemView.findViewById(R.id.lacTextView)
        val tvRsrq: TextView = itemView.findViewById(R.id.rsrqTextView)
        val tvRsrp: TextView = itemView.findViewById(R.id.rsrpTextView)
        val tvRscp: TextView = itemView.findViewById(R.id.rscpTextView)
        val tvEcNo: TextView = itemView.findViewById(R.id.ecNoTextView)
        val tvQualityOfService: TextView = itemView.findViewById(R.id.qualityOfServiceTextView)
        val tvBatchId: TextView = itemView.findViewById(R.id.batchIdTextView)
        // Add other TextViews here as needed
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NetworkInfoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_network_info, parent, false)
        return NetworkInfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: NetworkInfoViewHolder, position: Int) {
        val item = dataList[position]
        holder.tvCellTechnology.text = item.cellTechnology
        holder.tvLatitude.text = "Latitude: ${item.latitude}"
        holder.tvLongitude.text = "Longitude: ${item.longitude}"
        holder.tvEventTime.text = "Event Time: ${item.eventTime}"
        holder.tvcellTechnology.text = "Tv Cell Technology: ${item.cellTechnology}"
        holder.tvCellId.text = "Cell Id: ${item.cellId ?: "N/A"}"
        holder.tvPlmnId.text = "PLMN Id: ${item.plmnId ?: "N/A"}"
        holder.tvRac.text = "RAC: ${item.rac ?: "N/A"}"
        holder.tvTac.text = "TAC: ${item.tac ?: "N/A"}"
        holder.tvLac.text = "LAC: ${item.lac ?: "N/A"}"
        holder.tvRsrq.text = "RSRQ: ${item.rsrq ?: "N/A"}"
        holder.tvRsrp.text = "RSRP: ${item.rsrp ?: "N/A"}"
        holder.tvRscp.text = "RSCP: ${item.rscp ?: "N/A"}"
        holder.tvEcNo.text = "EC/NO: ${item.ecNo ?: "N/A"}"
        holder.tvQualityOfService.text = "Quality Of Service: ${item.qualityOfService}"
        holder.tvBatchId.text = "Batch Id: ${item.batchId}"

//        holder.tvcellId.text = "cellId: ${item.cellId}"
//        holder.tvqualityOfService.text = "qualityOfService: ${item.qualityOfService}"

        // Bind other data here as needed
    }

    override fun getItemCount(): Int = dataList.size

    fun setData(data: List<NetworkInfo>) {
        this.dataList = data
        notifyDataSetChanged()
    }
}
