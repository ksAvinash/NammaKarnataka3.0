package smartAmigos.com.nammakarnataka.fonts;

/**
 * Created by avinashk on 25/01/18.
 */


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;



@SuppressLint("AppCompatCustomView")
public class description_font_light extends TextView {

    public description_font_light(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public description_font_light(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public description_font_light(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/Caecilia_Light.otf" );
        setTypeface(tf);
    }

}