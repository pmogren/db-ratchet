package com.commercehub.dbratchet.util

import java.util.regex.Pattern

/**
 * Created by jgelais on 5/15/2014.
 */
class FileUtil {
    @SuppressWarnings('DuplicateStringLiteral')
    static Pattern convertWildcardToRegex(String pattern) {
        return Pattern.compile(pattern.replaceAll(/\//, '\\/')
                                      .replaceAll(/\./, '\\.')
                                      .replaceAll(/\*/, '.*')
                                      .replaceAll(/\?/, '.'))
    }
}
