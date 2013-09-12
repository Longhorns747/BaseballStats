package com.shernan.baseballstats;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class GraphActivity extends Activity {

    private String graphStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        Intent statsIntent = getIntent();
        graphStats = statsIntent.getStringExtra(DisplayActivity.GRAPH_STATS);

    }

    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            WebView webview = (WebView) findViewById(R.id.graphView);

            String content = "<html>"
                    + "  <head>"
                    + "    <script type=\"text/javascript\" src=\"https://www.google.com/jsapi\"></script>"
                    + "    <script type=\"text/javascript\">"
                    + "      google.load(\"visualization\", \"1\", {packages:[\"corechart\"]});"
                    + "      google.setOnLoadCallback(drawChart);"
                    + "      function drawChart() {"
                    + "        var data = google.visualization.arrayToDataTable(["
                    +          graphStats
                    + "        ]);"
                    + "        var options = {"
                    + "          title: 'Stats!',"
                    + "          width:" + webview.getWidth() + ","
                    + "          height:" + webview.getHeight() + ","
                    + "          hAxis: {title: 'Year', titleTextStyle: {color: 'red'}}"
                    + "        };"
                    + "        var chart = new google.visualization.LineChart(document.getElementById('chart_div'));"
                    + "        chart.draw(data, options);"
                    + "      }"
                    + "    </script>"
                    + "  </head>"
                    + "  <body>"
                    + "    <div id=\"chart_div\" style=\"width:" + webview.getWidth() + "px; height: " + webview.getHeight() + "px;\"></div>"
                    + "  </body>" + "</html>";

            WebSettings webSettings = webview.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webview.requestFocusFromTouch();
            webview.loadDataWithBaseURL( "file:///android_asset/", content, "text/html", "utf-8", null );
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.graph, menu);
        return true;
    }

    public String prepareData(){
        return null;
    }
    
}
