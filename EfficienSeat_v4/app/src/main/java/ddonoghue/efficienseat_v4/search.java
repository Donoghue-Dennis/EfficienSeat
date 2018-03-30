package ddonoghue.efficienseat_v4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class search extends AppCompatActivity {

    localTable currentTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //initialize custom view

        TableSearchView mTableSearchView = (TableSearchView) findViewById(R.id.custom_view_search);

        //Grab Party Size
        Intent intent = getIntent();
        int partySize = intent.getIntExtra("partySize",0);

        //sort tables for presentation

        //Draw first table

    }

    public void reserveTable(View view){
        //foo
        finish();
    }

    public void nextTable(View view){

    }
    public void cancelSearch(View view){
        finish();
    }
}
