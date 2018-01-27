package smartAmigos.com.nammakarnataka.fonts;

/**
 * Created by avinashk on 16/01/18.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;



@SuppressLint("AppCompatCustomView")
public class kaushan_font extends TextView {

    public kaushan_font(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public kaushan_font(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public kaushan_font(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/Kaushan.otf" );
        setTypeface(tf);
    }

}