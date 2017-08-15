package hu.ait.courseinformer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
    }

    @OnClick(R.id.btnRequest)
    public void requestClicked() {
        if (checkInputs()) {
            tvInfo.setText("Retrieving...");
            String dep = etDep.getText().toString().toLowerCase().trim();
            String num = etNum.getText().toString().trim();
            String URL = "http://www.davidson.edu/offices/registrar/schedules-and-courses/fall-2017-courses/"
                        + dep + "-fall-2017-courses";
            (new ParseAsyncTask(MainActivity.this)).execute(new String[]{URL, num});
        }
    }

    @OnClick(R.id.btnStart)
    public void startClicked() {
        Log.d("LOG_TAG", "Inside start btn");
        Intent intentStart = new Intent(MainActivity.this, ParseService.class);
        startService(intentStart);
    }

    @OnClick(R.id.btnStop)
    public void stopClicked() {
        Log.d("LOG_TAG", "Inside stop btn");
        Intent intentStop = new Intent(MainActivity.this, ParseService.class);
        stopService(intentStop);
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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS},
                    101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case 101:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permissions granted!", Toast.LENGTH_SHORT)
                            .show();
                }
                else {
                    Toast.makeText(this, "Permissions denied.", Toast.LENGTH_SHORT)
                            .show();
                }
        }
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
                break;

            case R.id.add_course:
                break;

            case R.id.add_number:
                break;

            default:
                break;
        }

        return true;
    }
}
