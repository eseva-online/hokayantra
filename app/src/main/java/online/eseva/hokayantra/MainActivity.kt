package online.eseva.hokayantra

import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.hardware.SensorManager
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.RotateAnimation


import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.media.AudioManager
import android.media.SoundPool


class MainActivity : AppCompatActivity(), SensorEventListener {
    private var mSensorManager: SensorManager? = null
    private var currentDegree = 0f

    // Directions
    val DIRECTION_UTTAR = "ઉત્તર"
    val DIRECTION_DAKSHIN = "દક્ષિણ"
    val DIRECTION_PURV = "પૂર્વ"
    val DIRECTION_PACHCHIM = "પશ્ચિમ"
    val DIRECTION_AGNI = "અગ્નિ"
    val DIRECTION_ISHAN = "ઈશાન"
    val DIRECTION_NAIRUTYA = "નૈઋત્ય"
    val DIRECTION_VAYAVYA = "વાયવ્ય"

    var backColors: MutableList<Int> = mutableListOf()
    var lastSelectedColor = 0;

    var sp: SoundPool? = null
    var tickSoundId = 0

    var lastDegreeOn = 0f
    private var timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager?

        setEventListeners()

        // load fake tick sounds
        sp = SoundPool(1, AudioManager.STREAM_MUSIC, 0)
        tickSoundId = sp!!.load(this, R.raw.tick, 1)

        backColors.add(Color.parseColor("#5639B0")) // purple
        backColors.add(Color.parseColor("#0015ff")) // blue
        backColors.add(Color.parseColor("#6E4B3F")) // brown

        backColors.add(Color.parseColor("#7700BF")) // purple 2
        backColors.add(Color.parseColor("#009411")) // green
        backColors.add(Color.parseColor("#45662C")) // green 2
        backColors.add(Color.parseColor("#009494")) // teal

        backColors.add(Color.parseColor("#750185")) // purple dark
        backColors.add(Color.parseColor("#000B8E")) // blue dark
        backColors.add(Color.parseColor("#8E0070")) // pink
        backColors.add(Color.parseColor("#C20000")) // red

    }

    /**
     * Set different themes on compass
     */
    private fun randomCompassTheme() {
        val random = Random()
        var randomPos = lastSelectedColor
        while(lastSelectedColor == randomPos) {
            Log.d("MainActivity.kt", "Random Pos: ${randomPos}")
            randomPos = random.nextInt(backColors.size)
        }

        val randomColor = backColors.get(randomPos)
        val whiteColor = Color.parseColor("#FFFFFF")

        val shape = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(whiteColor, randomColor))
        shape.gradientType = GradientDrawable.RADIAL_GRADIENT
        shape.shape = GradientDrawable.OVAL
        shape.gradientRadius = 250f

        // bangImage.setAnimScaleFactor(0)
        bangImage.setDotColors(intArrayOf(backColors[1], backColors[2], backColors[3], backColors[5]))
        bangImage.setCircleEndColor(randomColor)
        bangImage.setCircleStartColor(randomColor)
        bangImage.likeAnimation()

        yantraContentImage.setBackgroundDrawable(shape)
    }

    private fun setEventListeners() {

        info.setOnLongClickListener {
            mockSensor()
        }

        // clicking on needle will change the theme of compass
        yantraNeedleImage.setOnClickListener {
            randomCompassTheme()
        }

        aboutHokayantraWrapper.setOnClickListener {
            Global.showPopup(this, getString(R.string.title_about_hokayantra), getString(R.string.hokayantra_bg))
        }

        bottomSheetLayout.setOnProgressListener { progress: Float ->
            when (progress) {
                0f -> indicatorArrow.setImageResource(R.drawable.ic_keyboard_arrow)
                1f -> indicatorArrow.setImageResource(R.drawable.ic_keyboard_arrow_down)
            }
        }

        indicatorArrow.setOnClickListener {
            if (bottomSheetLayout.isExpended()) bottomSheetLayout.collapse()
            else bottomSheetLayout.expand()
        }

        infoArrow.setOnClickListener {
            Global.showPopup(this, getString(R.string.title_about_hokayantra), getString(R.string.hokayantra_bg))
        }

        // more apps
        moreAppsWrapper.setOnClickListener {
            Global.openDeveloperPageInStore(this)
        }

    }

    /**
     * Faking the rotation of the direction randomly so one can debug on devices without sensor
     */
    private fun mockSensor(): Boolean {
        val delay = 1000L
        var degree = 0f

        val handler: Handler? = Handler()

        handler!!.postDelayed(object : Runnable {
            override fun run() {
                val r: Random = Random()

                if (degree >= 360) degree = 0f
                else {
                    val isMinus = r.nextInt(1)
                    if(isMinus == 0) degree += r.nextInt(10)
                    else degree += -r.nextInt(10)
                }
                onDegreeChanged(degree)

                handler.postDelayed(this, delay)
            }
        }, delay)

        return false;
    }

    private fun onDegreeChanged(degree: Float) {
        if(lastDegreeOn == degree) return
        onDegreeChangedReally(degree)
    }

    private fun onDegreeChangedReally(degree: Float) {
        Log.d("MainActivity.kt", "Degree: ${degree}")
        info2.text = "${degree.toInt()}º"
        info.text = getDirectionFromDegree(degree)

        var ra: RotateAnimation? = null
        var degreeToRotate = degree
        // if(Math.abs(currentDegree - degree) >= 180) {
        //      degreeToRotate = currentDegree + degree
        // }
        ra = RotateAnimation(
                currentDegree,
                -degreeToRotate,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f)

        ra.duration = 210
        ra.fillBefore = true
        ra.fillAfter = true

        yantraContentImage.startAnimation(ra)
        currentDegree = -degree
        lastDegreeOn = degree

        sp!!.play(tickSoundId, 0.2f, 0.2f, 0, 0, 1f);
    }

    private fun getDirectionFromDegree(degree: Float): String {
        return when (degree) {
            in 0..22, in 338..360 -> DIRECTION_UTTAR
            in 23..67 -> DIRECTION_ISHAN
            in 68..122 -> DIRECTION_PURV
            in 123..157 -> DIRECTION_AGNI
            in 158..202 -> DIRECTION_DAKSHIN
            in 203..247 -> DIRECTION_NAIRUTYA
            in 248..292 -> DIRECTION_PACHCHIM
            in 293..337 -> DIRECTION_VAYAVYA
            else -> ""
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val degree = Math.round(event!!.values[0]).toFloat()
        onDegreeChanged(degree)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    override fun onResume() {
        super.onResume()

        mSensorManager!!.registerListener(this, mSensorManager!!.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    override fun onPause() {
        super.onPause()

        mSensorManager!!.unregisterListener(this);
    }
}