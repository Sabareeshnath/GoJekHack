package com.go.jek.godrive;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.ToggleButton;

public class MoreActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {


   // ToggleButton toggleButton;
    Switch aSwitch;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        sharedpreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);

        aSwitch= (Switch) findViewById(R.id.switch1);



        aSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        if(b){


            editor.putString("inspection", "on");


        }else{
            editor.putString("inspection", "off");
        }
        editor.commit();
    }
}
