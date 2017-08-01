package hu.ait.courseinformer;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.util.Log;

import hu.ait.courseinformer.network.ParseAsyncSMSTask;
import hu.ait.courseinformer.network.ResultListener;

public class ParseService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean enabled = false;
    private String[] courses = {"MAT 10388", "PHY 15460"};

    public class ParseTimerThread extends Thread implements ResultListener {

        public void run() {

            Handler handler = new Handler(ParseService.this.getMainLooper());

            while(enabled) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < courses.length; i++) {
                            String[] course = courses[i].split(" ");
                            String dep = course[0].toLowerCase().trim();
                            String crn = course[1].trim();

                            String URL = "http://www.davidson.edu/offices/registrar/schedules-and-courses/fall-2017-courses/"
                                    + dep + "-fall-2017-courses";
                            (new ParseAsyncSMSTask(ParseTimerThread.this)).execute(new String[]{URL, crn});
                        }
                    }
                });
                try {
                    sleep(180000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void resultArrived(String result) {
            if (!result.equals("-1")) {
                Log.d("LOG_TAG", "Inside resultArrived");
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("7047779769", null, result, null, null);
            }
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
