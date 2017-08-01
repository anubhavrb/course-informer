package hu.ait.courseinformer;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import hu.ait.courseinformer.network.ParseAsyncTask;
import hu.ait.courseinformer.network.ResultListener;

public class ParseService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean enabled = false;

    public class ParseTimerThread extends Thread implements ResultListener {

        public void run() {

            Handler handler = new Handler(ParseService.this.getMainLooper());

            while(enabled) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        String URL = "http://www.davidson.edu/offices/registrar/schedules-and-courses/fall-2017-courses/csc-fall-2017-courses";
                        (new ParseAsyncTask(ParseTimerThread.this)).execute(new String[]{URL, "321"});
                    }
                });
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void resultArrived(String result) {
            Toast.makeText(ParseService.this, result, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (!enabled) {
            enabled = true;
            new ParseTimerThread().start();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        enabled = false;
        super.onDestroy();
    }
}
