/**
 * Copyright © 2016-2026 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.common.data;

import com.google.common.base.Splitter;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.Strings;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static org.apache.commons.lang3.StringUtils.repeat;

/**
 * String utils.
 */
public class StringUtils {

    private static final int DEFAULT_TOKEN_LENGTH = 8;

    public static final SecureRandom RANDOM = new SecureRandom();

    public static final String EMPTY = "";

    public static final int INDEX_NOT_FOUND = -1;
    /**
     * Is empty.
     *
     * @param source source ({@link String})
     * @return the boolean result
     */

    public static boolean isEmpty(String source) {
        return source == null || source.isEmpty();
    }
    /**
     * Is blank.
     *
     * @param source source ({@link String})
     * @return the boolean result
     */

    public static boolean isBlank(String source) {
        return source == null || source.isEmpty() || source.trim().isEmpty();
    }
    /**
     * Is not empty.
     *
     * @param source source ({@link String})
     * @return the boolean result
     */

    public static boolean isNotEmpty(String source) {
        return source != null && !source.isEmpty();
    }
    /**
     * Is not blank.
     *
     * @param source source ({@link String})
     * @return the boolean result
     */

    public static boolean isNotBlank(String source) {
        return source != null && !source.isEmpty() && !source.trim().isEmpty();
    }
    /**
     * Not blank or default.
     *
     * @param src src ({@link String})
     * @param def def ({@link String})
     * @return {@link String}
     */

    public static String notBlankOrDefault(String src, String def) {
        return isNotBlank(src) ? src : def;
    }
    /**
     * Removes start.
     *
     * @param str str ({@link String})
     * @param remove remove ({@link String})
     * @return {@link String}
     */

    public static String removeStart(final String str, final String remove) {
        if (isEmpty(str) || isEmpty(remove)) {
            return str;
        }
        if (str.startsWith(remove)) {
            return str.substring(remove.length());
        }
        return str;
    }
    /**
     * Substring before.
     *
     * @param str str ({@link String})
     * @param separator separator ({@link String})
     * @return {@link String}
     */

    public static String substringBefore(final String str, final String separator) {
        if (isEmpty(str) || separator == null) {
            return str;
        }
        if (separator.isEmpty()) {
            return EMPTY;
        }
        final int pos = str.indexOf(separator);
        if (pos == INDEX_NOT_FOUND) {
            return str;
        }
        return str.substring(0, pos);
    }
    /**
     * Substring between.
     *
     * @param str str ({@link String})
     * @param open open ({@link String})
     * @param close close ({@link String})
     * @return {@link String}
     */

    public static String substringBetween(final String str, final String open, final String close) {
        if (str == null || open == null || close == null) {
            return null;
        }
        final int start = str.indexOf(open);
        if (start != INDEX_NOT_FOUND) {
            final int end = str.indexOf(close, start + open.length());
            if (end != INDEX_NOT_FOUND) {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }
    /**
     * Obfuscate.
     *
     * @param input input ({@link String})
     * @param seenMargin seen margin
     * @param obfuscationChar obfuscation char
     * @param startIndexInclusive start index inclusive
     * @param endIndexExclusive end index exclusive
     * @return {@link String}
     */

    public static String obfuscate(String input, int seenMargin, char obfuscationChar,
                                   int startIndexInclusive, int endIndexExclusive) {

        String part = input.substring(startIndexInclusive, endIndexExclusive);
        String obfuscatedPart;
        if (part.length() <= seenMargin * 2) {
            obfuscatedPart = repeat(obfuscationChar, part.length());
        } else {
            obfuscatedPart = part.substring(0, seenMargin)
                    + repeat(obfuscationChar, part.length() - seenMargin * 2)
                    + part.substring(part.length() - seenMargin);
        }
        return input.substring(0, startIndexInclusive) + obfuscatedPart + input.substring(endIndexExclusive);
    }
    /**
     * Split.
     *
     * @param value value ({@link String})
     * @param maxPartSize max part size
     * @return {@link Iterable}
     */

    public static Iterable<String> split(String value, int maxPartSize) {
        return Splitter.fixedLength(maxPartSize).split(value);
    }
    /**
     * Equals ignore case.
     *
     * @param str1 str1 ({@link String})
     * @param str2 str2 ({@link String})
     * @return the boolean result
     */

    public static boolean equalsIgnoreCase(String str1, String str2) {
        return str1 == null ? str2 == null : str1.equalsIgnoreCase(str2);
    }
    /**
     * Join.
     *
     * @param keyArray key array
     * @param lwm2mSeparatorPath lwm2m separator path ({@link String})
     * @return {@link String}
     */

    public static String join(String[] keyArray, String lwm2mSeparatorPath) {
        return org.apache.commons.lang3.StringUtils.join(keyArray, lwm2mSeparatorPath);
    }
    /**
     * Trim to null.
     *
     * @param toString to string ({@link String})
     * @return {@link String}
     */

    public static String trimToNull(String toString) {
        return org.apache.commons.lang3.StringUtils.trimToNull(toString);
    }
    /**
     * Is none empty.
     *
     * @param str str ({@link String})
     * @return the boolean result
     */

    public static boolean isNoneEmpty(String str) {
        return org.apache.commons.lang3.StringUtils.isNoneEmpty(str);
    }
    /**
     * Ends with.
     *
     * @param str str ({@link String})
     * @param suffix suffix ({@link String})
     * @return the boolean result
     */

    public static boolean endsWith(String str, String suffix) {
        return Strings.CS.endsWith(str, suffix);
    }
    /**
     * Has length.
     *
     * @param str str ({@link String})
     * @return the boolean result
     */

    public static boolean hasLength(String str) {
        return org.springframework.util.StringUtils.hasLength(str);
    }
    /**
     * Is none blank.
     *
     * @param str str
     * @return the boolean result
     */

    public static boolean isNoneBlank(String... str) {
        return org.apache.commons.lang3.StringUtils.isNoneBlank(str);
    }
    /**
     * Has text.
     *
     * @param str str ({@link String})
     * @return the boolean result
     */

    public static boolean hasText(String str) {
        return org.springframework.util.StringUtils.hasText(str);
    }
    /**
     * Default string.
     *
     * @param s s ({@link String})
     * @param defaultValue default value ({@link String})
     * @return {@link String}
     */

    public static String defaultString(String s, String defaultValue) {
        return Objects.toString(s, defaultValue);
    }
    /**
     * Is numeric.
     *
     * @param str str ({@link String})
     * @return the boolean result
     */

    public static boolean isNumeric(String str) {
        return org.apache.commons.lang3.StringUtils.isNumeric(str);
    }

    public static boolean equals(String str1, String str2) {
        return Strings.CS.equals(str1, str2);
    }
    /**
     * Equals any.
     *
     * @param string string ({@link String})
     * @param otherStrings other strings
     * @return the boolean result
     */

    public static boolean equalsAny(String string, String... otherStrings) {
        return equalsAny(string, Arrays.asList(otherStrings));
    }
    /**
     * Equals any.
     *
     * @param string string ({@link String})
     * @param otherStrings other strings ({@link List})
     * @return the boolean result
     */

    public static boolean equalsAny(String string, List<String> otherStrings) {
        for (String otherString : otherStrings) {
            if (equals(string, otherString)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Equals any ignore case.
     *
     * @param string string ({@link String})
     * @param otherStrings other strings
     * @return the boolean result
     */

    public static boolean equalsAnyIgnoreCase(String string, String... otherStrings) {
        for (String otherString : otherStrings) {
            if (equalsIgnoreCase(string, otherString)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Substring before last.
     *
     * @param str str ({@link String})
     * @param separator separator ({@link String})
     * @return {@link String}
     */

    public static String substringBeforeLast(String str, String separator) {
        return org.apache.commons.lang3.StringUtils.substringBeforeLast(str, separator);
    }
    /**
     * Substring after last.
     *
     * @param str str ({@link String})
     * @param sep sep ({@link String})
     * @return {@link String}
     */

    public static String substringAfterLast(String str, String sep) {
        return org.apache.commons.lang3.StringUtils.substringAfterLast(str, sep);
    }
    /**
     * Contained by any.
     *
     * @param searchString search string ({@link String})
     * @param strings strings
     * @return the boolean result
     */

    public static boolean containedByAny(String searchString, String... strings) {
        if (searchString == null) return false;
        for (String string : strings) {
            if (string != null && string.contains(searchString)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Contains.
     *
     * @param seq seq ({@link CharSequence})
     * @param searchSeq search seq ({@link CharSequence})
     * @return the boolean result
     */

    public static boolean contains(final CharSequence seq, final CharSequence searchSeq) {
        return Strings.CS.contains(seq, searchSeq);
    }

    
    /**
     * Contains0x00.
     *
     * @param s s ({@link String})
     * @return the boolean result
     */

    public static boolean contains0x00(final String s) {
        return s != null && s.contains("\u0000");
    }
    /**
     * Random numeric.
     *
     * @param length length
     * @return {@link String}
     */

    public static String randomNumeric(int length) {
        return RandomStringUtils.secure().nextNumeric(length);
    }
    /**
     * Random.
     *
     * @param length length
     * @return {@link String}
     */

    public static String random(int length) {
        return RandomStringUtils.secure().next(length);
    }
    /**
     * Random.
     *
     * @param length length
     * @param chars chars ({@link String})
     * @return {@link String}
     */

    public static String random(int length, String chars) {
        return RandomStringUtils.secure().next(length, chars);
    }
    /**
     * Random alphanumeric.
     *
     * @param count count
     * @return {@link String}
     */

    public static String randomAlphanumeric(int count) {
        return RandomStringUtils.secure().nextAlphanumeric(count);
    }
    /**
     * Random alphabetic.
     *
     * @param count count
     * @return {@link String}
     */

    public static String randomAlphabetic(int count) {
        return RandomStringUtils.secure().nextAlphabetic(count);
    }
    /**
     * Generate safe token.
     *
     * @param length length
     * @return {@link String}
     */

    public static String generateSafeToken(int length) {
        byte[] bytes = new byte[length];
        RANDOM.nextBytes(bytes);
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder.encodeToString(bytes);
    }
    /**
     * Generate safe token.
     *
     * @return {@link String}
     */

    public static String generateSafeToken() {
        return generateSafeToken(DEFAULT_TOKEN_LENGTH);
    }
    /**
     * Truncate.
     *
     * @param string string ({@link String})
     * @param maxLength max length
     * @return {@link String}
     */

    public static String truncate(String string, int maxLength) {
        return truncate(string, maxLength, n -> "...[truncated " + n + " symbols]");
    }
    /**
     * Truncate.
     *
     * @param string string ({@link String})
     * @param maxLength max length
     * @param truncationMarkerFunc truncation marker func ({@link Function})
     * @return {@link String}
     */

    public static String truncate(String string, int maxLength, Function<Integer, String> truncationMarkerFunc) {
        if (string == null || maxLength <= 0 || string.length() <= maxLength) {
            return string;
        }
        int truncatedSymbols = string.length() - maxLength;
        return string.substring(0, maxLength) + truncationMarkerFunc.apply(truncatedSymbols);
    }
    /**
     * Split by comma without quotes.
     *
     * @param value value ({@link String})
     * @return {@link List}
     */

    public static List<String> splitByCommaWithoutQuotes(String value) {
        List<String> splitValues = List.of(value.trim().split("\\s*,\\s*"));
        List<String> result = new ArrayList<>();
        char lastWayInputValue = '#';
        for (String str : splitValues) {
            char startWith = str.charAt(0);
            char endWith = str.charAt(str.length() - 1);

            // if first value is not quote, so we return values after split
            if (startWith != '\'' && startWith != '"') return splitValues;

            // if value is not in quote, so we return values after split
            if (startWith != endWith) return splitValues;

            // if different way values, so don't replace quote and return values after split
            if (lastWayInputValue != '#' && startWith != lastWayInputValue) return splitValues;

            result.add(str.substring(1, str.length() - 1));
            lastWayInputValue = startWith;
        }
        return result;
    }

}
