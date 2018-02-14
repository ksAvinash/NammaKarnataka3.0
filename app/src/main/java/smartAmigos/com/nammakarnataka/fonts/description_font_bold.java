package smartAmigos.com.nammakarnataka.fonts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by avinashk on 08/02/18.
 */

@SuppressLint("AppCompatCustomView")
public class description_font_bold extends TextView {

    public description_font_bold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public description_font_bold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public description_font_bold(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/MuktaMahee-ExtraBold.ttf" );
        setTypeface(tf);
    }

}
