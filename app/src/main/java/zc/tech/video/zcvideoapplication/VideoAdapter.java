package zc.tech.video.zcvideoapplication;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Zack on 2015/12/8.
 */
public class VideoAdapter extends BaseAdapter{

    private class ItemViewHolder{
        public TextureView textureView;
        public TextView textView;
    }

    protected Context context;
    protected String[] urlList;
    private LayoutInflater inflater;

    public VideoAdapter(Context context, String[] datalist){
        urlList = datalist;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }


        @Override
    public int getCount() {
        return urlList.length;
    }

    @Override
    public Object getItem(int position) {
        return urlList[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ItemViewHolder holder;
        final String data = urlList[position];
        if (convertView == null) {
            holder = new ItemViewHolder();
            convertView = inflater.inflate(R.layout.video_item, null);
            holder.textureView = (TextureView)convertView.findViewById(R.id.textureView);
            holder.textView = (TextView)convertView.findViewById(R.id.textView);
            convertView.setTag(holder);
        }else{
            holder = (ItemViewHolder)convertView.getTag();
        }

        holder.textView.setText(data.substring(0, data.length() - 4));

        TextureView.SurfaceTextureListener mListener = holder.textureView.getSurfaceTextureListener();
        if(mListener != null && mListener instanceof VideoSurfaceTextureListener){
            ((VideoSurfaceTextureListener)mListener).destroyVideo();
        }

        mListener = new VideoSurfaceTextureListener(data);
        holder.textureView.setSurfaceTextureListener(mListener);
        if (holder.textureView.isAvailable()) {
            mListener.onSurfaceTextureAvailable(holder.textureView.getSurfaceTexture(), holder.textureView.getWidth(), holder.textureView.getHeight());
        }

        final VideoSurfaceTextureListener listener = mListener != null ? (VideoSurfaceTextureListener)mListener : null;
        holder.textureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    MediaPlayer mp = listener.getMediaPlayer();
                    int mediaPosition = mp.getCurrentPosition();
                    mp.pause();

                    Intent intent = new Intent();
                    intent.setClass(context, VideoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(VideoActivity.FILE_URL, data);
                    bundle.putInt(VideoActivity.MEDIA_POSITION, mediaPosition);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            }
        });
        return convertView;
    }

    private class VideoSurfaceTextureListener implements TextureView.SurfaceTextureListener {

        private MediaPlayer mMediaPlayer;
        private String FILE_URL = "";

        public VideoSurfaceTextureListener(String file){
            FILE_URL = file;
        }

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            if(mMediaPlayer != null){
                return;
            }
            Surface surface = new Surface(surfaceTexture);
            try {
                AssetFileDescriptor afd = context.getAssets().openFd(FILE_URL);
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                mMediaPlayer.setSurface(surface);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepareAsync();
                mMediaPlayer.setVolume(0f, 0f);
                // Play video when the media source is ready for playback.
                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                    }
                });

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {}

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {}

        public MediaPlayer getMediaPlayer(){
            return mMediaPlayer;
        }

        public void destroyVideo(){
            try{
                if(mMediaPlayer != null){
                    mMediaPlayer.stop();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            try{
                if(mMediaPlayer != null){
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
