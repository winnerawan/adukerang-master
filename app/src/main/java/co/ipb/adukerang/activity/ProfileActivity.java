package co.ipb.adukerang.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import co.ipb.adukerang.R;
import co.ipb.adukerang.fragment.KeluhanFragment;
import co.ipb.adukerang.fragment.KeluhanKuFragment;
import co.ipb.adukerang.fragment.ProfileFragment;
import co.ipb.adukerang.handler.SQLiteHandler;
import co.ipb.adukerang.handler.SessionManager;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = ProfileActivity.class.getSimpleName();

    private SQLiteHandler db;
    private SessionManager session;
    private String uid,name,email,regid;

    @InjectView(R.id.username) TextView tv_username;
    @InjectView(R.id.email) TextView tv_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.inject(this);
        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());

        HashMap<String, String> user = db.getUserDetails();
            name  = user.get("name");
            email = user.get("email");
            regid = user.get("gcm_regid");

        tv_username.setText(name);
        tv_email.setText(email);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager()));
        viewPager.getAdapter().notifyDataSetChanged();

        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);
    }
    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 2;

        public SampleFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return (position == 0) ? "PROFILE" :
                    getResources().getString(R.string.title_keluhan);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            return (position == 0) ? new ProfileFragment() : new KeluhanKuFragment();
        }
    }
}
