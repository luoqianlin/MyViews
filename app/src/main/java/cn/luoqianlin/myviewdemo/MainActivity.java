package cn.luoqianlin.myviewdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ProgressDisplayView pdv= (ProgressDisplayView) findViewById(R.id.pdv);
        pdv.setEnabled(true);
        pdv.setSelectedPos(0);
        pdv.setOnItemChangeListener(new ProgressDisplayView.OnItemChangeListener() {
            @Override
            public void onItemChanged(int oldPos, int newPos) {
                Log.e("Main",String.format("old postion:%d,new postion:%d",oldPos,newPos));
            }
        });
    }
}
