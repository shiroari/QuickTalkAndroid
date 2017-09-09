package io.b3.quicktalk.config

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import java.util.*

interface ConfigService {

    enum class ActionType {
        Stop, Random, Repeat
    }

    enum class VoiceType {
        Front, Back
    }

    val speedRate: Int
    val afterActionType: ActionType
    val voice1: String
    val voice2: String
    val isDisabledAutoLock: Boolean
    val isEnabledAutoPlay: Boolean
    val isEnabledTts: Boolean

    var isPaused: Boolean

    fun resetAllSettings()
    fun addObserver(observer: Observer)
    fun deleteObserver(observer: Observer)

}

class ConfigServiceImpl(context: Context) : Observable(), ConfigService {

    private val settings: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    init {
        settings.registerOnSharedPreferenceChangeListener { _, _ -> update() }
        initDefaults()
    }

    private fun initDefaults() {
        val editor = settings.edit()
        editor.putBoolean("autoplay", isEnabledAutoPlay)
        editor.putBoolean("sound", isEnabledTts)
        editor.putBoolean("disable_auto_lock", isDisabledAutoLock)
        editor.putInt("speed", speedRate)
        editor.putString("after_action", afterActionType.name)
        editor.putString("front_voice", voice1)
        editor.putString("back_voice", voice2)
        editor.apply()
    }

    override val speedRate: Int
        get() = settings.getInt("speed", 100)

    override val afterActionType: ConfigService.ActionType
        get() {
            val name = settings.getString("after_action", ConfigService.ActionType.Random.name)
            return ConfigService.ActionType.valueOf(name)
        }

    override val voice1: String
        get() = settings.getString("front_voice", "ru")

    override val voice2: String
        get() = settings.getString("back_voice", "en")

    override val isDisabledAutoLock: Boolean
        get() = settings.getBoolean("disable_auto_lock", true)

    override val isEnabledAutoPlay: Boolean
        get() = settings.getBoolean("autoplay", true)

    override val isEnabledTts: Boolean
        get() = settings.getBoolean("sound", true)

    override var isPaused: Boolean = true
        set(value) {
            field = value
            update()
        }

    override fun resetAllSettings() {
        update()
    }

    private fun update() {
        setChanged()
        notifyObservers()
    }
}