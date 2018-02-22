package ddonoghue.efficienseat_v4;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

/**
 * Created by DDonoghue on 11/26/2017.z
 */

public class CustomView extends View {
    //paint color settings
    private Paint paintClaimed;
    private Paint paintReserved;
    private Paint paintOpen;
    private Paint paintError;
    private Paint paintText;

    int partySize = 0;
    List<localTable> tables = new ArrayList<>();

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;

    public CustomView(Context context){
        super(context);

        init(null);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public CustomView(Context context, AttributeSet attrs){
        super(context, attrs);

        init(attrs);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);

        init(attrs);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    @TargetApi(21)
    public CustomView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes ){
        super(context, attrs, defStyleAttr, defStyleRes);

        init(attrs);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    private void init(@Nullable AttributeSet set){
        paintClaimed = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintClaimed.setColor(getResources().getColor(R.color.claimed));
        paintReserved = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintReserved.setColor(getResources().getColor(R.color.reserved));
        paintOpen = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintOpen.setColor(getResources().getColor(R.color.open));
        paintError = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintError.setColor(getResources().getColor(R.color.black));
        paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(getResources().getColor(R.color.black));
    }

    @Override
    protected void onDraw(Canvas canvas){
        int canvasWidth = this.getWidth() - 200;
        int canvasHeight = this.getHeight() - 200;
        int inBoundX = 0;
        int inBoundY = 0;
        double ratioX;
        double ratioY;
        double finalRatio;

        canvas.save();
        canvas.scale(mScaleFactor, mScaleFactor);

        for(final localTable temptable : tables){
            int temptableX = temptable.getTableX();
            int temptableY = temptable.getTableY();

            if(temptableX > inBoundX) inBoundX = temptableX;
            if(temptableY > inBoundY) inBoundY = temptableY;
        }

        ratioX = ((double)canvasWidth)/((double)inBoundX);
        ratioY = ((double)canvasHeight)/((double)inBoundY);
        if(ratioX<=ratioY)finalRatio = ratioX;
        else finalRatio = ratioY;

        for(final localTable tempTable : tables){
            boolean partyFits = tempTable.getTableAvail() >= partySize;

            int x = ((int)(tempTable.getTableX() * finalRatio)) + 100;
            int y = canvasHeight-((int)(tempTable.getTableY() * finalRatio)) + 100;

            drawTable(x,y,tempTable,canvas,partyFits);
        }

        canvas.restore();
    }

    public void drawTable(int x, int y, localTable table, Canvas canvas, Boolean partyFits){
        //table size factor
        int tSize = 50;

        //offset between table and seats
        float off = Math.round(tSize*1.65);
        float miniOff = Math.round(off*0.1);


        if(partyFits && ((table.getTableStatus()==0) || (table.getTableStatus()==1))) {
            //Draw Table
            canvas.drawCircle(x, y, tSize, paintOpen);
            paintText.setTextSize(tSize);
            canvas.drawText(table.getTableID() + "",x-25, y+13, paintText);

            //Draw Seats
            paintText.setTextSize(20);

            //Seat One
            int x1 = (int)(x + (off * sin(toRadians(table.getTableAngle()-135))));
            int y1 = (int)(y + (off * cos(toRadians(table.getTableAngle()-135))));

            if(table.getSeat1() == 0) {
                canvas.drawCircle(x1, y1, tSize/2, paintOpen);
            }else if(table.getSeat1() == 1){
                canvas.drawCircle(x1, y1, tSize/2, paintClaimed);
            }else if(table.getSeat1() == 2){
                canvas.drawCircle(x1, y1, tSize/2, paintReserved);
            }
            canvas.drawText( "s1",(x1 - miniOff), (y1 + miniOff), paintText);

            //Seat Two
            int x2 = (int)(x + (off * sin(toRadians(table.getTableAngle()+135))));
            int y2 = (int)(y + (off * cos(toRadians(table.getTableAngle()+135))));

            if(table.getSeat2() == 0) {
                canvas.drawCircle(x2, y2, tSize/2, paintOpen);
            }else if(table.getSeat2() == 1){
                canvas.drawCircle(x2, y2, tSize/2, paintClaimed);
            }else if(table.getSeat2() == 2){
                canvas.drawCircle(x2, y2, tSize/2, paintReserved);
            }
            canvas.drawText( "s2",(x2 - miniOff), (y2 + miniOff), paintText);

            //Seat Three
            int x3 = (int)(x + (off * sin(toRadians(table.getTableAngle()+45))));
            int y3 = (int)(y + (off * cos(toRadians(table.getTableAngle()+45))));

            if(table.getSeat3() == 0) {
                canvas.drawCircle(x3, y3, tSize/2, paintOpen);
            }else if(table.getSeat3() == 1){
                canvas.drawCircle(x3, y3, tSize/2, paintClaimed);
            }else if(table.getSeat3() == 2){
                canvas.drawCircle(x3, y3, tSize/2, paintReserved);
            }
            canvas.drawText( "s3",(x3 - miniOff), (y3 + miniOff), paintText);

            //Seat Four
            int x4 = (int)(x + (off * sin(toRadians(table.getTableAngle()-45))));
            int y4 = (int)(y + (off * cos(toRadians(table.getTableAngle()-45))));

            if(table.getSeat4() == 0) {
                canvas.drawCircle(x4, y4, tSize/2, paintOpen);
            }else if(table.getSeat4() == 1){
                canvas.drawCircle(x4, y4, tSize/2, paintClaimed);
            }else if(table.getSeat4() == 2){
                canvas.drawCircle(x4, y4, tSize/2, paintReserved);
            }
            canvas.drawText( "s4",(x4 - miniOff), (y4 + miniOff), paintText);

        }else if(table.getTableStatus()==2 || table.getTableAvail() == 0){
            canvas.drawCircle(x, y, 50, paintClaimed);
        }else{
            canvas.drawCircle(x, y, 50, paintError);
        }
    }

    public void addTable(localTable sentTable) {
        this.tables.add(sentTable);
    }

    public void addTables(List<localTable> sentTables) {
        for(localTable table : sentTables){
            if(!this.tables.contains(table))
                this.tables.add(table);
        }
    }

    public void setPartySize(int partySize)
    {
        this.partySize=partySize;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Let the ScaleGestureDetector inspect all events.
        mScaleDetector.onTouchEvent(ev);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

            invalidate();
            return true;
        }
    }
}