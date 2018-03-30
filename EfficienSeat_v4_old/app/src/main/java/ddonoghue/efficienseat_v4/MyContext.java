package ddonoghue.efficienseat_v4;

import android.app.Application;
import android.content.Context;

/**
 * Created by DDonoghue on 3/5/2018.
 */

public class MyContext extends Application {
    private static MyContext instance;

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }

    public static Context getContext(){
        return instance.getApplicationContext();
    }
}
