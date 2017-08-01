package hu.ait.courseinformer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hu.ait.courseinformer.network.ParseAsyncTask;
import hu.ait.courseinformer.network.ResultListener;

public class MainActivity extends AppCompatActivity implements ResultListener {

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
    }

    @OnClick(R.id.btnRequest)
    public void requestClicked() {
        if (checkInputs()) {
            tvInfo.setText("Retrieving...");
            String dep = etDep.getText().toString().toLowerCase();
            String num = etNum.getText().toString();
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
}
