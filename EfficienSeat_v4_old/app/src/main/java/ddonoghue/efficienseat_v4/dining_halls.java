package ddonoghue.efficienseat_v4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

public class dining_halls extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dining_halls);
    }

    public void goToDining(View view){
        Intent intent = new Intent(this, dining_hall_map.class);
        String strID = String.valueOf(view.getTag());
        intent.putExtra("DINING_HALL",strID);
        startActivity(intent);
    }
}
