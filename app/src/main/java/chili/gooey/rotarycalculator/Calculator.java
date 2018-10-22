package chili.gooey.rotarycalculator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by common on 10/22/18.
 */

public class Calculator {


    private String TAG = "Calculator";
    Activity mainActivity;

    //Used to specify all potential operations used by the calculator
    //Clear: Initialization/Clear Screen
    //Plus: During addition
    //Minus, Multiply, Divide: Analogous to Plus
    enum Operation
    {
        CLEAR, PLUS, MINUS, MULTIPLY, DIVIDE;
    }

    //Model in MVC
    private static String current_Result;
    private static String first_Operand, second_Operand;
    private static Operation current_Operation;


    Calculator(Activity context) {
        mainActivity = context;
        current_Result = "";
        first_Operand = "";
        second_Operand = "";
        current_Operation = Operation.CLEAR;
    }

    Calculator(Activity context, String tag) {
        mainActivity = context;
        this.TAG = tag;
        current_Result = "";
        first_Operand = "";
        second_Operand = "";
        current_Operation = Operation.CLEAR;
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


    //Method: typingValue
    //Used to identify where a numerical or decimal button press gets appended to
    //Consider that every operation has two operands
    //How do we know which operand to append the button presses value to?
    void typingValue(String value)
    {
        if(current_Operation==Operation.CLEAR)
        {
            first_Operand+=value;
            updateResult(String.valueOf(evaluate(first_Operand, "0", Operation.PLUS)));
        }
        else
        {
            second_Operand+=value;
            updateResult(String.valueOf(evaluate(first_Operand, second_Operand, current_Operation)));
        }
    }

    //Method: typingOperation
    //Used to identify what to do when given an operation button press
    //If both operands are full then an operation button press indicates we are moving on the the next
    //set of operands
    //Otherwise we still need to wait for the operation to complete
    //E.g.  Instead of waiting til 3+4+5+6=18 to evaluate
    //      We evaluate at each step    3+4=7
    //                                  7+5=12
    //                                  12+6=18
    void typingOperation(Operation op)
    {

        if(first_Operand!="" && second_Operand!="")
        {
            String result = String.valueOf(evaluate(first_Operand, second_Operand, current_Operation));
            first_Operand = result;
            second_Operand = "";
            updateResult(result);
            current_Operation=op;
        }
        else
        {
            current_Operation=op;
        }
    }



    void clear()
    {
        first_Operand="";
        second_Operand="";
        current_Operation=Operation.CLEAR;
    }

    void clearWithView()
    {
        clear();
        mainActivity.sendBroadcast(new Intent("calulator.clearScreen"));

    }

    void updateResult(String value)
    {
        current_Result=value;
        Intent toSend = new Intent("calulator.updateResult");
        toSend.putExtra("Result", value);
        mainActivity.sendBroadcast(toSend);
    }


    void updateHistory()
    {
        Intent toSend = new Intent("calulator.updateHistoryOperation");
        Bundle extras = new Bundle();
        extras.putString("FirstOperand", first_Operand);
        extras.putString("SecondOperand", second_Operand);
        extras.putString("CurrentOperation", current_Operation.toString());
        toSend.putExtras(extras);
        mainActivity.sendBroadcast(toSend);
    }
}
