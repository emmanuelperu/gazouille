package fr.manumehdi.gazouille.views.span;

import android.content.Context;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

/**
 * Created by mehdi on 15/11/2015.
 */

public class HashTagSpan extends ClickableSpan {

    private Context context;
    private TextPaint paint;
    private OnHashTagClickListener onHashTagClickListener;

    public interface OnHashTagClickListener {
        void onClick(String hashTag);
    }

    public HashTagSpan(Context context) {
        super();
        this.context = context;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        paint = ds;
        // FIXME check null !!
        paint.setColor(ds.linkColor);
        paint.setARGB(255, 30, 144, 255);
    }

    @Override
    public void onClick(View view) {
        TextView textView = (TextView) view;
        Spanned spanned = (Spanned) textView.getText();
        int start = spanned.getSpanStart(this);
        int end = spanned.getSpanEnd(this);
        String hashTag = spanned.subSequence(start + 1, end).toString();
        if (onHashTagClickListener != null) {
            onHashTagClickListener.onClick(hashTag);
        }

        paint.bgColor = 0xFF0000;
        textView.invalidate();
    }

    public void setOnHashTagClickListener(OnHashTagClickListener onHashTagClickListener) {
        this.onHashTagClickListener = onHashTagClickListener;
    }
}