package com.leslie.stock.analyzer;

import com.leslie.stock.analyzer.swing.TranslucentFrame;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Connection.Request;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HeroWatcher {

    private static final String URL = "http://xueqiu.com/";
    private static final int CONNECTION_TIME_OUT = 10 * 1000;

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private Date lastDate = new Date();
    private TranslucentFrame popupWindow = new TranslucentFrame(2, "notice", "");

    public void watch(final String id) {
        Runnable runnable = new Runnable() {
            public void run() {
                Document doc = getHeroDocument(id);
                List<JSONObject> newStatuses = parse(doc);
                noticePopup(newStatuses);
            }
        };
        executor.scheduleAtFixedRate(runnable, 3, 5, TimeUnit.SECONDS);
    }

    private Document getHeroDocument(String id) {
        Document doc = null;

        try {
            Connection connection = Jsoup.connect(URL + id).timeout(CONNECTION_TIME_OUT);

            Request request = connection.request();
            request.ignoreHttpErrors(true);
            request.ignoreContentType(true);
            doc = connection.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return doc;
    }

    private List<JSONObject> parse(Document doc) {
        if (doc == null) {
            return Collections.emptyList();
        }

        Elements scripts = doc.getElementsByTag("script");
        for (int i = 0; i < scripts.size(); i++) {
            Element script = scripts.get(i);
            if (!script.toString().contains("SNB.data.statuses")) {
                continue;
            }

            int start = script.toString().indexOf("SNB.data.statuses") + 20;
            int end = script.toString().indexOf("SNB.data.statusType") - 4;
            JSONObject statusJson = JSONObject.fromObject(script.toString().substring(start, end));
            if (statusJson == null) {
                return Collections.emptyList();
            }

            JSONArray statuses = statusJson.getJSONArray("statuses");
            List<JSONObject> newStatuses = new ArrayList<JSONObject>(statuses.size());
            synchronized (lastDate) {
                Date tempDate = lastDate;
                for (int j = 0; j < statuses.size(); j++) {
                    JSONObject newStatus = new JSONObject();

                    newStatus.put("description", statuses.getJSONObject(j).get("description"));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(Long.parseLong(statuses.getJSONObject(j).getString("created_at")));
                    Date createdAt = calendar.getTime();
                    if (createdAt.after(lastDate)) {
                        newStatus.put("created_at", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createdAt));
                        newStatuses.add(newStatus);
                        if (createdAt.after(tempDate)) {
                            tempDate = createdAt;
                        }
                    }
                }
                lastDate = tempDate;
            }
            return newStatuses;
        }
        return Collections.emptyList();
    }

    private void noticePopup(List<JSONObject> newStatuses) {
        for (int h = newStatuses.size() - 1; h >= 0; h--) {
            JSONObject status = newStatuses.get(h);
            String message = String.format("%s - %s", status.get("created_at"), status.getString("description"));
            System.out.println(message);
            popupWindow.setMessage(message);
            popupWindow.popup();
        }
    }

    public static void main(String[] args) {
        //new HeroWatcher().watch("6785033954");
        new HeroWatcher().watch("6254653026");
    }

}
