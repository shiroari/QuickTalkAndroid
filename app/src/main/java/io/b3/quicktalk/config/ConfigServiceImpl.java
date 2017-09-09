package io.b3.quicktalk.config;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Observable;

import io.b3.quicktalk.AppContext;

/**
 * @author Stas Sukhanov
 * @since 26.07.2016
 */
public class ConfigServiceImpl extends Observable implements ConfigService {

    private boolean paused = true;

    private SharedPreferences settings;

    public ConfigServiceImpl() {
        settings = PreferenceManager.getDefaultSharedPreferences(AppContext.context());
        settings.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                update();
            }
        });
        initDefaults();
    }

    private void initDefaults() {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("autoplay", isEnabledAutoPlay());
        editor.putBoolean("sound", isEnabledTts());
        editor.putBoolean("disable_auto_lock", isDisabledAutoLock());
        editor.putInt("speed", getSpeedRate());
        editor.putString("after_action", getAfterActionType().name());
        editor.putString("front_voice", getVoice1());
        editor.putString("back_voice", getVoice2());
        editor.commit();
    }

    @Override
    public int getSpeedRate() {
        return settings.getInt("speed", 100);
    }

    @Override
    public ActionType getAfterActionType() {
        String name = settings.getString("after_action", ActionType.Random.name());
        return ActionType.valueOf(name);
    }

    @Override
    public String getVoice1() {
        return settings.getString("front_voice", "ru");
    }

    @Override
    public String getVoice2() {
        return settings.getString("back_voice", "en");
    }

    @Override
    public boolean isDisabledAutoLock() {
        return settings.getBoolean("disable_auto_lock", true);
    }

    @Override
    public boolean isEnabledAutoPlay() {
        return settings.getBoolean("autoplay", true);
    }

    @Override
    public boolean isEnabledTts() {
        return settings.getBoolean("sound", true);
    }

    @Override
    public void setPaused(boolean paused) {
        this.paused = paused;
        update();
    }

    @Override
    public boolean isPaused() {
        return paused;
    }

    @Override
    public void resetAllSettings() {
        update();
    }

    private void update() {
        setChanged();
        notifyObservers();
    }
}
