package co.ipb.adukerang.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import co.ipb.adukerang.R;
import co.ipb.adukerang.controller.AppController;
import co.ipb.adukerang.helper.SlideActivity;

public class DetailsActivity extends AppCompatActivity {


    @InjectView(R.id.ppava) NetworkImageView iv_pp;
    @InjectView(R.id.fotokeluhandet) NetworkImageView iv_keluhan;
    @InjectView(R.id.txtkeluhandet)
    TextView keluhan_details;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setupWindowAnimations();
        ButterKnife.inject(this);

        Bundle select = getIntent().getExtras();
        String pp = select.getString("profile_picture");
        String foto = select.getString("foto");
        String keluhan = select.getString("keluhan");

        iv_pp.setImageUrl(pp, imageLoader);
        iv_keluhan.setImageUrl(foto, imageLoader);
        keluhan_details.setText(keluhan);

    }
    private void setupWindowAnimations() {
        Fade fade = new Fade();
        fade.setDuration(1000);
        getWindow().setEnterTransition(fade);

        Slide slide = new Slide();
        slide.setDuration(1000);
        getWindow().setReturnTransition(slide);
    }
}
