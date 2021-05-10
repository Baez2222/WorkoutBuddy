package com.example.android.workoutbuddy

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.marginBottom
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.android.workoutbuddy.databinding.ActivityCtsearchresultBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CTSearchResultActivity: AppCompatActivity() {

    private lateinit var binding: ActivityCtsearchresultBinding
    private lateinit var textView_searchresult: TextView
    private lateinit var linearLayout_searchresult: LinearLayout

    private lateinit var username:String
    private lateinit var searchString:String

    private val client = AsyncHttpClient()
    private val base_api_url = "https://api.nal.usda.gov/fdc/v1/foods/search?api_key=2UfJlOXOSvxR31QcLnjDTidF5IQWpdUMRGxZQ6pH&query="


    private val appViewModel : AppViewModel by viewModels{
        AppViewModelFactory((application as AppApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCtsearchresultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        textView_searchresult = binding.textViewSearchresult
        linearLayout_searchresult = binding.linearlayoutSearchresults


        username = intent.getStringExtra("username").toString()
        searchString = intent.getStringExtra("search").toString()

        textView_searchresult.text = textView_searchresult.text.toString() + searchString


        // call api to get random character
        client.get(base_api_url + searchString, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
//                Toast.makeText(this@CTSearchResultActivity, responseBody?.let { String(it) }, Toast.LENGTH_LONG).show()
                val foodArray = JSONObject(responseBody?.let { String(it) }).getJSONArray("foods")

                for (i in 0 until foodArray.length()){
                    val foodDescr = (foodArray[i] as JSONObject).get("description").toString()
                    val foodNutrients = (foodArray[i] as JSONObject).getJSONArray("foodNutrients")
                    val foodId = (foodArray[i] as JSONObject).get("fdcId").toString().toInt()
                    var calories = ""
                    for (j in 0 until foodNutrients.length()){
                        if((foodNutrients[j] as JSONObject).get("nutrientId").toString() == "1008"){
                            calories = (foodNutrients[j] as JSONObject).get("value").toString()
                            break
                        }
                    }

                    val button = Button(this@CTSearchResultActivity)
                    button.setBackgroundColor(resources.getColor(R.color.purple_200, theme))
                    button.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT).apply {
                        this.setMargins(5,5,5,5)
                    }
                    button.text = foodDescr + ", " + calories + " CAL"
                    linearLayout_searchresult.addView(button)

                    button.setOnClickListener {
                        val popup = PopupWindow(it.context)
                        val view = layoutInflater.inflate(R.layout.popupfoodsearch, null)
                        popup.isFocusable = true
                        popup.update()
                        view.findViewById<TextView>(R.id.textView_ppFood).text = foodDescr

                        view.findViewById<Button>(R.id.button_ppSubmit).setOnClickListener {
                            if (view.findViewById<EditText>(R.id.editTextNumber_quantity).text.isEmpty()){
                                Toast.makeText(this@CTSearchResultActivity, "Enter Quantity", Toast.LENGTH_SHORT).show()
                            }
                            else{
                                val currentDate = LocalDate.now()
                                val date = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(currentDate)
                                val foodInfo = Food(username, foodDescr, calories.toInt(), foodId, date, view.findViewById<EditText>(R.id.editTextNumber_quantity).text.toString().toInt())
                                appViewModel.insertFood(foodInfo)
                                finish()
                            }
                        }

                        popup.contentView = view
                        popup.isOutsideTouchable = true
                        popup.showAtLocation(view, Gravity.CENTER, 0, 0)
                    }
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                Log.e("api error", String(responseBody!!))
            }

        })


    }
}