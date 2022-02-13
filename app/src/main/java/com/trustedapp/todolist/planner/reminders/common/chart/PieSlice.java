package com.trustedapp.todolist.planner.reminders.common.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 *
 */
public class PieSlice extends View {

    static final float D_TO_R = 0.0174532925f;
    RectF oval;
    float startAngele, sweepAngle;
    Paint paint;
    Paint whiteLinePaint;
    PieChart pieChart;

    public boolean isLastSlice = false;

    public PieSlice(Context context, AttributeSet attSet) {
        super(context, attSet);
    }

    public PieSlice(Context context, PieChart pieChart) {
        super(context);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.pieChart = pieChart;

        whiteLinePaint = new Paint(paint);
        whiteLinePaint.setColor(Color.WHITE);
        whiteLinePaint.setStrokeWidth(5f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawArc(oval, startAngele, sweepAngle, true, paint);
        drawLineBesideCir(canvas, startAngele);
        if (isLastSlice) {
            drawLineBesideCir(canvas, sweepAngle + startAngele);
        }
        canvas.save();
        canvas.restore();
    }

    private void drawLineBesideCir(Canvas canvas, float angel) {
        int sth = 1;
        if (angel % 360 > 180 && angel % 360 < 360) {
            sth = -1;
        }
        Point pieCenterPoint = new Point();
        pieCenterPoint.set(getWidth() / 2, getHeight() / 2);
        float lineToX = (float) (getHeight() / 2 + Math.cos(Math.toRadians(-angel)) * getWidth() / 2);
        float lineToY =
                (float) (getHeight() / 2 + sth * Math.abs(Math.sin(Math.toRadians(-angel))) * getHeight() / 2);
        canvas.drawLine(pieCenterPoint.x, pieCenterPoint.y, lineToX, lineToY, whiteLinePaint);
    }
}
