package io.b3.quicktalk.internal;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;

/**
 * @author shiroari
 * @since 6.08.2016
 */
public class SpeakerImpl extends UtteranceProgressListener implements Speaker, TextToSpeech.OnInitListener {

    private TextToSpeech sync;
    private SpeakerListener listener;

    public SpeakerImpl(Context context) {
        this.sync = new TextToSpeech(context, this);
    }

    public void setListener(SpeakerListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean isSpeaking() {
        return sync.isSpeaking();
    }

    @Override
    public void stop() {
        sync.stop();
    }

    @Override
    public void play(String text, String voice) {
        sync.setLanguage(new Locale(voice));
        HashMap<String, String> params = new HashMap<>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, text);
        if (TextToSpeech.SUCCESS != sync.speak(text, TextToSpeech.QUEUE_FLUSH, params)) {
            Log.d("voice", "Can not add to queue - " + text);
        }
    }

    @Override
    public void onInit(int status) {
        if (status != TextToSpeech.SUCCESS) {
            Log.d("voice", "error = " + status);
        }
        sync.setOnUtteranceProgressListener(this);
    }

    @Override
    public void onStart(String text) {
        Log.d("voice", "Start - " + text);
        listener.speakerStarted();
    }

    @Override
    public void onDone(String text) {
        Log.d("voice", "Done - " + text);
        listener.speakerStopped();
    }

    @Override
    public void onError(String text) {
        Log.d("voice", " - " + text);
    }
}
