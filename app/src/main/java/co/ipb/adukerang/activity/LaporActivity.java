package co.ipb.adukerang.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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
import co.ipb.adukerang.helper.SuggestionAdapter;


public class LaporActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = LaporActivity.class.getSimpleName();
    private JSONArray result;
    private List<String> listIDBarang =  new ArrayList<>();
    private String KEY_ID_RUANG = "id_ruang";
    private String KEY_ID_BARANG = "id_barang";
    private String KEY_KELUHAN = "keluhan";
    private String KEY_FOTO = "foto";
    private String KEY_UID = "uid";
    private Bitmap bitmap;
    private int PICK_IMAGE_REQUEST = 1;
    private SQLiteHandler db;
    private SessionManager session;

    @InjectView(R.id.autoCompleteTextView) AutoCompleteTextView tv_id_ruang;
    @InjectView(R.id.spItems) Spinner spBarang;
    @InjectView(R.id.toolbar) Toolbar toolbar;
    @InjectView(R.id.folder) ImageButton bChoose;
    @InjectView(R.id.editKeluhan) EditText txtKeluhan;
    @InjectView(R.id.btnLapor) Button bLapor;
    @InjectView(R.id.btnCancel) Button bCancel;
    @InjectView(R.id.foto) ImageView foto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_lapor);
        getDataBarang();

        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        tv_id_ruang.setAdapter(new SuggestionAdapter(this, tv_id_ruang.getText().toString()));
        bChoose.setOnClickListener(this);
        bLapor.setOnClickListener(this);
    }
        private void showFileChooser() {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                foto.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onClick(View v) {
        if(v == bChoose){
            showFileChooser();
        }
        if (v == bLapor) {
            setKeluhan();
        }
    }
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void getDataBarang(){
        //Creating a string request
        StringRequest stringRequest = new StringRequest(AppConfig.URL_GET_BARANG,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            j = new JSONObject(response);
                            result = j.getJSONArray("results");
                            getBarang(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });


        RequestQueue requestQueue = Volley.newRequestQueue(this);

        requestQueue.add(stringRequest);
    }
    private void getBarang(JSONArray j) {
        for (int i = 0; i < j.length(); i++) {
            try {
                JSONObject json = j.getJSONObject(i);

                listIDBarang.add(json.getString("id_barang"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        spBarang.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, listIDBarang));

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void setKeluhan(){
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_SET_KELUHAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        Toast.makeText(LaporActivity.this, s , Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();
                        Log.d(TAG, "LAPOR KOMANDAN ! :"+tv_id_ruang.getText());
                        //Showing toast
                        Toast.makeText(LaporActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String foto = getStringImage(bitmap);
                String id_ruang = tv_id_ruang.getText().toString();
                String id_barang = spBarang.getSelectedItem().toString();
                String keluhan = txtKeluhan.getText().toString();

                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put(KEY_FOTO, foto);
                params.put(KEY_ID_RUANG, id_ruang);
                params.put(KEY_ID_BARANG, id_barang);
                params.put(KEY_KELUHAN, keluhan);

                //returning parameters
                return params;

            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }
}
