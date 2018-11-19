package es.eriktorr.katas.orders.domain.common;

import java.util.Optional;

/**
 * Description from Apache Commons Lang3:<br/>
 * <p>Operations on {@link java.lang.String} that are {@code null} safe.</p>
 */
public final class StringUtils {

    private static final String EMPTY_JSON = "{}";

    private StringUtils() {}

    public static char[] trimJsonToEmpty(String payload) {
        return Optional.ofNullable(trimToNull(payload))
                .orElse(EMPTY_JSON)
                .toCharArray();
    }

    private static String trimToNull(final String string) {
        final String trimmedString = trim(string);
        return isEmpty(trimmedString) ? null : trimmedString;
    }

    private static String trim(final String string) {
        return string == null ? null : string.trim();
    }

    private static boolean isEmpty(final CharSequence charSequence) {
        return charSequence == null || charSequence.length() == 0;
    }

}