package io.b3.quicktalk.engine;

/**
 * @author shiroari
 * @since 26.07.2016
 */
public interface Tutor {
    void start();
    void start(int index);
    void stop();
    void resume();
    void restart();
    void startRandom();
    void save();
    void nextStep();
    void previousStep();
    void replayStep();
}
