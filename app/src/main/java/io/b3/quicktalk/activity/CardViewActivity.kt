package io.b3.quicktalk.activity

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.view.GestureDetectorCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.util.Log
import android.view.GestureDetector
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import io.b3.quicktalk.AppContext
import io.b3.quicktalk.R
import io.b3.quicktalk.config.ConfigService
import io.b3.quicktalk.engine.CardManager
import io.b3.quicktalk.engine.Tutor
import java.util.*
import javax.inject.Inject

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * TODO:
 * - Butterknife
 */
class CardViewActivity : AppCompatActivity(), Observer {

    @Inject
    lateinit var tutor: Tutor

    @Inject
    lateinit var manager: CardManager

    @Inject
    lateinit var config: ConfigService

    private var firstStart = true
    private var hold: Boolean = false

    private var mContentView: View? = null

    private var output: TextView? = null
    private var status: TextView? = null
    private var header: TextView? = null
    private var playIcon: ImageView? = null
    private var soundIcon: ImageView? = null

    private var mDetector: GestureDetectorCompat? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        delegate.requestWindowFeature(AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR_OVERLAY)
        delegate.requestWindowFeature(AppCompatDelegate.FEATURE_ACTION_MODE_OVERLAY)

        super.onCreate(savedInstanceState)

        AppContext.inject(this)

        config.addObserver(this)
        manager.addObserver(this)

        setContentView(R.layout.activity_card_view)

        mContentView = findViewById(R.id.content)

        output = findViewById(R.id.output) as TextView
        header = findViewById(R.id.header) as TextView
        status = findViewById(R.id.status) as TextView
        playIcon = findViewById(R.id.playIcon) as ImageView
        soundIcon = findViewById(R.id.soundIcon) as ImageView

        mDetector = GestureDetectorCompat(this, object : GestureDetector.SimpleOnGestureListener() {

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                replayStep()
                return true
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                Log.d("g", "confirm")
                return super.onSingleTapConfirmed(e)
            }

            override fun onLongPress(event: MotionEvent) {
                showInfo()
            }

            override fun onFling(startEvent: MotionEvent, endEvent: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                val dx = endEvent.x - startEvent.x
                val dy = endEvent.y - startEvent.y
                if (Math.abs(dx) > 50.0f && Math.abs(dx) > Math.abs(dy)) {
                    if (dx > 0) {
                        previousStep()
                    } else {
                        nextStep()
                    }
                } else if (dy < -50.0f && Math.abs(dx) < Math.abs(dy)) {
                    openMenu()
                }
                return true
            }

        })

        if (firstStart) {
            firstStart = false
            tutor.start()
        } else {
            tutor.resume()
        }

        enterFullScreen()
    }

    override fun onRestart() {
        super.onRestart()
        enterFullScreen()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            hideInfo()
        }
        this.mDetector?.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    private fun openMenu() {

        tutor.stop()

        val builder = AlertDialog.Builder(this)

        builder.setTitle("Menu")

        builder.setNegativeButton("Close") { _, _ -> enterFullScreen() }

        builder.setItems(MENU_ITEMS) { _, index -> dispatchMenuAction(index) }

        val dialog = builder.create()
        dialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        var index = 0
        for (item in MENU_ITEMS) {
            menu.add(0, 0, index++, item)
        }
        return true
    }

    override fun onOptionsMenuClosed(menu: Menu) {
        super.onOptionsMenuClosed(menu)
        Log.d("menu", "closed")
        enterFullScreen()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        dispatchMenuAction(item.order)
        return true
    }

    private fun enterFullScreen() {
        mContentView?.systemUiVisibility =
                View.SYSTEM_UI_FLAG_VISIBLE or
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }

    private fun dispatchMenuAction(index: Int) {
        when (index) {
            0 -> selectDialog()
            1 -> {
                randomDialog()
                enterFullScreen()
            }
            2 -> {
                restartDialog()
                enterFullScreen()
            }
            3 -> openSettings()
        }
    }

    private fun selectDialog() {
        tutor.stop()
        startActivity(Intent(this, SelectActivity::class.java))
    }

    private fun restartDialog() {
        tutor.restart()
    }

    private fun randomDialog() {
        tutor.startRandom()
    }

    private fun previousStep() {
        tutor.previousStep()
    }

    private fun nextStep() {
        tutor.nextStep()
    }

    private fun replayStep() {
        if (hold) {
            hold = false
        }
        if (config.isEnabledAutoPlay) {
            if (config.isPaused) {
                tutor.resume()
            } else {
                tutor.stop()
            }
        } else {
            tutor.replayStep()
        }
    }

    private fun openSettings() {
        tutor.stop()
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    private fun showInfo() {
        hold = true
        update()
    }

    private fun hideInfo() {
        hold = false
        update()
    }

    private fun updateView() {
        when (manager.state) {
            CardManager.CardState.Title, CardManager.CardState.Front -> {
                output?.setTextColor(Color.BLACK)
                output?.text = manager.text
            }
            CardManager.CardState.Back -> {
                output?.setTextColor(Color.argb(120, 0, 0, 0))
                output?.text = manager.text
            }
        }
    }

    private fun updateOverlay() {
        status?.text = String.format("%s/%s", manager.current + 1, manager.count)
        if (hold) {
            if (manager.state !== CardManager.CardState.Title) {
                header?.text = manager.title
            }
        } else {
            header?.text = ""
        }
    }

    private fun updateSettings() {
        playIcon?.setImageResource(
                if (config.isPaused)
                    R.drawable.paused
                else
                    R.drawable.play)
        playIcon?.visibility = if (config.isEnabledAutoPlay)
            ImageView.VISIBLE
        else
            ImageView.INVISIBLE
        soundIcon?.visibility = if (config.isEnabledTts)
            ImageView.VISIBLE
        else
            ImageView.INVISIBLE
    }

    override fun update(observable: Observable, o: Any?) {
        update()
    }

    private fun update() {
        supportActionBar?.title = manager.title
        disableAutoLock(config.isPaused
                && config.isEnabledAutoPlay
                && config.isDisabledAutoLock)
        updateSettings()
        updateOverlay()
        updateView()
    }

    fun disableAutoLock(disabled: Boolean) {
        // TODO: disable autolock
    }

    companion object {
        private val MENU_ITEMS = arrayOf("Select", "Random", "Restart", "Settings")
    }

}
