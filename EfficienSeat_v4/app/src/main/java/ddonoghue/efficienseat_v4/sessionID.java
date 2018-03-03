package ddonoghue.efficienseat_v4;

/**
 * Created by DDonoghue on 3/2/2018.
 */

class sessionID {
    private static sessionID instance;

    static sessionID getInstance() {
        if (instance == null)
            instance = new sessionID();
        return instance;
    }

    private sessionID(){}

    private int sessionString;

    public int getID(){
        return sessionString;
    }

    public void setID(int ID){
        this.sessionString = ID;
    }
}
