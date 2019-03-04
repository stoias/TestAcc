package com.example.testacc

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),SensorEventListener,SurfaceHolder.Callback {
    private var start:Boolean = false
    private var xText:String? = null
    private var yText:String? = null
    private var zText:String? = null

    private var surfaceWidth:Int = 0
    private var surfaceHeight:Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val holder = surfaveView.holder // ここ
        holder.addCallback(this)

        button.setOnClickListener {
            if(start == false){
                button.setText("STOP")
                start = true
            }else{
                button.setText("START")
                start = false
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(event == null) return
        if(event.sensor.type == Sensor.TYPE_ACCELEROMETER){
            xText = event.values[0].toString()
            yText = event.values[1].toString()
            zText = event.values[2].toString()
        }
     }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager  // SenserManagerの指定
        val accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)      // 加速度センサーの指定
        sensorManager.registerListener(this,accSensor,SensorManager.SENSOR_DELAY_GAME)  // センサーの更新頻度（3-30ms）
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        surfaceWidth = width
        surfaceHeight = height
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.unregisterListener(this)
    }

}
