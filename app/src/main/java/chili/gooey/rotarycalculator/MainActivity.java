package chili.gooey.rotarycalculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static String TAG = "Main.RotCal";

    enum Operation
    {
        CLEAR, PLUS, MINUS, MULTIPLY, DIVIDE;
    }

    //Model in MVC
    private static String current_Result;
    private static String first_Operand, second_Operand;
    private static Operation current_Operation;
    private static boolean finished_Typing;
    TextView calc_Screen;
    TextView history_Screen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        calc_Screen = (TextView) findViewById(R.id.calcScreen);
        history_Screen = (TextView) findViewById(R.id.histScreen);
        history_Screen.setMovementMethod(new ScrollingMovementMethod());
        clearScreen();
        history_Screen.setText("");

        //Controller in MVC
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
    }

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
                typingValue(buttonText);
                break;
            case R.id.badd:
                typingOperation(Operation.PLUS);
                break;
            case R.id.bsub:
                typingOperation(Operation.MINUS);
                break;
            case R.id.bmult:
                typingOperation(Operation.MULTIPLY);
                break;
            case R.id.bdiv:
                typingOperation(Operation.DIVIDE);
                break;
            case R.id.bc:
                clearScreen();
                break;
            case R.id.benter:
                break;
        }


        updateHistoryView();
    }

    void typingValue(String value)
    {
        if(current_Operation==Operation.CLEAR)
        {
            first_Operand+=value;
            updateResultView(String.valueOf(evaluate(first_Operand, "0", Operation.PLUS)));
        }
        else
        {
            second_Operand+=value;
            updateResultView(String.valueOf(evaluate(first_Operand, second_Operand, current_Operation)));
        }
    }

    void typingOperation(Operation op)
    {

        if(first_Operand!="" && second_Operand!="")
        {
            String result = String.valueOf(evaluate(first_Operand, second_Operand, current_Operation));
            first_Operand = result;
            second_Operand = "";
            updateResultView(result);
            current_Operation=op;
        }
        else
        {
            current_Operation=op;
        }
    }

    void clearScreen()
    {
        calc_Screen.setText("");
        first_Operand="";
        second_Operand="";
        current_Operation=Operation.CLEAR;
    }

    double evaluate(String first, String second, Operation op)
    {
        Log.d(TAG, String.format("evaluate: %s, %s, %s", first, second, op.toString()));
        switch (op) {
            case PLUS:
                return Double.valueOf(first) + Double.valueOf(second);
            case MINUS:
                return Double.valueOf(first) - Double.valueOf(second);
            case MULTIPLY:
                return Double.valueOf(first) * Double.valueOf(second);
            case DIVIDE:
                return Double.valueOf(first) / Double.valueOf(second);
        }
        return 0;
    }

    void updateResultView(String value)
    {
        current_Result=value;
        (calc_Screen).setText(current_Result);
    }

    void updateHistoryView()
    {
        history_Screen.setText(String.format(history_Screen.getText()+"%s | %s | %s\n", first_Operand, second_Operand, current_Operation));
        ((ScrollView) findViewById(R.id.scrollView2)).fullScroll(View.FOCUS_DOWN);
    }

}
