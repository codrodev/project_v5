package dm.sime.com.kharetati.util;

/**
 * Created by Hasham on 8/23/2017.
 */

import java.lang.reflect.Field;
import android.content.Context;
import android.graphics.Typeface;

public final class FontsOverride {

  public static void setDefaultFont(Context context,
                                    String staticTypefaceFieldName) {
    final Typeface regular = Typeface.createFromAsset(context.getAssets(),"Dubai-Regular.ttf");
    replaceFont(staticTypefaceFieldName, regular);
  }

  protected static void replaceFont(String staticTypefaceFieldName,
                                    final Typeface newTypeface) {
    try {
      final Field staticField = Typeface.class.getDeclaredField(staticTypefaceFieldName);
      staticField.setAccessible(true);
      staticField.set(null, newTypeface);
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }
}
