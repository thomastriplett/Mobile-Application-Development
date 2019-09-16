package com.thomastriplett.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.EditText;
import android.widget.TextView;
import java.text.DecimalFormat;

import static java.lang.Double.parseDouble;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private int count = 0;
    private boolean f_to_c = true;
    private boolean c_to_f = false;
    private String previous = " ";
    private String text;

    private EditText userText;
    private RadioButton radioButton;
    private RadioButton radioButton2;
    private TextView output;
    private TextView history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "MainActivity");

        userText = (EditText) findViewById(R.id.editText);
        radioButton = (RadioButton) findViewById(R.id.radioButton);
        radioButton2 = (RadioButton) findViewById(R.id.radioButton2);
        output = (TextView) findViewById(R.id.textView);
        history = (TextView) findViewById(R.id.textView3);
    }

    @Override
    protected void onDestroy() {
            super.onDestroy();
            Log.d(TAG, "onDestroy:" + count++ + " *********");
    }



    public void fToC(View v) {
        f_to_c = true;
        c_to_f = false;
        Log.d(TAG, "Button 1");
    }

    public void cToF(View v) {
        c_to_f = true;
        f_to_c = false;
        Log.d(TAG, "Button 2");
    }

    public void convert(View v) {
        text = userText.getText().toString();
        double input = parseDouble(text);
        if (f_to_c == true) {
            double result = (input - 32.0)*5.0/9.0;
            DecimalFormat df = new DecimalFormat("#0.0");
            String format_result = df.format(result);
            output.setText(format_result);
            printHistory(v, format_result);
        }
        else if (c_to_f == true) {
            double result = (input*9.0/5.0)+32.0;
            DecimalFormat df = new DecimalFormat("#0.0");
            String format_result = df.format(result);
            output.setText(format_result);
            printHistory(v, format_result);
        }
    }

    public void printHistory(View v, String format_result) {
        if (previous.equals(" ")) {
            if (f_to_c == true) {
                history.setText("F to C: "+text+" -> "+format_result);
                previous = "F to C: "+text+" -> "+format_result;
            }
            else {
                history.setText("C to F: "+text+" -> "+format_result);
                previous = "C to F: "+text+" -> "+format_result;
            }

        }
        else {
            if (f_to_c == true) {
                history.setText("F to C: "+text+" -> "+format_result + "\n" + previous);
                previous = "F to C: "+text+" -> "+format_result + "\n" + previous;
            }
            else {
                history.setText("C to F: "+text+" -> "+format_result + "\n" + previous);
                previous = "C to F: "+text+" -> "+format_result + "\n" + previous;
            }
        }

    }
}
