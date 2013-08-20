package com.shernan.baseballstats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.shernan.baseballstats.utils.*;

import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.DrawableContainer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class DisplayActivity extends Activity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    private static String read_db_url = "http://54.213.67.121/db_read.php";

    ArrayList<HashMap<String, String>> statsList;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_STATS = "stats";
    private static final String TAG_YEAR = "yearID";
    private static final String TAG_AVG = "AVG";

    //Player name from the input screen
    String inputFName;
    String inputLName;

    // stats JSONArray
    JSONArray stats = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        //Getting the name of the player from query screen
        Intent queryIntent = getIntent();
        inputFName = queryIntent.getStringExtra(QueryActivity.PLAYER_FIRST_NAME);
        inputLName = queryIntent.getStringExtra(QueryActivity.PLAYER_LAST_NAME);

        statsList = new ArrayList<HashMap<String, String>>();

        new LoadStats().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.display, menu);
        return true;
    }

    /**
     * Background Async Task to Load all stats by making HTTP Request
     * */
    class LoadStats extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DisplayActivity.this);
            pDialog.setMessage("Loading stats. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting stats
         * */
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("firstName", inputFName));
            params.add(new BasicNameValuePair("lastName", inputLName));

            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(read_db_url, "GET", params);

            // Check your log cat for JSON reponse
            //Log.d("All Stats: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // Stats found
                    // Getting Array of stats
                    stats = json.getJSONArray(TAG_STATS);

                    // looping through stats
                    for (int i = 0; i < stats.length(); i++) {
                        JSONObject c = stats.getJSONObject(i);

                        // Storing each json item in variable
                        String year = c.getString(TAG_YEAR);
                        String avg = c.getString(TAG_AVG);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap
                        map.put(TAG_YEAR, year);
                        map.put(TAG_AVG, avg);

                        // adding HashList to ArrayList
                        statsList.add(map);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Put JSON data into table format
                     * */
                    TableLayout table = new TableLayout(DisplayActivity.this);
                    table.setBackgroundResource(getResources().getIdentifier("blankbg" , "drawable", getPackageName()));

                    //Set up name header
                    TableRow tableHeader = new TableRow(DisplayActivity.this);
                    tableHeader.setGravity(Gravity.CENTER);
                    TextView fName = new TextView(DisplayActivity.this);
                    fName.setGravity(Gravity.CENTER);
                    fName.setPadding(0, 0, 10, 0);
                    fName.setText(inputFName);
                    fName.setTextSize(20);
                    fName.setTextColor(Color.BLACK);
                    TextView lName = new TextView(DisplayActivity.this);
                    lName.setGravity(Gravity.CENTER);
                    lName.setText(inputLName);
                    lName.setTextSize(20);
                    lName.setTextColor(Color.BLACK);
                    table.addView(tableHeader);

                    //Set up table header
                    TableRow statsHeader = new TableRow(DisplayActivity.this);
                    statsHeader.setGravity(Gravity.CENTER);
                    TextView yearHeader = new TextView(DisplayActivity.this);
                    yearHeader.setText("Year");
                    yearHeader.setPadding(0, 0, 10, 0);
                    yearHeader.setTextSize(20);
                    yearHeader.setTextColor(Color.BLACK);
                    TextView AVGHeader = new TextView(DisplayActivity.this);
                    AVGHeader.setText("AVG");
                    AVGHeader.setTextSize(20);
                    AVGHeader.setTextColor(Color.BLACK);
                    statsHeader.addView(yearHeader);
                    statsHeader.addView(AVGHeader);
                    table.addView(statsHeader);

                    //Get data from JSON response and put into table
                    for(HashMap<String, String> yearResult: statsList){
                        TableRow statsRow = new TableRow(DisplayActivity.this);
                        statsRow.setGravity(Gravity.CENTER);
                        statsRow.setDividerPadding(10);

                        TextView year = new TextView(DisplayActivity.this);
                        year.setText(yearResult.get(TAG_YEAR));
                        year.setPadding(0, 0, 10, 0);
                        year.setTextSize(20);
                        year.setTextColor(Color.BLACK);
                        TextView battingAvg = new TextView(DisplayActivity.this);
                        battingAvg.setText(yearResult.get(TAG_AVG));
                        battingAvg.setTextSize(20);
                        battingAvg.setTextColor(Color.BLACK);
                        statsRow.addView(year);
                        statsRow.addView(battingAvg);
                        table.addView(statsRow);
                    }

                    setContentView(table);
                }
            });

        }

    }
    
}
