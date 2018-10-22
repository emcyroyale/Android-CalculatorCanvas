package chili.gooey.rotarycalculator;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by common on 10/22/18.
 */

public class ViewUtility {


    private String TAG = "ViewUtility";

    ViewUtility() {}

    ViewUtility(String tag) {
        this.TAG = tag;
    }


    public static void getAllChildren(String TAG, View view)
    {
        Log.d(TAG, "getAllChildren: " + view.toString());

        if(!(view instanceof ViewGroup)){
            Log.e(TAG, "getAllViewChildren1: " + view.toString() + " |Text: " + getTextFromView(view));
            return;
        }
        for(int i=0; i<((ViewGroup)view).getChildCount(); i++)
        {
            getAllChildren(TAG, ((ViewGroup)view).getChildAt(i));
            Log.e(TAG, "getAllViewChildren2: " + ((ViewGroup)view).toString() + " |Text: " + getTextFromView(view));
        }
    }


    public static String getTextFromView(View v)
    {
        try {
            Method m = v.getClass().getMethod("getText");
            return m.invoke(v).toString();
        } catch (NoSuchMethodException e) {
            //e.printStackTrace();
        } catch (IllegalAccessException e) {
            //e.printStackTrace();
        } catch (InvocationTargetException e) {
            //e.printStackTrace();
        }
        return "";
    }

}
