package com.example.testacc

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Paint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),SensorEventListener,SurfaceHolder.Callback {
    private var start:Boolean = true
    private var xText:Float = 0f
    private var yText:Float = 0f
    private var go:Boolean    = false

    private var surfaceWidth:Int = 0
    private var surfaceHeight:Int = 0

    private val radius = 50.0f
    private val coef = 1000.0f

    private var ballX: Float = 0f
    private var ballY: Float = 0f
    private var vx: Float = 0f
    private var vy: Float = 0f
    private var time :Long = 0L

    private var gX: Float = 540f
    private var gY: Float = 700f
    private var gR: Float = 130f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_main)

        val holder = surfaceView.holder
        holder.addCallback(this)

        button.setOnClickListener {
            if(start == false){
                if(ballX + radius <= gX + gR && ballX - radius >= gX - gR && ballY + radius <= gY + gR  && ballY - radius >= gY - gR){
                    val toast = Toast.makeText(applicationContext,"SUCCESS",Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 10, 10)
                    toast.show()
                }
                button.text = "STOP"
                start = true
                go = false
                val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
                sensorManager.unregisterListener(this)
            }else{
                button.text = "START"
                start = false
                go = true
                start()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {

            if(event == null) return
            if(time == 0L) time = System.currentTimeMillis()
            if(event.sensor.type == Sensor.TYPE_ACCELEROMETER){
                var x:Float
                var y:Float
                var t:Float
                if(go == true){
                    x = xText
                    y = yText
                    t = 0f
                    time = 0L
                }else{
                    x = -event.values[0]
                    y = event.values[1]
                    t = (System.currentTimeMillis() - time).toFloat()
                    time = System.currentTimeMillis()
                    t /= 1000.0f
                }
                xText = -event.values[0]
                yText = event.values[1]

                val dx = vx * t + x * t * t / 2.0f
                val dy = vy * t + y * t * t  / 2.0f
                ballX += dx * coef
                ballY += dy * coef
                vx += x * t
                vy += y * t

                if(ballX - radius < 0 && vx < 0){
                    vx = -vx / 1.5f
                    ballX = radius
                }else if(ballX + radius > surfaceWidth && vx > 0){
                    vx = -vx / 1.5f
                    ballX = surfaceWidth - radius

                }
                if(ballY - radius < 0 && vy < 0){
                    vy = -vy / 1.5f
                    ballY = radius
                }else if(ballY + radius > surfaceHeight && vy > 0){
                    vy = -vy / 1.5f
                    ballY = surfaceHeight - radius

                }
                go = false
                drawCanvas()
        }

     }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        start()
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        if(start == true){
            surfaceWidth = width
            surfaceHeight = height
            ballX = (width / 2).toFloat()
            ballY = (height / 2).toFloat()

        }
    }

    fun start(){
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager  // SenserManagerの指定
        val accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)      // 加速度センサーの指定
        sensorManager.registerListener(this,accSensor,SensorManager.SENSOR_DELAY_GAME)  // センサーの更新頻度（3-30ms）

    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.unregisterListener(this)
    }

    private fun drawCanvas(){
        val canvas = surfaceView.holder.lockCanvas()

        canvas.drawColor(Color.rgb(237,244,252))

        canvas.drawCircle(gX,gY,gR, Paint().apply {
            color = Color.MAGENTA
            this.setStyle(Paint.Style.STROKE)
            this.strokeWidth = 5f
        })

        canvas.drawCircle(ballX,ballY,radius, Paint().apply {
            this.color = Color.BLACK
            color = Color.BLACK
        })

        surfaceView.holder.unlockCanvasAndPost(canvas)
    }

}
