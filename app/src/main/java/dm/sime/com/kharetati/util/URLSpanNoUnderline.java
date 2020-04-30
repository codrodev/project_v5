package dm.sime.com.kharetati.util;

import android.text.TextPaint;
import android.text.style.URLSpan;

/**
 * Created by Imran on 3/26/2018.
 */
public class URLSpanNoUnderline extends URLSpan {
    public URLSpanNoUnderline(String url) {
        super(url);
    }

    @Override
    public void updateDrawState(TextPaint ds)
    {
        super.updateDrawState(ds);
        ds.setUnderlineText(false);
    }
}