package chili.gooey.rotarycalculator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by common on 10/22/18.
 */

public class DebugFunctions {

    private String TAG = "DebugFunction";

    private Activity mainActivity;

    DebugFunctions(Activity context) {
        this.mainActivity = context;
    }

    DebugFunctions(Activity context, String tag) {
        this.mainActivity = context;
        this.TAG = tag;
    }


    void debugView() {
        ViewGroup root = mainActivity.findViewById(android.R.id.content);
        ViewUtility.getAllChildren(TAG, root);
        //((View)root).setBackgroundColor(Color.RED);
        View someView = root.findViewById(R.id.b6);
        int [] location = {0,0};
        someView.getLocationOnScreen(location);
        Log.d(TAG, "debugView: " + location[0] + " " + location[1]);
        touchActionSimuation(MotionEvent.ACTION_DOWN, location[0], location[1]);
        touchActionSimuation(MotionEvent.ACTION_UP, location[0], location[1]);
    }

    void debugAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle("Hello Test Me!");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mainActivity, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("YO1");
        arrayAdapter.add("YO2");
        arrayAdapter.add("YO3");
        arrayAdapter.add("YO4");
        arrayAdapter.add("YO5");
        arrayAdapter.add("YO6");
        arrayAdapter.add("YO7");

        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(mainActivity.getApplicationContext(), "Meh", Toast.LENGTH_SHORT);
            }
        });
        builder.show();


        Handler handler=new Handler();
        Runnable r=new Runnable() {
            public void run() {
                touchActionSimuation(MotionEvent.ACTION_DOWN, 680, 1430);
                touchActionSimuation(MotionEvent.ACTION_UP,  680, 1430);
            }
        };
        handler.postDelayed(r, 3000);



    }

    void debugNLU(){
        EditText textViewDebug = mainActivity.findViewById(R.id.tvdebug);
        final String textToParse = textViewDebug.getText().toString();


        @SuppressLint("StaticFieldLeak")
        AsyncTask networkTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {

                URL connectionURL;

                try {
//                    connectionURL = new URL("https://dialogflow.googleapis.com/v2/projects/test-61aa2/agent/sessions/bc0c9c99-6be1-1f7b-291e-b92146be18d7:detectIntent");
//
//                    HttpsURLConnection connection = (HttpsURLConnection) connectionURL.openConnection();
//                    connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
//                    connection.setRequestProperty("Authorization", "Bearer ya29.c.ElopBgcGS_Sa3w4AwbIezgOCeEeE_7kslBjD05lhfy_ylhli6W3Vo5Ci-u5icT8tjy0jnDGGWbZ2uZPvInNC7GKrsdchvw24dNSuYt9Qyny89Xz1sHBczoVICos");
//                    JSONObject queryJSON = new JSONObject("{\"queryInput\":{\"text\":{\"text\":\""+textToParse+"\",\"languageCode\":\"en\"}},\"queryParams\":{\"timeZone\":\"America/New_York\"}}");
//
//                    DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
//                    wr.writeBytes(queryJSON.toString());
//                    wr.flush();
//                    wr.close();
//
//                    String output = read(connection.getInputStream());
//                    Log.d(TAG, "run: " + output);



                    connectionURL = new URL("https://api.wit.ai/message?v=20181001&q="+textToParse);

                    HttpsURLConnection connection = (HttpsURLConnection) connectionURL.openConnection();
                    connection.setRequestProperty("Authorization", "Bearer 2ENHJRUJEEOIH7BUQCKLXY65OK7G6UFN");

                    final String output = read(connection.getInputStream());
                    Log.d(TAG, "run: " + output);
                    JSONObject jsonOutput = null;
                    try {
                        jsonOutput = new JSONObject(output);
                        final String jsonOutString = jsonOutput.getJSONObject("entities").getString("intent");

                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                forceUpdateHistoryView(jsonOutString);
                                Intent toSend = new Intent("calulator.updateHistoryString");
                                toSend.putExtra("String", jsonOutString);
                                mainActivity.sendBroadcast(toSend);
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                forceUpdateHistoryView("Nothing!");
                                Intent toSend = new Intent("calulator.updateHistoryString");
                                toSend.putExtra("String", "Nothing!");
                                mainActivity.sendBroadcast(toSend);
                            }
                        });
                    }


                }
                catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        };

        networkTask.execute();


    }

    void debugIntent() throws IOException {
        PackageManager pm = mainActivity.getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA|PackageManager.GET_SHARED_LIBRARY_FILES);
        for(final ApplicationInfo app : apps)
        {
            if(!app.publicSourceDir.contains("atak")) continue;

            Log.d(TAG, "debugIntent: "+ app.publicSourceDir);
            ZipFile apk = new ZipFile(app.publicSourceDir);
            ZipEntry manifest = apk.getEntry("AndroidManifest.xml");
            if (manifest!=null)
            {
                InputStream stream = apk.getInputStream(manifest);
                StringBuilder stringBuilder = new StringBuilder();
                String line = null;
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                while((line=bufferedReader.readLine())!= null)
                    //stringBuilder.append(line);
                    Log.d(TAG, "debugIntent: "+ line);
                Log.d(TAG, "debugIntent: "+stringBuilder.toString());
            }
            mainActivity.runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                                  Log.d(TAG, "run: M4$$");
                              }
                          }
            );
        }
    }

    void debugCreateAlertDialog() {
        AlertDialog.Builder abd = new AlertDialog.Builder(mainActivity);
        abd.setIcon(R.drawable.ic_launcher_background);
        abd.setTitle("Hellow");
        abd.setMessage("This is a new message");
        abd.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e(TAG, "onClick: " + this.toString());
                View v = mainActivity.getWindow().getDecorView().getRootView();
                ViewUtility.getAllChildren(TAG, v);
            }
        });
        abd.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });



        AlertDialog newAD = abd.create();
        newAD.show();
        Handler hd = new Handler();
        Log.e(TAG, "debugCreateAlertDialog: Posting Runnable");
        hd.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "run: inRunnable" );
//                View v = getWindow().getDecorView().getRootView();
//                getAllViewChildren(v);

                List<android.support.v4.app.Fragment> fgList =  ((AppCompatActivity)mainActivity).getSupportFragmentManager().getFragments();
                for(android.support.v4.app.Fragment f : fgList)
                {
                    Log.e(TAG, "runnable: "+f.toString());
                }
                Class wmgClass = null;


                try {
                    wmgClass = Class.forName("android.view.WindowManagerGlobal");
                    Object wmgInstnace = wmgClass.getMethod("getInstance").invoke(null, (Object[])null);

                    Method getViewRootNames = wmgClass.getMethod("getViewRootNames");
                    Method getRootView = wmgClass.getMethod("getRootView", String.class);
                    String[] rootViewNames = (String[])getViewRootNames.invoke(wmgInstnace, (Object[])null);

                    for(String viewName : rootViewNames) {
                        Log.e(TAG, "run: _________________________");
                        View rootView = (View) getRootView.invoke(wmgInstnace, viewName);
                        Log.d(TAG, "Found root view: " + viewName + "|||RootView: " + rootView + " |||Context: " + rootView.getContext().toString());
                        //getAllChildren(rootView);
                        if(rootView.getContext() instanceof ContextThemeWrapper)
                        {
                            Log.d(TAG, "run: I'm a ContextThemeWrapper Only");
                        }
                        if(rootView.getContext() instanceof Activity)
                        {
                            Log.d(TAG, "run: I'm an Activity");
                        }


                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }, 1000);

    }

    void touchActionSimuation(int MOTIONEVENT, float x, float y)
    {
        long downtime = SystemClock.uptimeMillis();
        int metastate = 0;
        MotionEvent motionEvent = MotionEvent.obtain(downtime,
                downtime+1,
                MOTIONEVENT,
                x, y,
                metastate);

        mainActivity.dispatchTouchEvent(motionEvent);
    }

    private String read(InputStream is) throws IOException {
        BufferedReader in = null;
        String inputLine;
        StringBuilder body;
        try {
            in = new BufferedReader(new InputStreamReader(is));

            body = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                body.append(inputLine);
            }
            in.close();

            return body.toString();
        } catch(IOException ioe) {
            throw ioe;
        }
    }

}
