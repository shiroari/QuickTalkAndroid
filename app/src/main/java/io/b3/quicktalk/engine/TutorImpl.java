package io.b3.quicktalk.engine;

import javax.inject.Inject;

import io.b3.quicktalk.config.ConfigService;
import io.b3.quicktalk.config.ConfigService.VoiceType;
import io.b3.quicktalk.internal.Speaker;
import io.b3.quicktalk.internal.SpeakerListener;
import io.b3.quicktalk.internal.Timer;
import io.b3.quicktalk.internal.TimerListener;
import io.b3.quicktalk.model.CardSet;

import static io.b3.quicktalk.engine.CardManager.CardState.Front;

/**
 * @author Stas Sukhanov
 * @since 26.07.2016
 */
public class TutorImpl implements Tutor {

    private ConfigService config;
    private CardSetCatalog catalog;
    private CardManager manager;
    private Speaker speaker;
    private Timer timer;

    private boolean timerStopped;

    @Inject
    public TutorImpl(ConfigService configService,
                     CardSetCatalog catalog,
                     CardManager manager,
                     Speaker speaker,
                     Timer timer) {

        this.config = configService;
        this.catalog = catalog;
        this.manager = manager;
        this.speaker = speaker;
        this.timer = timer;
        bindListeners();
    }

    private void bindListeners() {
        this.timer.setListener(new TimerListener() {
            @Override
            public void timeout() {
                TutorImpl.this.timeout();
            }
        });
        this.speaker.setListener(new SpeakerListener() {
            @Override
            public void speakerStarted() {
                TutorImpl.this.speakerStarted();
            }
            @Override
            public void speakerStopped() {
                TutorImpl.this.speakerStopped();
            }
        });
    }

    @Override
    public void save() {
        // TODO
        //saveState(manager.getCardSet().getId(), manager.getCurrent());
    }

    @Override
    public void start() {

        String cardSetId = null;
        int cardIndex = -1;

        if (cardSetId != null) {
            CardSet cardSet = catalog.getCardSet(cardSetId);
            if (cardIndex != -1) {
                manager.resume(cardSet, cardIndex);
            } else {
                manager.start(cardSet);
            }
            playCard();
            return;
        }

        random();
    }

    private void random() {
        if (catalog.getCount() == 0) {
            return;
        }
        start((int) (Math.random() * (catalog.getCount() - 1)));
    }

    @Override
    public void start(int index) {
        manager.start(catalog.getCardSet(index));
        playCard();
    }

    @Override
    public void stop() {
        if (config.isPaused()) {
            return;
        }
        stopTimer();
        config.setPaused(true);
        playCard();
    }

    @Override
    public void resume() {
        config.setPaused(false);
        playCard();
    }

    @Override
    public void restart() {
        stopTimer();
        config.setPaused(false);
        manager.restart();
        playCard();
    }

    @Override
    public void startRandom() {
        stopTimer();
        config.setPaused(false);
        random();
    }

    @Override
    public void nextStep() {
        stopTimer();
        config.setPaused(false);
        if (!manager.nextStep()) {
            switch (config.getAfterActionType()) {
                case Random:
                    startRandom();
                    return;
                case Repeat:
                    restart();
                    return;
                case Stop:
                    config.setPaused(true);
            }
        }
        playCard();
    }

    @Override
    public void previousStep() {
        stopTimer();
        config.setPaused(true);
        manager.previousStep();
        playCard();
    }

    @Override
    public void replayStep() {
        stopTimer();
        config.setPaused(false);
        if (speaker.isSpeaking()) {
            config.setPaused(true);
        }
        playCard();
    }

    private void playCard() {
        switch (manager.getState()) {
            case Title:
            case Back:
                textToSpeech(manager.getText(), VoiceType.Back);
                break;
            case Front:
                textToSpeech(manager.getText(), VoiceType.Front);
                break;
        }
    }

    private void textToSpeech(String text, VoiceType voice) {
        if (config.isPaused()) {
            if (config.isEnabledTts()) {
                speaker.stop();
            }
            return;
        }
        timerStopped = false;
        if (config.isEnabledTts()) {
            speaker.play(text, (voice == VoiceType.Front) ? config.getVoice1() : config.getVoice2());
        } else {
            setTimer(evalReadTime() + getCheckDelay());
        }
    }

    private void setTimer(Double delay) {
        if (config.isEnabledAutoPlay()) {
            timer.timeout(delay * 0.01 * config.getSpeedRate());
        }
    }

    private void stopTimer() {
        timerStopped = true;
        timer.invalidate();
    }

    private Double evalReadTime() {
        return 0.1 * manager.getText().length();
    }

    private Double evalSpeechTime() {
        return 0.2 * manager.getText().length();
    }

    private Double getCheckDelay() {
        return manager.getState() == Front ? evalSpeechTime() : 3;
    }

    private void timeout() {
        nextStep();
    }

    private void speakerStarted() {
        timer.invalidate();
    }

    private void speakerStopped() {
        if (!timerStopped) {
            setTimer(getCheckDelay());
        }
    }

}
