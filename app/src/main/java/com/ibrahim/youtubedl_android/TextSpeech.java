package com.ibrahim.youtubedl_android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.util.Locale;

public class TextSpeech extends AppCompatActivity {

    private static final int SUCCESS = 0;
    private TextToSpeech mTTS;
    private EditText mEditText, mFileName;
    private SeekBar mSeekBarPitch;
    private SeekBar mSeekBarSpeed;
    private Button mButtonSpeak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_to_speech);

        mButtonSpeak = findViewById(R.id.button_speak);

        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.ENGLISH);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        mButtonSpeak.setEnabled(true);
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }

        });



        mEditText = findViewById(R.id.edit_text);
        mFileName = findViewById(R.id.file_name);
        mSeekBarPitch = findViewById(R.id.seek_bar_pitch);
        mSeekBarSpeed = findViewById(R.id.seek_bar_speed);

        mButtonSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convert();
            }
        });
    }

    private void convert() {
        String text = mEditText.getText().toString();
        String filename = mFileName.getText().toString();
        float pitch = (float) mSeekBarPitch.getProgress() / 50;
        if (pitch < 0.1) pitch = 0.1f;
        float speed = (float) mSeekBarSpeed.getProgress() / 50;
        if (speed < 0.1) speed = 0.1f;

        mTTS.setPitch(pitch);
        mTTS.setSpeechRate(speed);

//        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        if (!text.equals("") && !filename.equals("")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                String state = Environment.getExternalStorageState();
                boolean mExternalStorageWriteable = false;
                boolean mExternalStorageAvailable = false;
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    // Can read and write the media
                    mExternalStorageAvailable = mExternalStorageWriteable = true;

                } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                    // Can only read the media
                    mExternalStorageAvailable = true;
                    mExternalStorageWriteable = false;
                } else {
                    // Can't read or write
                    mExternalStorageAvailable = mExternalStorageWriteable = false;
                }
                File root = android.os.Environment.getExternalStorageDirectory();
                File dir = new File(root.getAbsolutePath() + "/TextToSpeech");
                dir.mkdirs();
                File file = new File(dir, filename + ".mp3");

                int i = mTTS.synthesizeToFile((CharSequence) text, null, file,
                        "tts");


                mTTS.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String s) {

                        Toast.makeText(TextSpeech.this, "Text Conversion started", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onDone(String s) {
                        mEditText.setText("");
                        mFileName.setText("");
                        Toast.makeText(TextSpeech.this, "Text Converted Successfully", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(String s) {

                    }
                });


//                if (i == SUCCESS) {
//                    Toast.makeText(this, "Text Converted Successfully", Toast.LENGTH_SHORT).show();
//                    mEditText.setText("");
//                    mFileName.setText("");
//                    mTTS.shutdown();
//                } else {
//                    Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
//                    i = 1;
//                }
//            mTTS.speak(text,TextToSpeech.QUEUE_FLUSH,null,null);
            }
        }else Toast.makeText(this, "Please enter File name and text", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }

        super.onDestroy();
    }


}