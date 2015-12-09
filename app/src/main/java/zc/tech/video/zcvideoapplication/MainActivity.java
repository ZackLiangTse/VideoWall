package zc.tech.video.zcvideoapplication;

import android.graphics.SurfaceTexture;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.TextureView;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity{

    private ListView listView;
    private VideoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.listView);
        initView();
    }

    private void initView() {
        String[] dataList = new String[]{"rilakkumaSky.mp4","rilakkuma.mp4","Hyoyeon.mp4","Jessica.mp4","Seohyun.mp4","Sooyoung.mp4","Sunny.mp4","Taeyeon.mp4","Tiffany.mp4","YoonA.mp4","Yuri.mp4"};
        adapter = new VideoAdapter(this, dataList);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listView.setAdapter(null);
    }
}
