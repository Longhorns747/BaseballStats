package com.shernan.baseballstats;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class GraphActivity extends Activity {

    private String graphStats;
    private String formatPattern;
    private String vAxis;

    // Progress Dialog
    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        Intent statsIntent = getIntent();
        graphStats = statsIntent.getStringExtra(DisplayActivity.GRAPH_STATS);
        formatPattern = statsIntent.getStringExtra(DisplayActivity.DATA_FORMAT);
    }

    /**
     * Need to wait until the WebView has attached before we put the graph in
     * @param hasFocus
     */

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
                    + "          hAxis: {title: 'Year', titleTextStyle: {color: 'red'}},"
                    + "          vAxis: {format: '" + formatPattern + "'}"
                    + "        };"
                    + "        var chart = new google.visualization.LineChart(document.getElementById('chart_div'));"
                    + "        var formatter = new google.visualization.NumberFormat({pattern:'" + formatPattern + "'});"
                    + "        formatter.format(data, 1);"
                    + "        chart.draw(data, options);"
                    + "      }"
                    + "    </script>"
                    + "  </head>"
                    + "  <body>"
                    + "    <div id=\"chart_div\"></div>"
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
    
}
