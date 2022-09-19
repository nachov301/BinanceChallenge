package app.iggy.myapplicationchallenge.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.iggy.myapplicationchallenge.model.Binance
import app.iggy.myapplicationchallenge.R
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.roundToInt

class BinanceAdapter(var binanceList: List<Binance>) : RecyclerView.Adapter<BinanceAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val crypto_symbol = itemView.findViewById<TextView>(R.id.crypto_symbol)
        val crypto_name = itemView.findViewById<TextView>(R.id.crypto_name)
        val crypto_price = itemView.findViewById<TextView>(R.id.crypto_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.binance_item,
            parent, false
        )
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (binanceList.isNotEmpty()){
            holder.crypto_symbol.text = binanceList[position].symbol
            holder.crypto_name.text = binanceList[position].name
            val roundOffPrice = roundOffDecimal(binanceList[position].price)
            holder.crypto_price.text = "ARS $roundOffPrice"
        }
    }

    private fun roundOffDecimal(number: Double): Double? {
        val df = DecimalFormat("#.####")
        df.roundingMode = RoundingMode.CEILING
        return df.format(number).toDouble()
    }

    override fun getItemCount(): Int {
        return binanceList.size
    }

    fun filterList(filterlist: List<Binance>) {
        binanceList = filterlist
        notifyDataSetChanged()
    }
}