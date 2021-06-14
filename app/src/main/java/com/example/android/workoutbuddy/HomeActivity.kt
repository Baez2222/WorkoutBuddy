package com.example.android.workoutbuddy

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.PowerManager
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.view.View.*
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer.measure
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import com.example.android.workoutbuddy.database.AppApplication
import com.example.android.workoutbuddy.database.AppViewModel
import com.example.android.workoutbuddy.database.AppViewModelFactory
import com.example.android.workoutbuddy.databinding.ActivityHomeBinding
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class HomeActivity: AppCompatActivity() {

    private lateinit var binding : ActivityHomeBinding
    private lateinit var button_startWorkout: Button
    private lateinit var button_calorieTracker: Button
    private lateinit var button_createWorkout: Button
    private lateinit var calendarView: CalendarView
    private lateinit var button_progressPic: Button
    private lateinit var button_exportData: Button
    private lateinit var newImagePath: String
    private lateinit var username: String
    private lateinit var imageView_logo: ImageView

    private val appViewModel : AppViewModel by viewModels{
        AppViewModelFactory((application as AppApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        button_startWorkout = binding.buttonStartWorkout
        button_calorieTracker = binding.buttonCalorieTracker
        button_createWorkout = binding.buttonCreateWorkout
        calendarView = binding.calendarView
        button_progressPic = binding.buttonAddProgressPic
        button_exportData = binding.buttonExportData
        imageView_logo = binding.imageView

        username = intent.getStringExtra("username").toString()


        button_createWorkout.setOnClickListener {
            val intent = Intent(this, CreateWorkoutActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }

        button_calorieTracker.setOnClickListener {
            val intent = Intent(this, CalorieTrackerActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }

        button_startWorkout.setOnClickListener {
            val popup = PopupWindow(this)
            val view = layoutInflater.inflate(R.layout.popupwindow, null)
            popup.contentView = view
            popup.isOutsideTouchable = true
            val llPopup : LinearLayout = view.findViewById(R.id.linearLayout_popup)

            // query all workout names
            if (username != null) {
                appViewModel.getWorkoutsByUsername(username).observe(this, Observer {
                    Log.println(Log.ERROR, "list size", it.size.toString())
                    if (it.isEmpty()) {
                        val textView = TextView(this)
                        textView.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        textView.text = "   No Workouts Created."
                        llPopup.addView(textView)
                    } else {
                        for (element in it) {
                            if(llPopup.childCount < it.size){
                                val button = Button(this)
                                button.layoutParams = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                                button.text = element
                                button.setOnClickListener {
                                    val intent = Intent(this, StartWorkoutMainActivity::class.java)
                                    intent.putExtra("username", username)
                                    intent.putExtra("workout", button.text.toString())
                                    popup.dismiss()
                                    startActivity(intent)
                                }
                                llPopup.addView(button)
                            }
                        }
                    }
                })
            }
            popup.showAtLocation(view, Gravity.CENTER, 0, 0)
        }

        val wakeLock : PowerManager.WakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag").apply {
                acquire()
            }
        }
        wakeLock.release()


//        calendarView.setOnDateChangeListener(OnDateChangeListener { calendarView, year, month, day ->
//            val popup = PopupWindow(this)
//            val view = layoutInflater.inflate(R.layout.popupwindow, null)
//            popup.contentView = view
//            popup.isOutsideTouchable = true
//        })


        calendarView.setOnDateChangeListener { calendarView, year, month, day ->
            val popup = PopupWindow(this)
            val view = layoutInflater.inflate(R.layout.popupcalendar, null)
            val dateView = view.findViewById<TextView>(R.id.textView_pcDate)
            val progressPic = view.findViewById<ImageView>(R.id.imageView_progressPicture)
            val linearLayout = view.findViewById<LinearLayout>(R.id.linearLayout_calendarTable)


            val selectedDate = year.toString()+"-"+String.format("%02d", month + 1)+"-"+String.format(
                "%02d",
                day
            )
            dateView.text = selectedDate

            appViewModel.getWorkoutsByDate(username, selectedDate).observe(this, Observer {
                if (it.isEmpty() || it == null) {
                    val textViewWorkout = TextView(this)
                    textViewWorkout.text = "No Workouts Recorded."
                    textViewWorkout.gravity = Gravity.CENTER
                    linearLayout.addView(textViewWorkout)
                } else {
                    var previous: String? = null
                    for (i in it) {
                        if (i.workout != previous) {
                            val textViewWorkout = TextView(this)
                            textViewWorkout.text = i.workout
                            textViewWorkout.gravity = Gravity.CENTER
                            linearLayout.addView(textViewWorkout)
                            previous = i.workout
                        }
                        val inflater = LayoutInflater.from(applicationContext)
                        val tableRow = inflater.inflate(R.layout.calendartablerow, null)
                        val exerciseName = tableRow.findViewById<TextView>(R.id.textView_calendarRowExercise)
                        val weight = tableRow.findViewById<TextView>(R.id.textView_calendarRowWeight)
                        val reps = tableRow.findViewById<TextView>(R.id.textView_calendarRowReps)
                        exerciseName.text = i.exercise
                        weight.text = i.weight.toString()
                        reps.text = i.reps.toString()
                        linearLayout.addView(tableRow)
                    }
                }
            })


            appViewModel.getPicture(selectedDate, username).observe(this, Observer {
                if (it != null) {
                    val imgFile = File(it.photoFile)
                    val myBitMap = BitmapFactory.decodeFile(imgFile.absolutePath)
                    progressPic.setImageBitmap(rotateBmp(myBitMap))
                }
            })

            popup.contentView = view
            popup.isOutsideTouchable = true
            popup.showAtLocation(view, Gravity.CENTER, 0, 0)
            popup.update(750, 900)
            Toast.makeText(this, selectedDate, Toast.LENGTH_SHORT).show();
        }


        button_progressPic.setOnClickListener {
            dispatchTakePictureIntent()
        }

        button_exportData.setOnClickListener {
            exportData()
        }

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

    // rotate image
    fun rotateBmp(bmp: Bitmap): Bitmap? {
        var bmp = bmp
        val matrix = Matrix()
        //set image rotation value to 90 degrees in matrix.
        matrix.postRotate(90F)
        //supply the original width and height, if you don't want to change the height and width of bitmap.
        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, matrix, true)
        return bmp
    }


    // opens takes saves
    private fun dispatchTakePictureIntent() {
        val currentDate = LocalDate.now()
        val timeStamp = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(currentDate)
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        takePictureIntent.resolveActivity(packageManager)?.also {
            // Create the File where the photo should go
            val photoFile: File? = try {
                createImageFile(timeStamp)
            } catch (ex: IOException) {
                // Error occurred while creating the File
                Toast.makeText(this, "Error creating file", Toast.LENGTH_SHORT).show()
                null
            }
            // Continue only if the File was successfully created
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this,
                    "com.example.android.fileprovider",
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, 1)
                val picture = Picture(username, newImagePath, timeStamp)
                appViewModel.insertPicture(picture)
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(timeStamp: String): File? {
        // Create an image file name
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            timeStamp,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
        // Save a file: path for use with ACTION_VIEW intents
        newImagePath = image.absolutePath
        return image
    }

    private fun exportData(){
        val csv_header = "workout, exercise, reps, weight, date"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val outfile = File.createTempFile(
            "workout-data",
            ".csv",
            storageDir
        )

        outfile.appendText(csv_header)
        outfile.appendText("\n")

        appViewModel.allExercises.observe(this, Observer {
            for(i in it){
                outfile.appendText(i.workout + "," + i.exercise + "," + i.reps + "," + i.weight + "," + i.date)
                outfile.appendText("\n")
            }

            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "com.example.android.fileprovider",
                outfile
            )
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.type = "vnd.android.cursor.dir/email"

            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Workout Buddy Workout Data")
            sendIntent.putExtra(Intent.EXTRA_STREAM, photoURI)
            startActivity(Intent.createChooser(sendIntent, "Send email"))

        })
    }


}