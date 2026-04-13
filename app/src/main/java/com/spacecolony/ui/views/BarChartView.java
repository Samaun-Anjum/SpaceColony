package com.spacecolony.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

/**
 * Custom View that draws a coloured bar chart using Canvas.
 * Used for Statistics Visualization bonus feature.
 * No external library required.
 */
public class BarChartView extends View {

    private String[] labels  = new String[0];
    private float[]  values  = new float[0];
    private int[]    colors  = new int[0];
    private String   title   = "";

    private final Paint barPaint    = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint titlePaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint valuePaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint bgPaint     = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint gridPaint   = new Paint(Paint.ANTI_ALIAS_FLAG);

    public BarChartView(Context context) {
        super(context);
        init();
    }

    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BarChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Background
        bgPaint.setColor(0xFF0A1A2A);
        bgPaint.setStyle(Paint.Style.FILL);

        // Grid lines
        gridPaint.setColor(0x33FFFFFF);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(1f);

        // Label text under bars
        textPaint.setColor(0xFF80B0C0);
        textPaint.setTextSize(28f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(Typeface.MONOSPACE);

        // Value text on top of bars
        valuePaint.setColor(0xFFFFFFFF);
        valuePaint.setTextSize(26f);
        valuePaint.setTextAlign(Paint.Align.CENTER);
        valuePaint.setTypeface(Typeface.DEFAULT_BOLD);

        // Title text
        titlePaint.setColor(0xFF00E5FF);
        titlePaint.setTextSize(32f);
        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.DEFAULT_BOLD);
    }

    public void setData(String[] labels, float[] values, int[] colors) {
        this.labels = labels;
        this.values = values;
        this.colors = colors;
        invalidate();
    }

    public void setChartTitle(String title) {
        this.title = title;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getWidth();
        int h = getHeight();

        if (labels == null || labels.length == 0) return;

        // Background
        canvas.drawRoundRect(new RectF(0, 0, w, h), 16f, 16f, bgPaint);

        float titleHeight = title.isEmpty() ? 16f : 60f;
        float bottomPadding = 60f;  // space for labels
        float sidePadding = 24f;
        float topPadding = titleHeight + 16f;

        float chartH = h - topPadding - bottomPadding;
        float chartW = w - sidePadding * 2;

        // Draw title
        if (!title.isEmpty()) {
            canvas.drawText(title, w / 2f, 44f, titlePaint);
        }

        // Find max value for scaling
        float maxVal = 1f;
        for (float v : values) if (v > maxVal) maxVal = v;

        int n = labels.length;
        float barWidth = (chartW / n) * 0.6f;
        float gap      = (chartW / n) * 0.4f;

        // Draw horizontal grid lines (4 lines)
        gridPaint.setColor(0x22FFFFFF);
        for (int i = 1; i <= 4; i++) {
            float y = topPadding + chartH - (chartH * i / 4f);
            canvas.drawLine(sidePadding, y, w - sidePadding, y, gridPaint);
            // Grid value label
            textPaint.setTextAlign(Paint.Align.LEFT);
            textPaint.setColor(0x44FFFFFF);
            textPaint.setTextSize(22f);
            canvas.drawText(String.valueOf((int)(maxVal * i / 4)), sidePadding + 4, y - 4, textPaint);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setColor(0xFF80B0C0);
            textPaint.setTextSize(28f);
        }

        // Draw bars
        for (int i = 0; i < n; i++) {
            float barH = (values[i] / maxVal) * chartH;
            float left  = sidePadding + i * (chartW / n) + gap / 2f;
            float right = left + barWidth;
            float top   = topPadding + chartH - barH;
            float bottom = topPadding + chartH;

            // Bar colour
            barPaint.setColor(colors[i]);
            barPaint.setStyle(Paint.Style.FILL);

            // Draw rounded bar
            RectF rect = new RectF(left, top, right, bottom);
            canvas.drawRoundRect(rect, 8f, 8f, barPaint);

            // Subtle glow/highlight at top of bar
            barPaint.setColor(0x44FFFFFF);
            canvas.drawRoundRect(new RectF(left, top, right, top + 8f), 8f, 8f, barPaint);

            // Value on top of bar
            float cx = left + barWidth / 2f;
            if (values[i] > 0) {
                canvas.drawText(String.valueOf((int) values[i]), cx, top - 6f, valuePaint);
            }

            // Label below bar
            canvas.drawText(labels[i], cx, h - 16f, textPaint);
        }

        // Bottom axis line
        gridPaint.setColor(0x66FFFFFF);
        canvas.drawLine(sidePadding, topPadding + chartH,
                w - sidePadding, topPadding + chartH, gridPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minH = (int) (280 * getResources().getDisplayMetrics().density);
        int resolvedH = resolveSize(minH, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(resolvedH, MeasureSpec.EXACTLY));
    }
}
