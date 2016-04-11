package co.ipb.adukerang.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import butterknife.InjectView;
import co.ipb.adukerang.R;
import co.ipb.adukerang.helper.SlideActivity;

public class DetailsActivity extends SlideActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


    }
}
