package io.b3.quicktalk.engine

import io.b3.quicktalk.config.ConfigService
import io.b3.quicktalk.internal.Speaker
import io.b3.quicktalk.internal.SpeakerListener
import io.b3.quicktalk.internal.Timer
import io.b3.quicktalk.internal.TimerListener
import javax.inject.Inject

const val SELF_CHECK_TIME = 3.0

interface Tutor {
    fun start()
    fun start(index: Int)
    fun stop()
    fun resume()
    fun restart()
    fun startRandom()
    fun save()
    fun nextStep()
    fun previousStep()
    fun replayStep()
}

class TutorImpl @Inject
constructor(private val config: ConfigService,
            private val catalog: CardSetCatalog,
            private val manager: CardManager,
            private val speaker: Speaker,
            private val timer: Timer) : Tutor {

    private var timerStopped: Boolean = false

    init {
        bindListeners()
    }

    private fun bindListeners() {
        this.timer.setListener(object : TimerListener {
            override fun timeout() {
                this@TutorImpl.timeout()
            }
        })

        this.speaker.setListener(object : SpeakerListener {
            override fun speakerStarted() {
                this@TutorImpl.speakerStarted()
            }

            override fun speakerStopped() {
                this@TutorImpl.speakerStopped()
            }
        })
    }

    override fun save() {
        // TODO
        //saveState(manager.getCardSet().getId(), manager.getCurrent());
    }

    override fun start() {

        // TODO: load state
        val cardSetId: String? = null
        val cardIndex = -1

        if (cardSetId != null) {
            val cardSet = catalog.getCardSet(cardSetId)
            if (cardSet != null) {
                if (cardIndex != -1) {
                    manager.resume(cardSet, cardIndex)
                } else {
                    manager.start(cardSet)
                }
                playCard()
                return
            }
        }

        random()
    }

    private fun random() {
        if (catalog.count == 0) {
            return
        }
        start((Math.random() * (catalog.count - 1)).toInt())
    }

    override fun start(index: Int) {
        manager.start(catalog.getCardSet(index))
        playCard()
    }

    override fun stop() {
        if (config.isPaused) {
            return
        }
        stopTimer()
        config.isPaused = true
        playCard()
    }

    override fun resume() {
        config.isPaused = false
        playCard()
    }

    override fun restart() {
        stopTimer()
        config.isPaused = false
        manager.restart()
        playCard()
    }

    override fun startRandom() {
        stopTimer()
        config.isPaused = false
        random()
    }

    override fun nextStep() {
        stopTimer()
        config.isPaused = false
        if (!manager.nextStep()) {
            when (config.afterActionType) {
                ConfigService.ActionType.Random -> startRandom()
                ConfigService.ActionType.Repeat -> restart()
                ConfigService.ActionType.Stop -> config.isPaused = true
            }
            return
        }
        playCard()
    }

    override fun previousStep() {
        stopTimer()
        config.isPaused = true
        manager.previousStep()
        playCard()
    }

    override fun replayStep() {
        stopTimer()
        config.isPaused = false
        if (speaker.isSpeaking) {
            config.isPaused = true
        }
        playCard()
    }

    private fun playCard() {
        when (manager.state) {
            CardManager.CardState.Title, CardManager.CardState.Back -> textToSpeech(manager.text, ConfigService.VoiceType.Back)
            CardManager.CardState.Front -> textToSpeech(manager.text, ConfigService.VoiceType.Front)
        }
    }

    private fun textToSpeech(text: String, voice: ConfigService.VoiceType) {
        if (config.isPaused) {
            if (config.isEnabledTts) {
                speaker.stop()
            }
            return
        }
        timerStopped = false
        if (config.isEnabledTts) {
            speaker.play(text, if (voice == ConfigService.VoiceType.Front) config.voice1 else config.voice2)
        } else {
            setTimer(evalReadTime() + checkDelay)
        }
    }

    private fun setTimer(delay: Double?) {
        if (config.isEnabledAutoPlay) {
            timer.timeout(delay!! * 0.01 * config.speedRate.toDouble())
        }
    }

    private fun stopTimer() {
        timerStopped = true
        timer.invalidate()
    }

    private fun evalReadTime(): Double = 0.1 * manager.text.length

    private fun evalSpeechTime(): Double = 0.2 * manager.text.length

    private val checkDelay: Double
        get() = if (manager.state === CardManager.CardState.Front) evalSpeechTime() else SELF_CHECK_TIME

    private fun timeout() {
        nextStep()
    }

    private fun speakerStarted() {
        timer.invalidate()
    }

    private fun speakerStopped() {
        if (!timerStopped) {
            setTimer(checkDelay)
        }
    }

}