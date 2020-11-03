package com.agrima.voicecommandtest.ui.activities

import android.Manifest
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.animation.Animation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import com.agrima.voicecommandtest.ui.viewmodels.MainActivityViewModel
import com.agrima.voicecommandtest.ui.models.Status.*
import com.area51.voicecommandtest.R
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), RecognitionListener {

    private var speechRecognizerIntent: Intent? = null
    private var speechRecognizer: SpeechRecognizer? = null
    private val REQUEST_AUDIO_PERMISSION_ = 10001

    private var blinkAnimator = ObjectAnimator()
    private val TAG = MainActivity::class.simpleName

//    private val REQUEST_SPEECH_RECOGNIZER = 3000

    private lateinit var mainViewModel: MainActivityViewModel
    private var isListening: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            checkPermission()
        }

        mainViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        mainViewModel.generateBoard()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)

        speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent?.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        speechRecognizerIntent?.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        buttonTrigger.setOnClickListener {
//            startSpeechRecognizer()
            if (!isListening) {
                isListening = true
                startListening()
            } else {
                buttonTrigger.text = getString(R.string.text_listen)
                isListening = false
                speechRecognizer?.stopListening()
                textViewAction.text = getString(R.string.text_action)
            }
        }

        mainViewModel.boardTiles
            .observe(this, {
                for ((index, item) in it.withIndex()) {
                    when (item.status) {
                        UNSELECTED -> {
                            tileGrid[index].setBackgroundResource(R.drawable.shape_selected_outline_default)
                        }
                        SELECTED -> {
                            tileGrid[index].setBackgroundResource(R.drawable.shape_selected_outline)
                        }
                        START -> {
                            tileGrid[index].setBackgroundResource(R.drawable.shape_selected_filled)
                        }
                        STOP -> {
                            blinkAnimator = ObjectAnimator.ofInt(
                                tileGrid[index],
                                "backgroundColor",
                                ContextCompat.getColor(this, R.color.yellow),
                                ContextCompat.getColor(this, R.color.green)
                            )
                            blinkAnimator.duration = 1000
                            blinkAnimator.setEvaluator(ArgbEvaluator())
                            blinkAnimator.repeatCount = Animation.INFINITE
                            blinkAnimator.start()
                        }
                        null -> TODO()
                    }
                }
            })

        speechRecognizer?.setRecognitionListener(this)

    }

    private fun startListening() {
        buttonTrigger.text = getString(R.string.text_stop_listening)
        speechRecognizer?.startListening(speechRecognizerIntent)
    }

    private fun checkPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            REQUEST_AUDIO_PERMISSION_
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_AUDIO_PERMISSION_ && grantResults.size > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) Toast.makeText(
                this,
                "Permission Granted",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /*private fun startSpeechRecognizer() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say the command")
        startActivityForResult(intent, REQUEST_SPEECH_RECOGNIZER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SPEECH_RECOGNIZER) {
            if (resultCode == RESULT_OK) {
                val results: ArrayList<String>? =
                    data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val result = results!![0].toLowerCase(Locale.getDefault())
                Log.i(TAG, "onActivityResult: $result")
                execute(result)
            }
        }
    }*/

    private fun execute(result: String) {
        when (result) {
            "start" -> {
                if (blinkAnimator.isRunning)
                    blinkAnimator.end()
                mainViewModel.setGreen()
            }
            "play" -> {
                if (blinkAnimator.isRunning)
                    blinkAnimator.end()
                mainViewModel.setGreen()
            }
            "restart" -> {
                if (blinkAnimator.isRunning) {
                    blinkAnimator.end()
                    mainViewModel.setGreen()
                }
            }
            "stop" -> {
                mainViewModel.blink()
            }
            "pause" -> {
                mainViewModel.blink()
            }
            "cancel" -> {
                mainViewModel.generateBoard()
            }
            "up" -> {
                if (blinkAnimator.isRunning)
                    blinkAnimator.end()
                navigateUp()
            }
            "aap" -> {
                if (blinkAnimator.isRunning)
                    blinkAnimator.end()
                navigateUp()
            }
            "down" -> {
                if (blinkAnimator.isRunning)
                    blinkAnimator.end()
                navigateDown()
            }
            "left" -> {
                if (blinkAnimator.isRunning)
                    blinkAnimator.end()
                navigateLeft()
            }
            "right" -> {
                if (blinkAnimator.isRunning)
                    blinkAnimator.end()
                navigateRight()
            }
        }
    }

    private fun navigateRight() {
        Log.i(TAG, "navigateRight: ")
        mainViewModel.navigateRight()
    }

    private fun navigateLeft() {
        Log.i(TAG, "navigateLeft: ")
        mainViewModel.navigateLeft()
    }

    private fun navigateDown() {
        Log.i(TAG, "navigateDown: ")
        mainViewModel.navigateDown()
    }

    private fun navigateUp() {
        Log.i(TAG, "navigateUp: ")
        mainViewModel.navigateUp()
    }

    override fun onReadyForSpeech(params: Bundle?) {

    }

    override fun onBeginningOfSpeech() {
    }

    override fun onRmsChanged(rmsdB: Float) {
    }

    override fun onBufferReceived(buffer: ByteArray?) {
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer?.stopListening()
    }

    override fun onEndOfSpeech() {
        if (isListening)
            startListening()
        else
            buttonTrigger.text = getString(R.string.text_listen)
    }

    override fun onError(error: Int) {
        if (isListening)
            startListening()
        else
            buttonTrigger.text = getString(R.string.text_listen)
    }

    override fun onResults(results: Bundle?) {
        if (isListening) {
            val data = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            Log.i(TAG, "onResults: " + data?.get(0))
            data?.get(0)?.let {
                textViewAction.text = it
                execute(it)
            }
            startListening()
        } else
            buttonTrigger.text = getString(R.string.text_listen)
    }

    override fun onPartialResults(partialResults: Bundle?) {
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
    }

}