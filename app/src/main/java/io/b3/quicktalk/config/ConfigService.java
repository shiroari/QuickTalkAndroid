package io.b3.quicktalk.config;

import java.util.Observer;

/**
 * @author Stas Sukhanov
 * @since 26.07.2016
 */
public interface ConfigService {

    enum ActionType {
        Stop, Random, Repeat
    }

    enum VoiceType {
        Front, Back
    }

    int getSpeedRate();
    ActionType getAfterActionType();
    String getVoice1();
    String getVoice2();
    boolean isDisabledAutoLock();
    boolean isEnabledAutoPlay();
    boolean isEnabledTts();
    void resetAllSettings();

    boolean isPaused();
    void setPaused(boolean paused);

    void addObserver(Observer observer);
    void deleteObserver(Observer observer);

}
