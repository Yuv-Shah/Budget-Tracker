package com.example.budgettracker.ui.statistics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.budgettracker.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DonutChartView extends View {
    private Map<String, Double> categoryData;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint innerHolePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rectF = new RectF();

    private final List<SliceInfo> sliceInfos = new ArrayList<>();
    private OnSliceClickListener sliceClickListener;

    public interface OnSliceClickListener {
        void onSliceClick(String categoryName);
    }

    private static final int[] COLORS = {
        0xFF7C3AED, 0xFF2563EB, 0xFF059669, 0xFFEA580C, 0xFFEF4444, 0xFFCA8A04, 0xFF0E3A2F, 0xFFEC4899
    };

    private static class SliceInfo {
        String category;
        float startAngle;
        float sweepAngle;

        SliceInfo(String category, float startAngle, float sweepAngle) {
            this.category = category;
            this.startAngle = startAngle;
            this.sweepAngle = sweepAngle;
        }
    }

    public DonutChartView(Context context) {
        super(context);
        init();
    }

    public DonutChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        innerHolePaint.setStyle(Paint.Style.FILL);

        titlePaint.setTextSize(22f);
        titlePaint.setTextAlign(Paint.Align.CENTER);

        textPaint.setTextSize(28f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);
    }

    public void setData(Map<String, Double> data) {
        this.categoryData = data;
        invalidate();
    }

    public void setOnSliceClickListener(OnSliceClickListener listener) {
        this.sliceClickListener = listener;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        sliceInfos.clear();

        int cardSurfaceColor = ContextCompat.getColor(getContext(), R.color.card_surface);
        int textPrimaryColor = ContextCompat.getColor(getContext(), R.color.text_primary);
        int textSecondaryColor = ContextCompat.getColor(getContext(), R.color.text_secondary);

        innerHolePaint.setColor(cardSurfaceColor);
        titlePaint.setColor(textSecondaryColor);
        textPaint.setColor(textPrimaryColor);

        int width = getWidth();
        int height = getHeight();

        if (categoryData == null || categoryData.isEmpty()) {
            paint.setColor(0xFF334155);
            paint.setStyle(Paint.Style.FILL);
            float radius = Math.min(width, height) / 2f - 30f;
            canvas.drawCircle(width / 2f, height / 2f, radius, paint);
            canvas.drawCircle(width / 2f, height / 2f, radius * 0.55f, innerHolePaint);
            canvas.drawText("No Data", width / 2f, height / 2f + 8f, textPaint);
            return;
        }

        double total = 0.0;
        for (double val : categoryData.values()) {
            total += val;
        }
        if (total <= 0) {
            paint.setColor(0xFF334155);
            paint.setStyle(Paint.Style.FILL);
            float radius = Math.min(width, height) / 2f - 30f;
            canvas.drawCircle(width / 2f, height / 2f, radius, paint);
            canvas.drawCircle(width / 2f, height / 2f, radius * 0.55f, innerHolePaint);
            canvas.drawText("₹0.00", width / 2f, height / 2f + 8f, textPaint);
            return;
        }

        float padding = 20f;
        float diameter = Math.min(width, height) - (padding * 2);
        rectF.set(
            (width - diameter) / 2f,
            (height - diameter) / 2f,
            (width + diameter) / 2f,
            (height + diameter) / 2f
        );

        float startAngle = -90f; // Start from top
        int colorIndex = 0;

        paint.setStyle(Paint.Style.FILL);

        for (Map.Entry<String, Double> entry : categoryData.entrySet()) {
            float sweepAngle = (float) ((entry.getValue() / total) * 360f);
            paint.setColor(COLORS[colorIndex % COLORS.length]);
            
            // Draw pie slice
            canvas.drawArc(rectF, startAngle, sweepAngle, true, paint);
            
            // Store slice info for touch hit testing
            sliceInfos.add(new SliceInfo(entry.getKey(), startAngle, sweepAngle));

            startAngle += sweepAngle;
            colorIndex++;
        }

        // Draw inner hole for Donut Chart
        float outerRadius = diameter / 2f;
        float innerRadius = outerRadius * 0.58f;
        canvas.drawCircle(width / 2f, height / 2f, innerRadius, innerHolePaint);

        // Draw total text in center
        canvas.drawText("TOTAL EXPENSE", width / 2f, height / 2f - 12f, titlePaint);
        canvas.drawText(String.format(Locale.getDefault(), "₹%,.0f", total), width / 2f, height / 2f + 22f, textPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            performClick();
            float x = event.getX();
            float y = event.getY();
            int width = getWidth();
            int height = getHeight();
            float centerX = width / 2f;
            float centerY = height / 2f;

            float dx = x - centerX;
            float dy = y - centerY;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            float radius = Math.min(width, height) / 2f - 20f;

            if (distance <= radius && sliceClickListener != null) {
                double angle = Math.toDegrees(Math.atan2(dy, dx));
                if (angle < 0) {
                    angle += 360;
                }

                for (SliceInfo slice : sliceInfos) {
                    float normStart = slice.startAngle;
                    while (normStart < 0) normStart += 360;
                    float normEnd = normStart + slice.sweepAngle;

                    if (normStart <= normEnd) {
                        if (angle >= normStart && angle <= normEnd) {
                            sliceClickListener.onSliceClick(slice.category);
                            break;
                        }
                    } else {
                        if (angle >= normStart || angle <= normEnd) {
                            sliceClickListener.onSliceClick(slice.category);
                            break;
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
