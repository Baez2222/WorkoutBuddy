package com.example.android.workoutbuddy

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.example.android.workoutbuddy.database.AppApplication
import com.example.android.workoutbuddy.database.AppViewModel
import com.example.android.workoutbuddy.database.AppViewModelFactory
import com.example.android.workoutbuddy.databinding.ActivityCalorietrackerBinding
import java.io.IOException
import java.io.InputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CalorieTrackerActivity: AppCompatActivity() {

    private lateinit var binding : ActivityCalorietrackerBinding
    private lateinit var editText_daily : EditText
    private lateinit var textView_consumed: TextView
    private lateinit var textView_remainder: TextView
    private lateinit var editText_searchFood: EditText
    private lateinit var button_search: Button
    private lateinit var tableLayout_ct: TableLayout
    private var consumed = 0
    private var daily = 0
    private lateinit var username: String
    private lateinit var imageView_logo: ImageView

    private val appViewModel : AppViewModel by viewModels{
        AppViewModelFactory((application as AppApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCalorietrackerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        editText_daily = binding.editTextNumberDaily
        textView_consumed = binding.textViewConsumed
        textView_remainder = binding.textViewRemainder
        editText_searchFood = binding.editTextSearchFood
        button_search = binding.buttonCTsearch
        tableLayout_ct = binding.tableCt
        imageView_logo = binding.imageView4

        username = intent.getStringExtra("username").toString()

        // get calorie intake
        appViewModel.getCalorieIntake(username).observe(this, Observer {
            if(editText_daily.text.isEmpty()){
                editText_daily.setText(it.toString())
                daily = it
                // update consumed and remainder textviews
                textView_consumed.text = consumed.toString()
                daily = editText_daily.text.toString().toInt() - textView_consumed.text.toString().toInt()
                textView_remainder.text = daily.toString()
            }
        })


        editText_daily.addTextChangedListener {
            // update consumed and remainder textviews
            textView_consumed.text = consumed.toString()
            if(it!!.isNotEmpty()){
                daily = it.toString().toInt()
            }
            else{
                daily = 0
            }
            daily -= textView_consumed.text.toString().toInt()
            textView_remainder.text = daily.toString()
        }



        button_search.setOnClickListener {
            if(editText_searchFood.text.isEmpty()){
                Toast.makeText(this, "Empty Search", Toast.LENGTH_SHORT).show()
            }
            else{
                val intent = Intent(this, CTSearchResultActivity::class.java)
                intent.putExtra("username", username)
                intent.putExtra("search", editText_searchFood.text.toString())
                startActivity(intent)
            }
        }

        // show list of foods from that day
        val currentDate = LocalDate.now()
        val date = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(currentDate)
        val currRows = tableLayout_ct.childCount
        appViewModel.getFoodListByDate(date, username).observe(this, Observer {
            for(i in (currRows-1) until it.size){
                val tableRow = TableRow(this)
                tableRow.layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                )

                val textView_quant = TextView(this)
                textView_quant.text = it[i].quantity.toString()
                textView_quant.textSize = 16F
                textView_quant.gravity = Gravity.CENTER
                textView_quant.layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                ).apply {
                    weight = 1F
                }
                textView_quant.gravity = Gravity.CENTER
                tableRow.addView(textView_quant)


                val textView_name = TextView(this)
                textView_name.text = it[i].description
                textView_name.textSize = 16F
                textView_name.setEms(10)
                textView_name.gravity = Gravity.CENTER
                textView_name.isSingleLine = true
                textView_name.ellipsize = TextUtils.TruncateAt.MARQUEE
                textView_name.marqueeRepeatLimit = -1
                textView_name.isSelected = true
                textView_name.layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                ).apply {
                    weight = 2F
                }
                textView_name.gravity = Gravity.CENTER
                tableRow.addView(textView_name)


                val textView_cal = TextView(this)
                textView_cal.text = (it[i].calories*it[i].quantity).toString()
                consumed += it[i].calories*it[i].quantity
                textView_cal.textSize = 16F
                textView_cal.gravity = Gravity.CENTER
                textView_cal.layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                ).apply {
                    weight = 1F
                }
                textView_cal.gravity = Gravity.CENTER
                tableRow.addView(textView_cal)

                // update consumed and remainder textviews
                textView_consumed.text = consumed.toString()
                daily = editText_daily.text.toString().toInt() - textView_consumed.text.toString().toInt()
                textView_remainder.text = daily.toString()


                tableLayout_ct.addView(tableRow)
            }
        })

        // set logo
        var inputStream: InputStream? = null
        try {
            inputStream = assets.open("free-logo.png")
            val brew = Drawable.createFromStream(inputStream, null)
            imageView_logo.setImageDrawable(brew)
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }



    override fun onStop() {
        super.onStop()
        if(daily != editText_daily.text.toString().toInt()){
            appViewModel.updateCalorieIntake(editText_daily.text.toString().toInt(), username)
        }
    }
}