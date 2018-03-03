package ddonoghue.efficienseat;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


/**
 * Created by DDonoghue on 11/26/2017.
 */

public class CustomView extends View {

    private Rect mRect;
    private Paint mPaintRect;
    private Paint mPaintTable;
    boolean drawflag = false;
    List<Table> tables = new ArrayList<>();

    public CustomView(Context context){
        super(context);

        init(null);
    }

    public CustomView(Context context, AttributeSet attrs){
        super(context, attrs);

        init(attrs);
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    @TargetApi(21)
    public CustomView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes ){
        super(context, attrs, defStyleAttr, defStyleRes);

        init(attrs);
    }

    private void init(@Nullable AttributeSet set){
        mRect = new Rect();
        mPaintRect = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintTable = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintRect = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas){
        int canvasWidth = this.getWidth();
        int canvasHeight = this.getHeight();

        //query database for table
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://efficienseat26db.cgtr1q4gushl.us-east-1.rds.amazonaws.com", "efficienseat26", "Pair26Victory");
            Statement stmt = conn.createStatement();
            String query = "select * from efficienseat26_mysql_db.Hampshire;";
  