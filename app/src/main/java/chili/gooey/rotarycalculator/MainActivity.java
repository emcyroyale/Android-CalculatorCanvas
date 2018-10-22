package chili.gooey.rotarycalculator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/*
    MainActivity acts as the entry point and controller(OnClicks) of this Test Calculator Application.
    It sets up the view(Layout) and interacts with the model (Calculator).
    The model interacts with the view through a pub-sub pattern (BroadcastReciever)
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static String TAG = "Main.RotCal";

    //View in MVC
    TextView calc_Screen;
    TextView history_Screen;
    BroadcastReceiver viewUpdateReciever;


    //Model in MVC
    Calculator calculatorModel;

    //Debug
    DebugFunctions dbgFunction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e(TAG, "onCreate: MainActivity:" + this.toString());

        //Initialize View________________________________________________
        calc_Screen = (TextView) findViewById(R.id.calcScreen);
        history_Screen = (TextView) findViewById(R.id.histScreen);
        history_Screen.setMovementMethod(new ScrollingMovementMethod());
        clearScreen();
        history_Screen.setText("");

        IntentFilter viewRecieverIntentFilter = new IntentFilter();
        viewRecieverIntentFilter.addAction("calulator.clearScreen");
        viewRecieverIntentFilter.addAction("calulator.updateResult");
        viewRecieverIntentFilter.addAction("calulator.updateHistoryOperation");
        viewRecieverIntentFilter.addAction("calulator.updateHistoryString");


        viewUpdateReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Clear Screen
                //Update Result
                //Update History
                switch(intent.getAction()){
                    case "calulator.clearScreen":
                        clearScreen();
                        break;
                    case "calulator.updateResult":
                        String result = (String)intent.getExtras().get("Result");
                        updateResultView(result);
                        break;
                    case "calulator.updateHistoryOperation":
                        Bundle extras = intent.getExtras();
                        String firstOpd = (String)extras.get("FirstOperand");
                        String secondOpd = (String)extras.get("SecondOperand");
                        String currentOp = (String)extras.get("CurrentOperation");
                        updateHistoryView(firstOpd, secondOpd, currentOp);
                        break;
                    case "calulator.updateHistoryString":
                        updateHistoryView("A");
                        break;
                }
            }
        };

        this.registerReceiver(viewUpdateReciever, viewRecieverIntentFilter);

        //Controller in MVC________________________________________________
        findViewById(R.id.b0).setOnClickListener(this);
        findViewById(R.id.b1).setOnClickListener(this);
        findViewById(R.id.b2).setOnClickListener(this);
        findViewById(R.id.b3).setOnClickListener(this);
        findViewById(R.id.b4).setOnClickListener(this);
        findViewById(R.id.b5).setOnClickListener(this);
        findViewById(R.id.b6).setOnClickListener(this);
        findViewById(R.id.b7).setOnClickListener(this);
        findViewById(R.id.b8).setOnClickListener(this);
        findViewById(R.id.b9).setOnClickListener(this);
        findViewById(R.id.bdot).setOnClickListener(this);

        findViewById(R.id.badd).setOnClickListener(this);
        findViewById(R.id.bsub).setOnClickListener(this);
        findViewById(R.id.bmult).setOnClickListener(this);
        findViewById(R.id.bdiv).setOnClickListener(this);

        findViewById(R.id.benter).setOnClickListener(this);
        findViewById(R.id.bc).setOnClickListener(this);

        //Create Instance of Model________________________________________________
        calculatorModel = new Calculator(this);

        //DEBUG___________________________________________________________________
        dbgFunction = new DebugFunctions(this);
        findViewById(R.id.bdebug).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
//                debugView();
//                debugAlert();
                dbgFunction.debugCreateAlertDialog();
//                String response = null;
//                try {
//                    debugIntent();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        });

//        //Testing dynamic view creation
//
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//
//        ViewGroup rootView = this.findViewById(R.id.baseRootView);
//
//        ImageView ivDebug = new ImageView(this);
//        ivDebug.setBackgroundColor(Color.RED);
//        ivDebug.setLayoutParams(new ViewGroup.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels));
//
//        Log.d(TAG, "onCreate: " + rootView.getWidth() + " " + rootView.getHeight());
//        Log.d(TAG, "onCreate: " + displayMetrics.widthPixels + " " + displayMetrics.heightPixels);
//
//        rootView.addView(ivDebug);
    }


    /*--------------------------------------------------------
        EVENT OVERRIDES
     ---------------------------------------------------------*/


    //Controller
    @Override
    public void onClick(View view) {
        String buttonText = ((Button) view).getText().toString();
        switch (view.getId())
        {
            case R.id.b0:
            case R.id.b1:
            case R.id.b2:
            case R.id.b3:
            case R.id.b4:
            case R.id.b5:
            case R.id.b6:
            case R.id.b7:
            case R.id.b8:
            case R.id.b9:
            case R.id.bdot:
                calculatorModel.typingValue(buttonText);
                break;
            case R.id.badd:
                calculatorModel.typingOperation(Calculator.Operation.PLUS);
                break;
            case R.id.bsub:
                calculatorModel.typingOperation(Calculator.Operation.MINUS);
                break;
            case R.id.bmult:
                calculatorModel.typingOperation(Calculator.Operation.MULTIPLY);
                break;
            case R.id.bdiv:
                calculatorModel.typingOperation(Calculator.Operation.DIVIDE);
                break;
            case R.id.bc:
                clearScreen();
                calculatorModel.clear();
                break;
            case R.id.benter:
                break;
        }
        //Used to show past operations on the history screen
        calculatorModel.updateHistory();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent: " + event.getAction());
        return super.onTouchEvent(event);
    }



    /*--------------------------------------------------------
        VIEW UPDATING METHODS
     ---------------------------------------------------------*/

    void clearScreen()
    {
        calc_Screen.setText("");
    }

    void updateResultView(String value)
    {
        (calc_Screen).setText(value);
    }

    void updateHistoryView(String first_Operand, String second_Operand, String current_Operation)
    {
        history_Screen.setText(String.format(history_Screen.getText()+"%s | %s | %s\n", first_Operand, second_Operand, current_Operation));
        ((ScrollView) findViewById(R.id.scrollView2)).fullScroll(View.FOCUS_DOWN);
    }

    void updateHistoryView(String value)
    {
        history_Screen.setText(String.format(history_Screen.getText()+"%s\n", value));
        ((ScrollView) findViewById(R.id.scrollView2)).fullScroll(View.FOCUS_DOWN);
    }


}
