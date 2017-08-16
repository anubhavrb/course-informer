package hu.ait.courseinformer;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.util.Log;

import hu.ait.courseinformer.data.Course;
import hu.ait.courseinformer.network.ParseAsyncSMSTask;
import hu.ait.courseinformer.network.ResultListener;
import io.realm.Realm;
import io.realm.RealmResults;

public class ParseService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private final String PREFS_NAME = "MyPrefs";
    private final String PHONE_NUM = "PHONE_NUM";

    private boolean enabled = false;

    public class ParseTimerThread extends Thread implements ResultListener {

        public void run() {

            Handler handler = new Handler(ParseService.this.getMainLooper());

            while(enabled) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Realm realmCourse = Realm.getDefaultInstance();
                        RealmResults<Course> courseResults = realmCourse.where(Course.class).findAll();

                        for (int i = 0; i < courseResults.size(); i++) {
                            Course course = courseResults.get(i);
                            String dep = course.getDep().toLowerCase().trim();
                            String crn = course.getCrn().trim();

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
                SharedPreferences info = getSharedPreferences(PREFS_NAME, 0);
                String number = info.getString(PHONE_NUM, "");

                if (!number.equals("")) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(number, null, result, null, null);
                }
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
