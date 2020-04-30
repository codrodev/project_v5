package dm.sime.com.kharetati.util;

import java.util.ArrayList;

/**
 * Created by Imran on 10/4/2017.
 */

public class CaseInsensitiveArrayListString extends ArrayList<String> {
    @Override
    public boolean contains(Object o) {
        String paramStr = (String)o;
        for (String s : this) {
            if (paramStr.equalsIgnoreCase(s)) return true;
        }
        return false;
    }
}
