package hu.ait.courseinformer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hu.ait.courseinformer.network.ParseURL;
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
            (new ParseURL(MainActivity.this)).execute(new String[]{URL, num});
        }
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
