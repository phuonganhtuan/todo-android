package com.example.todo.common.chart;

import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class PieChart extends FrameLayout {

    Float total = 0f;

    public PieChart(Context context) {
        super(context);
    }

    public PieChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PieChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    ArrayList<PieSlice> pieSlices;

    public void setData(List<Float> data) {
        removeAllViews();
        pieSlices = new ArrayList<PieSlice>();
        if (data == null) {
            invalidate();
            return;
        }
        total = 0f;
        for (int i = 0; i < data.size(); i++) {
            total += data.get(i);
        }
        float startAngle = 0, sweepAngle;
        for (int i = 0; i < data.size(); i++) {

            sweepAngle = data.get(i) * (360f / total);

            PieSlice pieSlice = new PieSlice(getContext(), this);
            if (i == data.size() - 1) {
                pieSlice.isLastSlice = true;
            }

            pieSlice.startAngele = startAngle;
            pieSlice.sweepAngle = sweepAngle;
            addView(pieSlice);
            pieSlices.add(pieSlice);
            pieSlice.paint.setColor(ChartColor.INSTANCE.getChartColors().get(i % 6));

            startAngle += sweepAngle;
        }
    }

    RectF bounds;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float width = getMeasuredWidth();
        float height = getMeasuredHeight();

        float rectSize = Math.min(width, height);
        float widthDiff = (width - rectSize) / 2;
        float heightDiff = (height - rectSize) / 2;
        bounds = new RectF(widthDiff, heightDiff, rectSize + widthDiff, rectSize + heightDiff);

        if (pieSlices != null)
            for (PieSlice pieSlice : pieSlices) {
                pieSlice.oval = bounds;
            }
    }
}
