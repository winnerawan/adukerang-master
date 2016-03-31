package co.ipb.adukerang.adapter;

import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.util.List;

import co.ipb.adukerang.R;
import co.ipb.adukerang.controller.AppController;
import co.ipb.adukerang.fragment.KeluhanFragment;
import co.ipb.adukerang.fragment.KeluhanKuFragment;
import co.ipb.adukerang.helper.CircledNetworkImageView;
import co.ipb.adukerang.model.Keluhan;

/**
 * Created by winnerawan on 3/26/16.
 */
public class ListKeluhanKuAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private KeluhanKuFragment fragment;
    private List<Keluhan> listKeluhan;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public ListKeluhanKuAdapter(KeluhanKuFragment fragment, List<Keluhan> listKeluhan) {
        this.fragment = fragment;
        this.listKeluhan = listKeluhan;

    }

    @Override
    public int getCount() {
        return listKeluhan.size();
    }

    @Override
    public Object getItem(int location) {
        return listKeluhan.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) fragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_keluhan, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        TextView id_keluhan = (TextView) convertView.findViewById(R.id.id_keluhan);
        CircledNetworkImageView image = (CircledNetworkImageView) convertView.findViewById(R.id.thumbnail);
        TextView keluhan = (TextView) convertView.findViewById(R.id.keluhan);

        Keluhan k = listKeluhan.get(position);

        image.setImageUrl(k.getFoto(), imageLoader);
        id_keluhan.setText(k.getId_keluhan());
        keluhan.setText(k.getKeluhan());

        return convertView;
    }
}
