package ddonoghue.efficienseat_v4;


import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

class MyDDBClient {
    private static MyDDBClient mInstance = null;

    // Initialize the Amazon Cognito credentials provider
    CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
            MyContext.getContext(),
            "us-east-1:20683c3f-19dc-43cd-9f92-0fd149d55078", // Identity pool ID
            Regions.US_EAST_1 // Region
    );
    AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);

    protected MyDDBClient(){}

    public static synchronized MyDDBClient getInstance(){
        if(null == mInstance){
            mInstance = new MyDDBClient();
        }
        return mInstance;
    }
}
