package hu.ait.courseinformer.network;

import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class ParseAsyncSMSTask extends AsyncTask<String, Void, String> {

    private ResultListener resultListener;

    public ParseAsyncSMSTask(ResultListener resultListener) {
        this.resultListener = resultListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String URL = params[0];
        String crn = params[1];
        String result = "-1";

        try {
            Document doc = Jsoup.connect(URL).get();
            Elements courseRows = doc.select("tr.row.even, tr.row.odd");
            for (Element row: courseRows) {
                if (row.select("td.creditNumber").text().equals(crn)) {
                    int rem = Integer.parseInt(row.select("td.remaining").text());
                    if (rem > 0) {
                        String title = row.select("td.title").text();
                        String section = row.select("td.section").text();
                        String creditNumber = row.select("td.creditNumber").text();
                        result = title + " ";
                        if (!section.equals("0"))
                            result += section;
                        result += "(" + creditNumber + ")" + " has " + rem + " spots open!";
                        break;
                    }
                }
            }
        } catch (IOException e) {
            result = "-1";
        }
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        resultListener.resultArrived(s);
    }
}
