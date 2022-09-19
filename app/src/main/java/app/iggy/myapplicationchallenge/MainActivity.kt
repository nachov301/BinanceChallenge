package app.iggy.myapplicationchallenge

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.iggy.myapplicationchallenge.adapter.BinanceAdapter
import app.iggy.myapplicationchallenge.model.Binance
import com.airbnb.lottie.LottieAnimationView
import com.binance.api.client.BinanceApiClientFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    val API_KEY = BuildConfig.API_KEY
    val SECRET = BuildConfig.SECRET

    lateinit var binanceAdapter: BinanceAdapter
    lateinit var binanceRV: RecyclerView

    private val CCL = 200
    private val list: ArrayList<Binance> = arrayListOf()

    lateinit var lottieAnim : LottieAnimationView

    var flag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        lottieAnim = findViewById(R.id.lottie_loading)
        showLoadingAnim()

        val factory = BinanceApiClientFactory.newInstance(
            API_KEY, SECRET)

        val client = factory.newRestClient()


        val mainLooper = Looper.getMainLooper()
        GlobalScope.launch {
            client.ping()
            val serverTime = client.serverTime

            val cryptoList = arrayListOf<String>()
            val BUSD = "BUSD"

            cryptoList.add("BTC")
            cryptoList.add("ETH")
            cryptoList.add("BNB")
            cryptoList.add("LUNA")
            cryptoList.add("SOL")
            cryptoList.add("LTC")
            cryptoList.add("MATIC")
            cryptoList.add("AVAX")
            cryptoList.add("XRP")
            cryptoList.add("USDT")

            var arsPesos : Double

            for(i in 0 until cryptoList.size){

                if (cryptoList[i] == "USDT"){
                    val orderBook = client.getOrderBook("${BUSD}${cryptoList[i]}", 1)
                    val bids = orderBook.bids
                    val firstBidEntry = bids[0]
                    arsPesos = (1 / firstBidEntry.price.toDouble()) * CCL
                    list.add(Binance(cryptoList[i], "", arsPesos))
                }else{
                    val orderBook = client.getOrderBook("${cryptoList[i]}$BUSD", 1)
                    val asks = orderBook.asks
                    val firstAskEntry = asks[0]
                    arsPesos = firstAskEntry.price.toDouble() * CCL
                    Log.d(TAG, "onCreate: Crypto: ${cryptoList[i]}, pesos: $arsPesos")
                    list.add(Binance(cryptoList[i], "", arsPesos))
                }


            }

            Handler(mainLooper).post {
                binanceRV = findViewById(R.id.recycler_binance)
                binanceRV.layoutManager = LinearLayoutManager(this@MainActivity)
                binanceAdapter = BinanceAdapter(list)
                binanceRV.adapter = binanceAdapter
                hideAnim()
                flag = true
            }
        }



    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val search = menu.findItem(R.id.actionSearch)
        val searchView = search.actionView as SearchView
        searchView.queryHint = "Search crypto..."
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                if (flag){
                    filter(newText)
                }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }


    private fun filter(text: String) {

        val filteredlist: ArrayList<Binance> = ArrayList()

        for (item in list) {
            if (item.symbol.lowercase().contains(text.lowercase()) ||
                item.name.lowercase().contains(text.lowercase())) {
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            showEmptyAnim()
        } else {
            hideAnim()
        }
        binanceAdapter.filterList(filteredlist)
    }

    private fun showEmptyAnim(){
        lottieAnim.setAnimation(R.raw.empty_anim)
        if (lottieAnim.visibility != View.VISIBLE){
            lottieAnim.visibility = View.VISIBLE
        }
    }

    private fun showLoadingAnim(){
        lottieAnim.setAnimation(R.raw.loading_animation)
        if (lottieAnim.visibility != View.VISIBLE){
            lottieAnim.visibility = View.VISIBLE
        }
    }

    private fun hideAnim(){
        if (lottieAnim.visibility != View.GONE){
            lottieAnim.visibility = View.GONE
        }
    }

}