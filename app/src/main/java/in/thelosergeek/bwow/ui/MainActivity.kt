package `in`.thelosergeek.bwow.ui

import `in`.thelosergeek.bwow.R
import `in`.thelosergeek.bwow.data.api.ApiRequest
import `in`.thelosergeek.bwow.data.api.BASE_URL
import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity(), SensorEventListener {

    private val ACTIVITY_RECCOGNITION_CODE = 1

    var running = false
    lateinit var heart:TextView
    lateinit var count:TextView
    lateinit var sleepTime:TextView
    lateinit var trainingTime: TextView
    lateinit var progressBar: ProgressBar

    var sensorManager: SensorManager? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        askPermission()



        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        count = findViewById(R.id.count)
        heart = findViewById(R.id.hearRate)
        sleepTime = findViewById(R.id.sleepingHours)
        trainingTime = findViewById(R.id.trainingTime)
        progressBar = findViewById(R.id.my_progressBar)
        progressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));

        getData()

    }

    private fun askPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {

        } else {
            requestActivityRecognition();
        }    }

    private fun requestActivityRecognition() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            AlertDialog.Builder(this)
                .setTitle("Permission needed")
                .setMessage("This permission is needed because of activity recognition")
                .setPositiveButton("ok",
                    DialogInterface.OnClickListener { dialog, which ->
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                            ACTIVITY_RECCOGNITION_CODE
                        )
                    })
                .setNegativeButton("cancel",
                    DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                .create().show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                ACTIVITY_RECCOGNITION_CODE
            )
        }
    }



    private fun getData() {
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiRequest::class.java)


        GlobalScope.launch(Dispatchers.IO){
            try {
                val response = api.getDetails().awaitResponse()
                if (response.isSuccessful) {
                    val data = response.body()!!
                    withContext(Dispatchers.Main) {
                        heart.text = data.data.heartRate
                        sleepTime.text = data.data.sleepTime
                        trainingTime.text = data.data.trainingTime
                    }
                }
            }
            catch (e: Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(this@MainActivity,"Please Check Internet Connection",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onResume() {
        super.onResume()
        running = true
        val stepsSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepsSensor == null) {
            Toast.makeText(this, "No Step Counter Sensor!", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.registerListener(this, stepsSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        running = false
        sensorManager?.unregisterListener(this)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (running) {
            count.setText("" + event.values[0].toInt())
            progressBar.setProgress(event.values[0].toInt())
        }
    }


}