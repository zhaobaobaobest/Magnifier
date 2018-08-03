package com.ct.magnifierdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.ct.ctmagnifier.MagnifierView;

public class MainActivity extends AppCompatActivity {
    private Switch switchBtn,switchBtn2;
    private RelativeLayout rl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rl = (RelativeLayout) findViewById(R.id.rl);

        switchBtn = (Switch) findViewById(R.id.switchBtn);
        final MagnifierView mv = new MagnifierView.Builder(MainActivity.this)
                .intiLT(100,200)
                .viewWH(320,320)
                .scale(2f)
                .alpha(16)
                .color("#ff00ff")
                .build();
        switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    mv.startViewToRoot();
                }
                else
                {
                    mv.closeViewToRoot();
                }
            }
        });

        switchBtn2 = (Switch) findViewById(R.id.switchBtn2);
        final MagnifierView mv2 = new MagnifierView.Builder(MainActivity.this)
                .rootVg(rl)
                .viewWH(200,200)
                .build();
        switchBtn2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    mv2.startViewToRoot();
                }
                else
                {
                    mv2.closeViewToRoot();
                }
            }
        });

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mv.resetXY();
                mv2.resetXY();
            }
        });
    }
}
