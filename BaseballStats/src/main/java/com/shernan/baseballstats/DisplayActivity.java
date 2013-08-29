package com.shernan.baseballstats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.shernan.baseballstats.utils.*;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
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
    private static final String TAG_SUCCESS = "success", TAG_STATS = "stats", TAG_YEAR = "yearID", TAG_TEAM = "teamID";
    private static final String TAG_AVG = "AVG", TAG_OBP = "OBP", TAG_SLG = "SLG", TAG_OPS = "OPS";
    private static final String TAG_WINS = "W", TAG_LOSSES = "L", TAG_ERA = "ERA", TAG_ER = "ER", TAG_IP = "IP";

    //Column information arrays shared between Batting/Pitching
    private static final String[] COLUMN_TAGS = {TAG_YEAR, TAG_TEAM};
    private static final String[] COLUMN_NAMES = {"Year", "Team"};

    //Batting specific tags
    private static final String[] BATTING_TAGS = {TAG_AVG, TAG_OBP, TAG_SLG, TAG_OPS};

    //Pitching specific tags
    private static final String[] PITCHING_TAGS = {TAG_WINS, TAG_LOSSES, TAG_ER, TAG_IP, TAG_ERA};

    //Player name from the input screen
    String inputFName;
    String inputLName;

    //Type of statistic
    String statType;

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
        statType = queryIntent.getStringExtra(QueryActivity.STAT_TYPE);

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
            params.add(new BasicNameValuePair("statType", statType));
            params.add(new BasicNameValuePair("firstName", inputFName));
            params.add(new BasicNameValuePair("lastName", inputLName));

            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(read_db_url, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All Stats: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // Stats found
                    // Getting Array of stats
                    stats = json.getJSONArray(TAG_STATS);

                    // looping through stats
                    for (int i = 0; i < stats.length(); i++) {
                        JSONObject jsonStats = stats.getJSONObject(i);
                        HashMap<String, String> mapStats = new HashMap<String, String>();

                        // Storing each json item in variable
                        for(String tag: COLUMN_TAGS){
                            mapStats.put(tag, jsonStats.getString(tag));
                        }

                        if(statType.equals("Batting")){
                            for(String tag: BATTING_TAGS){
                                mapStats.put(tag, jsonStats.getString(tag));
                            }
                        } else if(statType.equals("Pitching")){
                            for(String tag: PITCHING_TAGS){
                                mapStats.put(tag, jsonStats.getString(tag));
                            }
                        }

                        // adding HashList to ArrayList
                        statsList.add(mapStats);
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
                    table.setBackgroundResource(getResources().getIdentifier("blank" , "drawable", getPackageName()));

                    addTableRows(table, new String[]{inputFName, inputLName}, null);

                    if(statType.equals("Batting"))
                        addTableRows(table, merge(COLUMN_NAMES, BATTING_TAGS), merge(COLUMN_TAGS, BATTING_TAGS));
                    else if(statType.equals("Pitching"))
                        addTableRows(table, merge(COLUMN_NAMES, PITCHING_TAGS), merge(COLUMN_TAGS, PITCHING_TAGS));

                    setContentView(table);
                }
            });

        }

    }

    /**
     * This will add statistics to an already existing table layout
     * @param table to add statistics to
     * @param columnNames
     * @param columnTags from the JSON, may be null if all you want to display are the names
     */

    public void addTableRows(TableLayout table, String[] columnNames, String[] columnTags){
        //Set up table header
        TableRow header = new TableRow(DisplayActivity.this);
        header.setGravity(Gravity.CENTER);

        for(int i = 0; i < columnNames.length; i++){
            TextView headerText = createColumn(columnNames[i], 20, Color.BLACK);
            headerText.setGravity(Gravity.CENTER);

            if(i != columnNames.length - 1)
                headerText.setPadding(0, 0, 5, 0);

            header.addView(headerText);
        }

        table.addView(header);

        //Just in case all we want to do is make a header, no need to do the following check
        if(columnTags != null){
            //Get data from JSON response and put into table
            for(HashMap<String, String> yearResult: statsList){
                TableRow row = new TableRow(DisplayActivity.this);
                row.setGravity(Gravity.CENTER);

                for(int i = 0; i < columnTags.length; i++){
                    TextView rowText = createColumn(yearResult.get(columnTags[i]), 20, Color.BLACK);
                    rowText.setBackgroundResource(R.drawable.cell_shape);

                    if(i != columnTags.length - 1)
                        rowText.setPadding(0, 0, 5, 0);

                    row.addView(rowText);
                }

                table.addView(row);
            }
        }
    }

    /**
     * A helper method to create a column in a table
     * @param text
     * @param size
     * @param color
     * @return the column created
     */

    public TextView createColumn(String text, int size, int color){
        TextView view = new TextView(DisplayActivity.this);
        view.setText(text);
        view.setTextSize(size);
        view.setTextColor(color);
        return view;
    }

    /**
     * A helper to merge various arrays together as needed for the column tags/names
     * @return the merged array
     */

    public String[] merge(String[] arr1, String[] arr2){
        String[] merged = new String[arr1.length + arr2.length];

        for(int i = 0; i < merged.length; i++){
            if(i < arr1.length){
                merged[i] = arr1[i];
            }
            else{
                merged[i] = arr2[i - arr1.length];
            }
        }

        return merged;
    }
    
}
