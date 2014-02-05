package eu.szwiec.draggablelinearlayout;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;

public class MainActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ViewGroup container = (ViewGroup) findViewById(R.id.container);
        for(int i=0; i<20; i++) {
            Button button = new Button(this);
            button.setText(String.valueOf(i));
            container.addView(button);
        }
    }
}