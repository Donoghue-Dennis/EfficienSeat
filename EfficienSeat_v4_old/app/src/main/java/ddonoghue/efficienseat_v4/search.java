package ddonoghue.efficienseat_v4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class search extends AppCompatActivity {

    TableSearchView mTableSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //initialize custom view
        mTableSearchView = (TableSearchView) findViewById(R.id.custom_view_search);

        //Grab Party Size
        Intent intent = getIntent();
        int partySize = intent.getIntExtra("partySize",0);
        mTableSearchView.setPartySize(partySize);
    }

    public void reserveTable(View view){
        mTableSearchView.reserveCurrentTable();
        finish();
    }

    public void nextTable(View view){
        mTableSearchView.nextTable();
    }

    public void cancelSearch(View view){
        finish();
    }
}
