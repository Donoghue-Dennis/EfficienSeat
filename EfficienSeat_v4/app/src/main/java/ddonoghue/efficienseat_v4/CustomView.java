package ddonoghue.efficienseat_v4;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DDonoghue on 11/26/2017.z
 */

public class CustomView extends View {

    private Paint paintClaimed;
    private Paint paintOpen;
    private Paint paintText;
    int partySize = 0;
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
        paintClaimed = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintClaimed.setColor(getResources().getColor(R.color.claimed));
        paintOpen = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintOpen.setColor(getResources().getColor(R.color.open));
        paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(getResources().getColor(R.color.black));
        paintText.setTextSize(50);
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

        //offset between table and seats
        int i = 55;

        for(final Table temptable : tables){
            int temptableX = temptable.getTableX();
            int temptableY = temptable.getTableY();

            if(temptableX > inBoundX) inBoundX = temptableX;
            if(temptableY > inBoundY) inBoundY = temptableY;
        }

        ratioX = ((double)canvasWidth)/((double)inBoundX);
        ratioY = ((double)canvasHeight)/((double)inBoundY);
        if(ratioX<=ratioY)finalRatio = ratioX;
        else finalRatio = ratioY;

        for(final Table temptable : tables){
            boolean partyFits = temptable.getTableAvail() >= partySize;

            int x = ((int)(temptable.getTableX() * finalRatio)) + 100;
            int y = canvasHeight-((int)(temptable.getTableY() * finalRatio)) + 100;

            if(partyFits && temptable.getTableStatus()==0) {
                //Draw Table
                canvas.drawCircle(x, y, 50, paintOpen);

                //Draw Seats
                //Seat One
                if(temptable.getSeat1() == 0) {
                    canvas.drawCircle(x - i, y + i, 20, paintOpen);
                }else if(temptable.getSeat1() == 1){
                    canvas.drawCircle(x - i, y + i, 20, paintClaimed);
                }

                //Seat Two
                if(temptable.getSeat2() == 0) {
                    canvas.drawCircle(x + i, y + i, 20, paintOpen);
                }else if(temptable.getSeat2() == 1){
                    canvas.drawCircle(x + i, y + i, 20, paintClaimed);
                }

                //Seat Three
                if(temptable.getSeat3() == 0) {
                    canvas.drawCircle(x - i, y - i, 20, paintOpen);
                }else if(temptable.getSeat3() == 1){
                    canvas.drawCircle(x - i, y - i, 20, paintClaimed);
                }

                //Seat Four
                if(temptable.getSeat4() == 0) {
                    canvas.drawCircle(x + i, y - i, 20, paintOpen);
                }else if(temptable.getSeat4() == 1){
                    canvas.drawCircle(x + i, y - i, 20, paintClaimed);
                }
            }else{
                canvas.drawCircle(x, y, 50, paintClaimed);
            }
            canvas.drawText(temptable.getTableID() + "",x-25, y+13, paintText);
        }
    }

    public void setTable(Table sentTable) {
        this.tables.add(sentTable);
    }

    public void setTable(List<Table> sentTables) {
        for(Table table : sentTables)this.tables.add(table);

    }

    public void setPartySize(int partySize)
    {
        this.partySize=partySize;
    }
}


