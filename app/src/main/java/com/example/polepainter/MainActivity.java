package com.example.polepainter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.INTERNET;

public class MainActivity extends AppCompatActivity  {



    Button get;
    TextView plot,plotDtls;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    private final static int ALL_PERMISSIONS_RESULT = 101;
    LocationTrack locationTrack;
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    //private String url = "https://www.mocky.io/v2/597c41390f0000d002f4dbd1";

    String url = "https://www.mapquestapi.com/geocoding/v1/reverse?key=yiPg9UvSW6rpSxGtGvDq08vdyFNONg7d&location=";
    String url1="";
    String url2="&includeRoadMetadata=true&includeNearestIntersection=true";
    String totUrl="";
    double longitude=0d;
    double latitude=0d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        permissions.add(INTERNET);

        permissionsToRequest = findUnAskedPermissions(permissions);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
        get = findViewById(R.id.get);
        plot=findViewById(R.id.plot);
        plotDtls=findViewById(R.id.plotDtls);





        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationTrack = new LocationTrack(MainActivity.this);
                if (locationTrack.canGetLocation()) {
                    longitude = locationTrack.getLongitude();
                    latitude = locationTrack.getLatitude();
                    plot.setText(Double.toString(latitude)+" "+Double.toString(longitude));
                    url1=Double.toString(latitude)+","+Double.toString(longitude);
                    totUrl=url+url1+url2;
                    plot.setText(Double.toString(latitude)+"\n"+Double.toString(longitude));
                    AsyncTaskExample asyncTask = new AsyncTaskExample();
                    asyncTask.execute();
                    //Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
                } else {
                    locationTrack.showSettingsAlert();
                }
            }
        });



    }

    private class AsyncTaskExample extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            sendAndRequestResponse();
            return null;
        }

        @Override
        protected void onPostExecute(String parsedText) {

        }
    }

    private void sendAndRequestResponse() {
        //RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(this);

        //String Request initialized
        mStringRequest = new StringRequest(Request.Method.GET, totUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject json = null;
                try {
                    json = new JSONObject(response.toString());
                    String a =json.get("results").toString();
                    JSONArray jarr1=new JSONArray(a);
                    JSONObject jobj1= (JSONObject) jarr1.get(0);
                    String b=jobj1.get("locations").toString();
                    JSONArray jarr2=new JSONArray(b);
                    JSONObject jobj2= (JSONObject) jarr2.get(0);
                    //Toast.makeText(getApplicationContext(),"Response :" + jobj2.get("street").toString(), Toast.LENGTH_LONG).show();//display the response on screen
                    plotDtls.setText(jobj2.get("street").toString()+", "+
                            jobj2.get("adminArea6").toString()+", "+
                            jobj2.get("adminArea6Type").toString()+", "+
                            jobj2.get("adminArea5").toString()+", "+
                            jobj2.get("adminArea5Type").toString()+", "+
                            jobj2.get("adminArea4").toString()+", "+
                            jobj2.get("adminArea4Type").toString()+", "+
                            jobj2.get("adminArea3").toString()+", "+
                            jobj2.get("adminArea3Type").toString()+", "+
                            jobj2.get("adminArea1").toString()+", "+
                            jobj2.get("adminArea1Type").toString()+", "+
                            jobj2.get("postalCode").toString()+", "
                    );
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.i(TAG,"Error :" + error.toString());
            }
        });

        mRequestQueue.add(mStringRequest);
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @SuppressLint("MissingSuperCall")
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationTrack.stopListener();
    }


}