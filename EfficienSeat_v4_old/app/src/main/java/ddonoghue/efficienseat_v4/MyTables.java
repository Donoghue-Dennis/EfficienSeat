package ddonoghue.efficienseat_v4;

import java.util.ArrayList;
import java.util.List;

class MyTables {
    private static MyTables mInstance = null;

    public List<localTable> tables = new ArrayList<>();

    protected MyTables(){}

    public static synchronized MyTables getInstance(){
        if(null == mInstance){
            mInstance = new MyTables();
        }
        return mInstance;
    }
}
