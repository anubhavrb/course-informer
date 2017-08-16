package hu.ait.courseinformer.network;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class ParseAsyncTask extends AsyncTask<String, Void, String> {

    private ResultListener resultListener;

    public ParseAsyncTask(ResultListener resultListener) {
        this.resultListener = resultListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String URL = params[0];
        String num = params[1];
        String result = "";

        boolean match = false;

        try {
            Document doc = Jsoup.connect(URL).get();
            Elements courseRows = doc.select("tr.row.even, tr.row.odd");
            for (Element row: courseRows) {
                if (row.select("td.course").text().equals(num)) {
                    match = true;
                    int rem = Integer.parseInt(row.select("td.remaining").text());
                    String title = row.select("td.title").text();
                    String section = row.select("td.section").text();
                    result += title;
                    if (!section.equals("0"))
                        result += " " + section;
                    result += " - " + rem + " spots open!\n";
                }
            }
            if (!match) {
                result = "A matching course could not be found.";
            }
        } catch (IOException e) {
            result = "An error occurred while retrieving the information.";
            return result;
        }
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        resultListener.resultArrived(s);
    }
}
