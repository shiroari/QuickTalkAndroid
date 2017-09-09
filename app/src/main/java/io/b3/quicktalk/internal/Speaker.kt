package io.b3.quicktalk.internal

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.*

interface SpeakerListener {
    fun speakerStarted()
    fun speakerStopped()
}

interface Speaker {
    val isSpeaking: Boolean
    fun stop()
    fun play(text: String, voice: String)
    fun setListener(listener: SpeakerListener)
}

class SpeakerImpl(context: Context) : UtteranceProgressListener(), Speaker, TextToSpeech.OnInitListener {

    private val sync: TextToSpeech = TextToSpeech(context, this)
    private var listener: SpeakerListener? = null

    override fun setListener(listener: SpeakerListener) {
        this.listener = listener
    }

    override val isSpeaking: Boolean
        get() = sync.isSpeaking

    override fun stop() {
        sync.stop()
    }

    override fun play(text: String, voice: String) {
        sync.language = Locale(voice)
        val params = HashMap<String, String>()
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, text)
        // TODO: replace
        if (TextToSpeech.SUCCESS != sync.speak(text, TextToSpeech.QUEUE_FLUSH, params)) {
            Log.d("voice", "Can not add to queue - " + text)
        }
    }

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) {
            // TODO: handle
            Log.d("voice", "error = " + status)
        }
        sync.setOnUtteranceProgressListener(this)
    }

    override fun onStart(text: String) {
        Log.d("voice", "Start - " + text)
        listener?.speakerStarted()
    }

    override fun onDone(text: String) {
        Log.d("voice", "Done - " + text)
        listener?.speakerStopped()
    }

    // TODO: replace
    override fun onError(text: String) {
        Log.d("voice", " - " + text)
    }
}