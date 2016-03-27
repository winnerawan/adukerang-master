package co.ipb.adukerang.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import co.ipb.adukerang.R;
import co.ipb.adukerang.controller.AppConfig;
import co.ipb.adukerang.controller.AppController;
import co.ipb.adukerang.handler.SQLiteHandler;
import co.ipb.adukerang.handler.SessionManager;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by winnerawan on 3/23/16.
 */
public class RegisterActivity extends ActionBarActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private static final String PREF_GCM_REG_ID = "PREF_GCM_REG_ID";
    private SQLiteHandler db;
    private SessionManager session;
    private ProgressDialog pDialog;
    private SharedPreferences prefs;
    private String gcmRegid;
    private String tesid="ABCDEFGHIJKLMNOP";
    GoogleCloudMessaging gcm;

    @InjectView(R.id.nameRegis) EditText txtUsername;
    @InjectView(R.id.emailRegis) EditText txtEmail;
    @InjectView(R.id.passwordRegis) EditText txtPassword;
    @InjectView(R.id.regis) Button bRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.inject(this);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        session = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());

        if (session.isLoggedIn()) {
            Intent dashboard = new Intent(RegisterActivity.this,
                    DashboardActivity.class);
            startActivity(dashboard);
            finish();
        }
        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
                gcmRegid = prefs.getString(PREF_GCM_REG_ID, "");
                String name = txtUsername.getText().toString();
                String email = txtEmail.getText().toString();
                String password = txtPassword.getText().toString();
                String gcm_regid = prefs.getString(PREF_GCM_REG_ID, "");
                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                    registerUser(name, email, password);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }
    private void registerUser(final String name, final String email,
                              final String password) {
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGIST, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String created_at = user
                                .getString("created_at");
                        String gcm_regid = user.getString("gcm_regid");

                        db.addUser(name, email, uid, created_at, gcm_regid);

                        Intent intent = new Intent(
                                RegisterActivity.this,
                                LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {


                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "register");
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                params.put("gcm_regid", tesid);
                return params;
            }

        };
        Log.d(TAG,"RGIDT : "+ gcmRegid.toString());
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
