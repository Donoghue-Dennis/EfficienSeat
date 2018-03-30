package ddonoghue.efficienseat_v4;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

class myTables {
    private static myTables mInstance = null;

    public List<localTable> tables = new ArrayList<>();

    protected myTables(){}

    public static synchronized myTables getInstance(){
        if(null == mInstance){
            mInstance = new myTables();
        }
        return mInstance;
    }
}
