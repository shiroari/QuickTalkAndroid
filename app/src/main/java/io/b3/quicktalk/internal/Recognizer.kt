package io.b3.quicktalk.internal

interface Recognizer {
    fun stop()
    fun listen(text: String)
}

class RecognizerImpl : Recognizer {

    override fun stop() {

    }

    override fun listen(text: String) {
        //SpeechRecognizer
    }
}

