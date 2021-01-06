package com.rcode;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rcode.dialog.NoRepeatDialog;
import com.rcode.view.PopScreenView;
import com.rcode.view.SmartViewFlipper;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private NoRepeatDialog dialog;
//    private SmartViewFlipper sf;
    private PopScreenView pop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        sf = findViewById(R.id.sf);
        pop = findViewById(R.id.pop);

        dialog = new NoRepeatDialog(this);
        dialog.setShowNextDialogDelay(1000);
    }

    public void showMoreDialog(View view){

        dialog.show(R.layout.dialog_layout, new NoRepeatDialog.OnDialogListener() {
            @Override
            public void onClose() {

            }

            @Override
            public void onShow() {
                TextView tv = findViewById(R.id.tv);
                tv.setText("我是第一个对话框");
            }
        });
        dialog.show(R.layout.dialog_layout, new NoRepeatDialog.OnDialogListener() {
            @Override
            public void onClose() {

            }

            @Override
            public void onShow() {
                TextView tv = findViewById(R.id.tv);
                tv.setText("我是第2个对话框");
            }
        });
        dialog.show(R.layout.dialog_layout, new NoRepeatDialog.OnDialogListener() {
            @Override
            public void onClose() {
                Toast.makeText(MainActivity.this, "第三个对话框已经关闭", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onShow() {
                TextView tv = findViewById(R.id.tv);
                tv.setText("我是第三个对话框");
            }
        });
    }


    public void startSF(View view){
//        sf.setAdapter(new SmartViewFlipper.Adapter<Vh,String>() {
//
//            @Override
//            public SmartViewFlipper.ViewHolder createView(Context context) {
//                return null;
//            }
//
//            @Override
//            public void updateData(SmartViewFlipper.ViewHolder viewHolder, String o) {
//
//            }
//
//            class Vh extends SmartViewFlipper.ViewHolder{
//
//                public Vh(View itemView) {
//                    super(itemView);
//                }
//            }
//        });
//        sf.start();
    }

    public void sendPop(View view){
        pop.addText("大家都觉得");
    }
}
