package zc.tech.video.zcvideoapplication;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.IOException;

/**
 * Created by Zack on 2015/12/8.
 */
public class VideoActivity extends AppCompatActivity {

    public static final String FILE_URL = "FILE_URL";
    public static final String MEDIA_POSITION = "MEDIA_POSITION";

    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        Bundle bundle = getIntent().getExtras();
        final String fileUrl = bundle.getString(FILE_URL);
        final int mediaPosition = bundle.getInt(MEDIA_POSITION);

        SurfaceView surface = (SurfaceView)findViewById(R.id.surfaceView);
        SurfaceHolder surfaceHolder = surface.getHolder(); // SurfaceHolder是SurfaceView的控制接口
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    mMediaPlayer = new MediaPlayer();
                    mMediaPlayer.setDisplay(holder);
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                    AssetFileDescriptor afd = getAssets().openFd(fileUrl);
                    mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    mMediaPlayer.prepare();
                    mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.seekTo(mediaPosition);
                            mp.start();
                        }
                    });
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        }); // 因为这个类实现了SurfaceHolder.Callback
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            if(mMediaPlayer != null){
                if(mMediaPlayer.isPlaying()){
                    mMediaPlayer.stop();
                }
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
