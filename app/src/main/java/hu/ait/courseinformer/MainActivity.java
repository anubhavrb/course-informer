package hu.ait.courseinformer;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hu.ait.courseinformer.network.ParseAsyncTask;
import hu.ait.courseinformer.network.ResultListener;

public class MainActivity extends AppCompatActivity implements ResultListener {

    private final String PREFS_NAME = "MyPrefs";
    private final String FIRST_TIME = "FIRST_TIME";
    private final String PHONE_NUM = "PHONE_NUM";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.etDep)
    EditText etDep;
    @BindView(R.id.etNum)
    EditText etNum;
    @BindView(R.id.tvInfo)
    TextView tvInfo;
    @BindView(R.id.btnRequest)
    Button btnRequest;
    @BindView(R.id.btnStart)
    Button btnStart;
    @BindView(R.id.btnStop)
    Button btnStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        requestPermissions();

        checkFirstTime();
    }

    @OnClick(R.id.btnRequest)
    public void requestClicked() {
        if (checkInputs()) {
            tvInfo.setText("Retrieving...");
            String dep = etDep.getText().toString().toLowerCase().trim();
            String num = etNum.getText().toString().trim();
            String URL = getString(R.string.base_url) + dep + getString(R.string.rest_url);
            (new ParseAsyncTask(MainActivity.this)).execute(new String[]{URL, num});
        }
    }

    @OnClick(R.id.btnStart)
    public void startClicked() {
        if (getSharedPreferences(PREFS_NAME, 0).contains(PHONE_NUM)) {
            Intent intentStart = new Intent(MainActivity.this, ParseService.class);
            startService(intentStart);
            Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Add a phone number first", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btnStop)
    public void stopClicked() {
        Intent intentStop = new Intent(MainActivity.this, ParseService.class);
        stopService(intentStop);
        Toast.makeText(this, "Service stopped", Toast.LENGTH_SHORT).show();
    }

    public void resultArrived(String result) {
        tvInfo.setText(result);
    }

    private boolean checkInputs() {
        if (TextUtils.isEmpty(etDep.getText())) {
            etDep.setError("Should not be empty");
            return false;
        }
        if (TextUtils.isEmpty(etNum.getText())) {
            etNum.setError("Should not be empty");
            return false;
        }
        return true;
    }

    private void requestPermissions() {

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED) ||
            (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) !=
                PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS,
                    Manifest.permission.READ_PHONE_STATE},
                    101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case 101:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permissions granted!", Toast.LENGTH_SHORT)
                            .show();
                }
                else {
                    Toast.makeText(this, "Permissions denied.", Toast.LENGTH_SHORT)
                            .show();
                }
        }
    }

    private void checkFirstTime() {
        SharedPreferences info = getSharedPreferences(PREFS_NAME, 0);
        if (info.getBoolean(FIRST_TIME, true)) {
            showPhoneDialog();
            info.edit().putBoolean(FIRST_TIME, false).commit();
        }
    }

    private void showPhoneDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final EditText etNumber = new EditText(MainActivity.this);
        builder.setTitle("Add Phone Number")
                .setView(etNumber)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!TextUtils.isEmpty(etNumber.getText())) {
                            SharedPreferences info = getSharedPreferences(PREFS_NAME, 0);
                            info.edit().putString(PHONE_NUM, etNumber.getText().toString().trim())
                                    .commit();
                        }
                    }
                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.view_courses:
                startActivity(new Intent(MainActivity.this, CoursesActivity.class));
                break;

            case R.id.add_course:
                break;

            case R.id.add_number:
                SharedPreferences info = getSharedPreferences(PREFS_NAME, 0);
                if (info.contains(PHONE_NUM)) {
                    Toast.makeText(this, info.getString(PHONE_NUM, "No phone number"), Toast.LENGTH_SHORT).show();
                    showPhoneDialog();
                }
                break;

            default:
                break;
        }

        return true;
    }
}
