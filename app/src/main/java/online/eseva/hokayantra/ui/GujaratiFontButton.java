package online.eseva.hokayantra.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.widget.Button;

import online.eseva.hokayantra.Global;

/**
 * Created by admin on 8/21/2014.
 */
public class GujaratiFontButton extends AppCompatButton {

    public GujaratiFontButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    public GujaratiFontButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GujaratiFontButton(Context context) {
        super(context);
        init();
    }

    public void init() {
        if(!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), Global.GUJ_FONT_PATH);
            setTypeface(tf, Typeface.NORMAL);
        }
    }

}
