package whunf.customclock;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by wxm on 2015/5/16 in PC
 */
public class MyClockView extends View implements Runnable {
    Paint paint;
    //加速参数
    double speed_factory = 1.0d;
    //分针扫过的角度
    int intermedia_degree = 6;
    Context context;
    Thread drawClockThread = new Thread(this);
    double[] pos = {350.0, 150.0};
    int origin_X = 350;
    int time_count = 0;
    int origin_Y = 400;
    int radium = 250;
    double tmpX, tmpY;
    double degree = 0;
    boolean flag = true;

    public MyClockView(Context context) {
        super(context);
        this.context = context;
    }

    public MyClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        drawClockThread.start();
    }

    public MyClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyClockView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawRing(canvas);
        drawClock(canvas, pos);
        drawTime(canvas, time_count);
    }

    @Override
    public void run() {
        while (flag) {
            if(degree<30){
                degree+=intermedia_degree;
            }else if(degree<180) {
                speed_factory+=0.2;
                degree+=intermedia_degree*speed_factory;
            }else  if(degree<330){
                speed_factory-=0.2;
                degree+=intermedia_degree*speed_factory;
            }else if(degree<360){
                degree+=intermedia_degree;
            }else {
                degree=0;
            }
            double Trans_degree = ((double) (degree / 180) * Math.PI);
            tmpX = radium * Math.sin(Trans_degree);
            tmpY = radium * Math.cos(Trans_degree);
            Log.i("X,Y", tmpX + "  " + tmpY);
            pos[0] = tmpX + origin_X;
            pos[1] = (origin_Y - tmpY);
            postInvalidate();
            try {
                Thread.sleep(1000 / 30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            time_count++;
        }
    }

    private void drawRing(Canvas canvas) {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeWidth(2);
        paint.setColor(Color.LTGRAY);
        paint.setStyle(Paint.Style.STROKE);
//        canvas.drawCircle(getWidth()/2,getHeight()/2,150,paint);
        canvas.drawCircle(350, 400, 250, paint);
    }

    private void drawClock(Canvas canvas, double[] pos) {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle((float) pos[0], (float) pos[1], 20, paint);
    }

    private void drawTime(Canvas canvas, int time_count) {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeWidth(3);
        paint.setTextSize(45);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GREEN);
        String min = time_count / 60 == 0 ? "0" : time_count / 60 + "";
        String sec = time_count % 60 < 10 ? "0" + time_count % 60 : time_count % 60 + "";
        String time = sec + ":" + min;
        float textWidth = paint.measureText(time);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float textHeight = fontMetrics.descent - fontMetrics.top;
        canvas.drawText(min + ":" + sec, 350 - textWidth / 2, 400 - textHeight / 2, paint);
    }
}
