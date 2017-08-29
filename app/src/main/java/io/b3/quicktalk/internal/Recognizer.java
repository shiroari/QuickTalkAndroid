package io.b3.quicktalk.internal;

/**
 * @author shiroari
 * @since 6.08.2016
 */
public interface Recognizer {
    void stop();
    void listen(String text);
}
