package com.hritik.musik.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;
import com.hritik.musik.R;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {
    ImageButton play,next,prev,nff,nfr;
    TextView name,start,stop;
    SeekBar seekMusic;
    BarVisualizer visualizer;
    ImageView musicImg;

    String sName,endTime;
    public static final  String EXTRA_NAME="song_name";
    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> mySongs;

    Thread updateSeekBar;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onDestroy() {
        if(visualizer!=null){
            visualizer.release();
        }
        super.onDestroy();
    }

    private void initializeConfig(){
        Uri uri = FileProvider.getUriForFile(this, "com.hritik.musik.fileprovider",mySongs.get(position));
        sName=mySongs.get(position).getName();
        name.setText(sName);

        mediaPlayer= MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();
        updateSeekBar = new Thread(){

            @Override
            public void run() {
                super.run();
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;

                while (currentPosition<totalDuration){
                    try {
                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekMusic.setProgress(currentPosition);
                    }
                    catch (InterruptedException | IllegalStateException e){
                        e.printStackTrace();
                    }
                }
            }
        };
        final Handler handler = new Handler(Looper.myLooper());
        final int delay = 1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime = createTime(mediaPlayer.getCurrentPosition());
                start.setText(currentTime);
                handler.postDelayed(this,delay);

            }
        },delay);

        endTime = createTime(mediaPlayer.getDuration());
        stop.setText(endTime);
        int audioSessionID= mediaPlayer.getAudioSessionId();
        if(audioSessionID!=0){
            visualizer.setAudioSessionId(audioSessionID);
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                next.performClick();
                System.out.println("NEXXXXXXTTTT");
            }
        });

        seekMusic.setMax(mediaPlayer.getDuration());
        updateSeekBar.start();
        seekMusic.getProgressDrawable().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.MULTIPLY);
        seekMusic.getThumb().setColorFilter(getResources().getColor(R.color.red),PorterDuff.Mode.SRC_IN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        play=findViewById(R.id.playButton);
        next=findViewById(R.id.nextButton);
        prev=findViewById(R.id.revButton);
        nff=findViewById(R.id.fastForwardButton);
        nfr=findViewById(R.id.fastRewindButton);

        musicImg=findViewById(R.id.musicImage);

        name= findViewById(R.id.txtsn);
        start=findViewById(R.id.txtStart);
        stop=findViewById(R.id.txtStop);

        seekMusic=findViewById(R.id.seekBar);
        visualizer=findViewById(R.id.blast);

        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mySongs= (ArrayList) bundle.getParcelableArrayList("songs");
        //String songName = intent.getStringExtra("songName");
        position=bundle.getInt("pos",-1);
        name.setSelected(true);

        initializeConfig();


        seekMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });




        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying())
                {
                    play.setImageResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                }
                else{
                    play.setImageResource(R.drawable.ic_pause);
                    mediaPlayer.start();

                }
            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position=(position+1)%mySongs.size();
                startAnimation(musicImg,360);
                initializeConfig();

            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position=((position-1)<0?(mySongs.size()-1):position-1);
                startAnimation(musicImg,-360);
                initializeConfig();

            }
        });

        nff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
                }
            }
        });
        nfr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
                }
            }
        });


    }

    public void startAnimation(View view,int values){
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation",values );
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }

    public String createTime(int duration){
       String time="";
       int min=duration/1000/60;
       int sec=duration/1000%60;
       time+=min+":";
       if (sec<10){
           time+="0";
       }
       time+=sec;
       return time;
    }
}