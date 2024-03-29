package com.example.Swapp.call;

import static android.view.View.GONE;

import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.content.res.AppCompatResources;

import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.LocalAudioListener;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import Swapp.R;

public class CallScreenActivity extends BaseActivity {

    static final String TAG = CallScreenActivity.class.getSimpleName();

    private AudioPlayer mAudioPlayer;
    private Timer mTimer;
    private UpdateCallDurationTask mDurationTask;

    private String mCallId;

    private TextView mCallDuration;
    private TextView mCallState;
    private TextView mCallerName;

    private ImageView endCallButton;
    private ImageView speakerButton;
    private ImageView muteButton;

    private class UpdateCallDurationTask extends TimerTask {

        @Override
        public void run() {
            CallScreenActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateCallDuration();
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.callscreen);

        mAudioPlayer = new AudioPlayer(this);
        mCallDuration = findViewById(R.id.callDuration);
        mCallerName = findViewById(R.id.remoteUser);
        mCallState = findViewById(R.id.callState);
        endCallButton = findViewById(R.id.hangupButton);
        speakerButton = findViewById(R.id.speakerButton);
        muteButton = findViewById(R.id.muteButton);

        speakerButton.setVisibility(GONE);
        muteButton.setVisibility(GONE);

        endCallButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                endCall();
            }
        });
        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);
    }

    @Override
    public void onServiceConnected() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.addCallListener(new SinchCallListener());
            mCallerName.setText(getIntent().getStringExtra("userName"));
            mCallState.setText(call.getState().toString());

        } else {
            Log.e(TAG, "Started with invalid callId, aborting.");
            finish();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mDurationTask.cancel();
        mTimer.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
        mTimer = new Timer();
        mDurationTask = new UpdateCallDurationTask();
        mTimer.schedule(mDurationTask, 0, 500);
    }

    @Override
    public void onBackPressed() {
        // User should exit activity by ending call, not by going back.
    }

    private void endCall() {
        mAudioPlayer.stopProgressTone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
        }
        finish();
    }

    private String formatTimespan(int totalSeconds) {
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    private void updateCallDuration() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            mCallDuration.setText(formatTimespan(call.getDetails().getDuration()));
        }
    }

    private class SinchCallListener implements CallListener {

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(TAG, "Call ended. Reason: " + cause.toString());
            mAudioPlayer.stopProgressTone();
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            endCall();
        }

        @Override
        public void onCallEstablished(Call call) {
            Log.d(TAG, "Call established");
            mAudioPlayer.stopProgressTone();
            mCallState.setText(call.getState().toString());
            muteButton.setVisibility(View.VISIBLE);
            speakerButton.setVisibility(View.VISIBLE);
            mCallDuration.setVisibility(View.VISIBLE);

            AudioController audioController = getSinchServiceInterface().getAudioController();

            audioController.disableSpeaker();
            muteButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (audioController.isMute()) {
                        muteButton.setBackground(AppCompatResources.getDrawable(CallScreenActivity.this, R.drawable.white_circle_button));
                        muteButton.setImageDrawable(AppCompatResources.getDrawable(CallScreenActivity.this, R.drawable.unmute_icon));
                        muteButton.setImageTintList(AppCompatResources.getColorStateList(CallScreenActivity.this, R.color.primary_black));
                        audioController.unmute();

                    } else {
                        muteButton.setBackground(AppCompatResources.getDrawable(CallScreenActivity.this, R.drawable.black_circle_button));
                        muteButton.setImageDrawable(AppCompatResources.getDrawable(CallScreenActivity.this, R.drawable.mute_icon));
                        muteButton.setImageTintList(AppCompatResources.getColorStateList(CallScreenActivity.this, R.color.white));
                        audioController.mute();

                    }
                }
            });

            speakerButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (audioController.isSpeakerOn()) {
                        speakerButton.setBackground(AppCompatResources.getDrawable(CallScreenActivity.this, R.drawable.white_circle_button));
                        speakerButton.setImageDrawable(AppCompatResources.getDrawable(CallScreenActivity.this, R.drawable.speaker_low_icon));
                        speakerButton.setImageTintList(AppCompatResources.getColorStateList(CallScreenActivity.this, R.color.primary_black));
                        audioController.disableSpeaker();

                    } else {
                        speakerButton.setBackground(AppCompatResources.getDrawable(CallScreenActivity.this, R.drawable.black_circle_button));
                        speakerButton.setImageDrawable(AppCompatResources.getDrawable(CallScreenActivity.this, R.drawable.speaker_icon));
                        speakerButton.setImageTintList(AppCompatResources.getColorStateList(CallScreenActivity.this, R.color.white));
                        audioController.enableSpeaker();

                    }
                }
            });

            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            audioController.disableSpeaker();
        }

        @Override
        public void onCallProgressing(Call call) {
            Log.d(TAG, "Call progressing");
            mCallDuration.setVisibility(GONE);
            mAudioPlayer.playProgressTone();
        }

    }
}
