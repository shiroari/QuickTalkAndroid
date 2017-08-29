package io.b3.quicktalk.internal;

/**
 * @author shiroari
 * @since 6.08.2016
 */
public interface Speaker {
    boolean isSpeaking();
    void stop();
    void play(String text, String voice);
    void setListener(SpeakerListener listener);
}
