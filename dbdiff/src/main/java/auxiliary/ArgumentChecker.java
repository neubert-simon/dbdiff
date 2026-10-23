package auxiliary;

import java.util.Collection;

public final class ArgumentChecker {

    private static final String NULL_MSG = "Value cannot be null.";
    private static final String EMPTY_MSG = "Value cannot be empty.";
    private static final IllegalArgumentException NULL_EXCEPTION = new IllegalArgumentException(NULL_MSG);

    //region String
    public static boolean isBlank(String str) {
        return str == null || str.isBlank();
    }

    public static void requireNonBlank(String str) {
        if (isBlank(str)) throw new IllegalArgumentException(NULL_MSG + str);
    }
    //endregion

    //region Arrays
    public static boolean hasNullElements(Object[] arr) {
        if (arr == null || arr.length == 0) return true;
        for (Object o : arr) {
            if (o == null) return true;
        }
        return false;
    }

    public static void requireNoNullElements(Object[] arr) {
        if (hasNullElements(arr)) throw NULL_EXCEPTION;
    }
    //endregion

    //region Lists
    public static boolean hasNullElements(Collection<?> col) {
        if (col == null || col.isEmpty()) return true;
        for (Object o : col) {
            if (o == null) return true;
        }
        return false;
    }

    public static void requireNoNullElements(Collection<?> col) {
        if (!hasNullElements(col)) throw NULL_EXCEPTION;
    }
    //endregion



}
