package eu.szwiec.draggablelinearlayout;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ViewGroup container = (ViewGroup) findViewById(R.id.container);
        for(int i=0; i<20; i++) {
            View v = LayoutInflater.from(this).inflate(R.layout.row, null);
            ((TextView)v.findViewById(R.id.textView)).setText("text " + i);
            container.addView(v);
        }
    }
}