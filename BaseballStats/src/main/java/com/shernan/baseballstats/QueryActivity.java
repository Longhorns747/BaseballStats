package com.shernan.baseballstats;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class QueryActivity extends Activity {
    public static final String PLAYER_FIRST_NAME = "com.shernan.PLAYER_FIRST_NAME";
    public static final String PLAYER_LAST_NAME = "com.shernan.PLAYER_LAST_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.query, menu);
        return true;
    }

    public void startQuery(View v){
        //Create Intent
        Intent i = new Intent(this, DisplayActivity.class);

        //Get player name
        String nameText = ((EditText) findViewById(R.id.query)).getText().toString();
        String[] playerName = nameText.split(" ");

        //Prepare Data for Activity Transfer
        i.putExtra(PLAYER_FIRST_NAME, playerName[0]);
        i.putExtra(PLAYER_LAST_NAME, playerName[1]);

        //Switch activities
        startActivity(i);
    }
    
}
