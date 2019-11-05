package com.example.ventus.networksignal.localization;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.ventus.networksignal.R;

public class StartingScreen extends Activity {
    private Button learnButton;
    private Button locateButton;
    private Button syncButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.starting_screen);
    }


    public void onClick(View view) {

        Button button= (Button) view;
        Intent intent;
        switch (button.getId()) {
            case R.id.learn_button:
                intent = new Intent(StartingScreen.this, Positions.class);
                startActivity(intent);
                break;
            case R.id.locate_button:
                intent = new Intent(StartingScreen.this, Locate.class);
                startActivity(intent);
                break;
            /*case R.id.test_button:
                intent = new Intent(StartingScreen.this, PinActivity.class);
                startActivity(intent);
                break;*/
            default:
                break;

        }

    }

}
