package co.ipb.adukerang.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.ipb.adukerang.R;
import co.ipb.adukerang.activity.DashboardActivity;
import co.ipb.adukerang.activity.DetailsActivity;
import co.ipb.adukerang.adapter.ListKeluhanAdapter;
import co.ipb.adukerang.adapter.ListKeluhanTeknisiAdapter;
import co.ipb.adukerang.controller.AppConfig;
import co.ipb.adukerang.controller.AppController;
import co.ipb.adukerang.handler.SQLiteHandler;
import co.ipb.adukerang.handler.SessionManager;
import co.ipb.adukerang.model.Keluhan;

/**
 * Created by winnerawan on 4/3/16.
 */
public class TeknisiFragment extends ListFragment {
    private static final String TAG = DashboardActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private List<Keluhan> listKeluhan = new ArrayList<Keluhan>();
    private ListKeluhanTeknisiAdapter adapter;
    public JSONObject obj;
    private SQLiteHandler db;
    private SessionManager session;
    public TeknisiFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewKeluhan = inflater.inflate(R.layout.keluhan_fragment, container, false);
        ListView lv_keluhan = (ListView) viewKeluhan.findViewById(android.R.id.list);
        adapter = new ListKeluhanTeknisiAdapter(this, listKeluhan);
        lv_keluhan.setAdapter(adapter);
        db = new SQLiteHandler(getActivity().getApplicationContext());
        session = new SessionManager(getActivity().getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        String uid = user.get("uid");
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();

        // Creating volley request obj
        JsonArrayRequest foodReq = new JsonArrayRequest(AppConfig.URL_HOME_TEKNISI+uid, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());
                hidePDialog();
                // Parsing json
                for (int i = 0; i < response.length(); i++) {
                    try {
                        obj = response.getJSONObject(i);
                        Keluhan keluhan = new Keluhan();
                        keluhan.setName(obj.getString("name"));
                        keluhan.setFoto(obj.getString("foto"));
                        keluhan.setKeluhan(obj.getString("keluhan"));
                        keluhan.setProfile_picture(obj.getString("profile_picture"));
                        listKeluhan.add(keluhan);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                VolleyLog.e("Error: ", error.toString());
                hidePDialog();
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(foodReq);
        return viewKeluhan;
    }
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //selected = ((SetanTextView)v.findViewById(R.id.title)).getText().toString();
        //String image = ((Drink)drinkMenus.get(position)).getThumbnailUrl();
        String selected_idk = ((TextView)v.findViewById(R.id.idk)).getText().toString();

        Toast toast = Toast.makeText(getActivity(), selected_idk, Toast.LENGTH_SHORT);
        toast.show();
        Intent details = new Intent(getActivity(), DetailsActivity.class);
        details.putExtra("id_keluhan", selected_idk);
        //details.putExtra("image", image);
        //startActivity(details);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }

    }

}