package leokapanen.testvkwall;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import leokapanen.login.Auth;
import leokapanen.login.LoginFragment;
import leokapanen.wall.WallFragment;

public class MainActivity extends AppCompatActivity implements IWallActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(false);

        if (!Auth.INSTANCE.isNeedLogin()) {
            switchFragment(new WallFragment());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Auth.INSTANCE.isNeedLogin()) {
            switchFragment(new LoginFragment());
        }
    }

    /**
     * Sets activity title
     *
     * @param title
     */
    @Override
    public void setTitle(String title) {
        if (title != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    /**
     * Changes fragment into activity
     *
     * @param fragment
     */
    @Override
    public void switchFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
    }
}
