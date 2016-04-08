package co.ipb.adukerang.activity;

import android.app.ProgressDialog;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import co.ipb.adukerang.R;
import co.ipb.adukerang.controller.AppConfig;
import co.ipb.adukerang.controller.AppController;
import co.ipb.adukerang.handler.SQLiteHandler;
import co.ipb.adukerang.handler.SessionManager;
import co.ipb.adukerang.helper.HttpRequest;

public class NotifActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = NotifActivity.class.getSimpleName();

    private SQLiteHandler db;
    private SessionManager session;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    String uid,email,name,teknisi_id,id_ruangan,id_barang,keluhan,foto,id_pengadu,gcm_pengadu,id_keluhan;
    @InjectView(R.id.n_foto_keluhan) NetworkImageView iv_foto_keluhan;
    @InjectView(R.id.txtRuang) TextView tvRuang;
    @InjectView(R.id.txtBarang) TextView tvBarang;
    @InjectView(R.id.txtKeluhan) TextView tvKeluhan;
    @InjectView(R.id.spStatus) Spinner spStatus;
    @InjectView(R.id.spVerify) Spinner spVerify;
    @InjectView(R.id.btnLapor) Button bLapor;
    @InjectView(R.id.btnCancel) Button bCancel;
    @InjectView(R.id.temporary_gcm_pengadu) TextView temp_gcm;
    @InjectView(R.id.temporary_unique_id) TextView temp_uid;
    @InjectView(R.id.temporary_id_keluhan) TextView temp_id_keluhan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif);
        ButterKnife.inject(this);
        getKeluhanBaru();
        getGCMUser();
        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        teknisi_id = user.get("uid");
        email = user.get("email");

        List<String> list_status = new ArrayList<String>();
        list_status.add("PROSES");
        list_status.add("PENDING");
        list_status.add("SELESAI");
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list_status);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStatus.setAdapter(statusAdapter);

        List<String> list_verify = new ArrayList<String>();
        list_verify.add("");
        list_verify.add("");
        list_verify.add("TUTUP ADUAN");

        ArrayAdapter<String> verifyAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list_verify);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spVerify.setAdapter(verifyAdapter);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        bLapor.setOnClickListener(this);
        bCancel.setOnClickListener(this);



    }
    @Override
    public void onClick(View v) {
        if(v == bCancel){
            onBackPressed();
        }
        if (v == bLapor) {

           updateKeluhan();
            sendNotifications();
            onBackPressed();
        }
        String tutup = ((Spinner)findViewById(R.id.spVerify)).getSelectedItem().toString();

        if ((v == bLapor) && (tutup.equals("TUTUP ADUAN"))) {
            tutupKeluhan();
        }
    }
    private void getKeluhanBaru() {
        JsonArrayRequest req = new JsonArrayRequest(AppConfig.URL_TEKNISI_NOTIF,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        try {
                            // Parsing json array response
                            // loop through each json object
                            //jsonResponse = "";
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject teknisi = (JSONObject) response
                                        .get(i);

                                 id_keluhan = teknisi.getString("id_keluhan");
                                 id_ruangan = teknisi.getString("id_ruang");
                                 id_barang = teknisi.getString("id_barang");
                                 keluhan = teknisi.getString("keluhan");
                                 foto = teknisi.getString("foto");
                                 uid = teknisi.getString("unique_id");

                                tvRuang.setText(id_ruangan);
                                tvBarang.setText(id_barang);
                                tvKeluhan.setText(keluhan);
                                iv_foto_keluhan.setImageUrl(foto, imageLoader);
                                temp_uid.setText(uid);
                                temp_id_keluhan.setText(id_keluhan);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "KELUHAN BARU TIDAK ADA: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "KELUHAN BARU TIDAK ADA" + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }

    private void updateKeluhan() {
        Map<String, String> data = new HashMap<String, String>();
        getKeluhanBaru();
        String spStatusSelect = ((Spinner)findViewById(R.id.spStatus)).getSelectedItem().toString();
        data.put("status", spStatusSelect);
        data.put("id_keluhan", temp_id_keluhan.getText().toString());
        if (HttpRequest.post(AppConfig.URL_UPDATE_KELUHAN + spStatusSelect +"&id_keluhan" + temp_id_keluhan.getText().toString()).form(data).created())
            System.out.print("Notifications Send!");
        Log.i(TAG, data.toString());
    }

    private void getGCMUser() {
        JsonArrayRequest req = new JsonArrayRequest(AppConfig.URL_GET_GCM_USER+uid,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        try {
                            // Parsing json array response
                            // loop through each json object
                            //jsonResponse = "";
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject teknisi = (JSONObject) response
                                        .get(i);

                                gcm_pengadu = teknisi.getString("gcm_regid");
                                temp_gcm.setText(gcm_pengadu);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "GCM TIDAK ADA: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "GCM TIDAK ADA" + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }
    private void sendNotifications() {
        getGCMUser();
        Map<String, String> data = new HashMap<String, String>();
        data.put("id", gcm_pengadu);
        if (HttpRequest.post(AppConfig.URL_SEND_NOTIF + gcm_pengadu).form(data).created())
            System.out.print("Notifications Send!");
        Log.i(TAG, data.toString());
    }
    private void tutupKeluhan() {
        final ProgressDialog loading = ProgressDialog.show(this,"Tutup Aduan...","Please wait...",false,false);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_TUTUP_ADUAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        loading.dismiss();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(NotifActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                int fix=1;
                String verifySelect = ((Spinner)findViewById(R.id.spVerify)).getSelectedItem().toString();
                String idk = ((TextView)findViewById(R.id.temporary_id_keluhan)).getText().toString();

                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();
                //Adding parameters
                params.put("is_fix", String.valueOf(fix));
                params.put("keterangan", verifySelect);
                params.put("id_keluhan", idk);

                //returning parameters
                return params;

            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
        Toast.makeText(getApplicationContext(),"ADUAN DITUTUP", Toast.LENGTH_LONG).show();
    }
}
