package co.ipb.adukerang.activity;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import co.ipb.adukerang.R;
import butterknife.ButterKnife;
import butterknife.InjectView;
import co.ipb.adukerang.controller.AppConfig;
import co.ipb.adukerang.controller.AppController;
import co.ipb.adukerang.handler.SessionManager;
import co.ipb.adukerang.handler.SQLiteHandler;

public class LoginActivity extends Activity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    @InjectView(R.id.email) EditText txtEmail;
    @InjectView(R.id.password) EditText txtPassword;
    @InjectView(R.id.login) Button bLogin;
    @InjectView(R.id.spinner_level) MaterialBetterSpinner level;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        //declare spinner
        List<String> levelList = new ArrayList<>();
        levelList.add("Teknisi");
        levelList.add("User");

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, levelList);
        level.setAdapter(adapterSpinner);

        level.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());

        //cek user sudah login apa belum
        if (session.isLoggedIn()) {
            //user sudah login
            Intent mainActivity = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(mainActivity);
            finish();
        }

        //tombol login
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = txtEmail.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();

                //cek user sudah mengisi edittext email & password apa belum
                if (!email.isEmpty() && !password.isEmpty()) {
                    //login
                    checkLogin(email, password);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Silahkan Isi Email dan Password", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGIST, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        session.setLogin(true);

                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String created_at = user
                                .getString("created_at");
                        String gcm_regid = user.getString("gcm_regid");
                        db.addUser(name, email, uid, created_at, gcm_regid);

                        Intent intent = new Intent(LoginActivity.this,
                                DashboardActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "login");
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };

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

