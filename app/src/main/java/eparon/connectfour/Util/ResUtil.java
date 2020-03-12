package eparon.connectfour.Util;

import java.lang.reflect.Field;

public class ResUtil {

    public static int getResID (String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

}
