package com.example.budgettracker.ui.statistics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.budgettracker.R;

import java.util.List;

public class LineChartView extends View {
    private List<Double> trendValues;
    private final Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public LineChartView(Context context) {
        super(context);
        init();
    }

    public LineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        linePaint.setColor(0xFF10B981);
        linePaint.setStrokeWidth(6f);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeCap(Paint.Cap.ROUND);

        fillPaint.setColor(0x3310B981);
        fillPaint.setStyle(Paint.Style.FILL);

        pointPaint.setColor(0xFF0E3A2F);
        pointPaint.setStyle(Paint.Style.FILL);

        textPaint.setColor(0xFF64748B);
        textPaint.setTextSize(26f);

        gridPaint.setStrokeWidth(2f);
        gridPaint.setStyle(Paint.Style.STROKE);
    }

    public void setTrendData(List<Double> values) {
        this.trendValues = values;
        invalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        int accentColor = ContextCompat.getColor(getContext(), R.color.income_green);
        int textColor = ContextCompat.getColor(getContext(), R.color.text_secondary);
        int gridColor = ContextCompat.getColor(getContext(), R.color.divider_color);

        linePaint.setColor(accentColor);
        fillPaint.setColor((accentColor & 0x00FFFFFF) | 0x33000000);
        pointPaint.setColor(accentColor);
        textPaint.setColor(textColor);
        gridPaint.setColor(gridColor);

        if (trendValues == null || trendValues.isEmpty()) {
            textPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("No trend data available", getWidth() / 2f, getHeight() / 2f, textPaint);
            return;
        }

        int width = getWidth();
        int height = getHeight();
        float padding = 50f;
        float usableWidth = width - (padding * 2);
        float usableHeight = height - (padding * 2);

        double maxVal = 1.0;
        for (double v : trendValues) {
            if (v > maxVal) maxVal = v;
        }

        canvas.drawLine(padding, padding, width - padding, padding, gridPaint);
        canvas.drawLine(padding, height / 2f, width - padding, height / 2f, gridPaint);
        canvas.drawLine(padding, height - padding, width - padding, height - padding, gridPaint);

        float stepX = trendValues.size() > 1 ? usableWidth / (trendValues.size() - 1) : usableWidth;

        Path linePath = new Path();
        Path fillPath = new Path();

        float firstX = padding;
        float firstY = height - padding - (float)((trendValues.get(0) / maxVal) * usableHeight);

        linePath.moveTo(firstX, firstY);
        fillPath.moveTo(firstX, height - padding);
        fillPath.lineTo(firstX, firstY);

        float prevX = firstX;

        for (int i = 1; i < trendValues.size(); i++) {
            float nextX = padding + (i * stepX);
            float nextY = height - padding - (float)((trendValues.get(i) / maxVal) * usableHeight);

            linePath.lineTo(nextX, nextY);
            fillPath.lineTo(nextX, nextY);

            prevX = nextX;
        }

        fillPath.lineTo(prevX, height - padding);
        fillPath.close();

        canvas.drawPath(fillPath, fillPaint);
        canvas.drawPath(linePath, linePaint);

        for (int i = 0; i < trendValues.size(); i++) {
            float px = padding + (i * stepX);
            float py = height - padding - (float)((trendValues.get(i) / maxVal) * usableHeight);
            canvas.drawCircle(px, py, 6f, pointPaint);
        }
    }
}
