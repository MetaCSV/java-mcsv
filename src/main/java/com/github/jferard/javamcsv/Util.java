/*
 * java-mcsv - A MetaCSV library for Java
 *     Copyright (C) 2020 J. Férard <https://github.com/jferard>
 *
 * This file is part of java-mcsv.
 *
 * java-mcsv is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * java-mcsv is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 *  for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses />.
 */

package com.github.jferard.javamcsv;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Util {
    public static final String CRLF = "\r\n";
    public static final Charset ASCII_CHARSET = Charset.forName("US-ASCII");
    public static Charset UTF_8_CHARSET = Charset.forName("UTF-8");
    public static String UTF_8_CHARSET_NAME = "UTF-8";

    public static List<String> parse(String s) {
        List<String> parts = new ArrayList<String>();
        StringBuilder cur = new StringBuilder();
        int i = 0;
        boolean backslash = false;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (backslash) {
                if (c != '\\' && c != '/') {
                    cur.append('\\');
                }
                cur.append(c);
                backslash = false;
            } else {
                if (c == '\\') {
                    backslash = true;
                } else if (c == '/') {
                    parts.add(cur.toString());
                    cur = new StringBuilder();
                } else {
                    cur.append(c);
                }
            }
            i++;
        }
        parts.add(cur.toString());
        return parts;
    }

    /**
     * Escape values and join then with a slash
     * @param out the output
     * @param values the values
     * @throws IOException
     */
    public static void render(Appendable out, String... values) throws IOException {
        int len = getActualLength(values);
        if (len == 0) {
            return;
        }
        Util.render_escaped(out, values[0]);
        for (int i = 1; i < len; i++) {
            out.append('/');
            Util.render_escaped(out, values[i]);
        }
    }

    private static int getActualLength(String[] values) {
        for (int last = values.length - 1; last >= 0; last--) {
            if (!values[last].isEmpty()) {
                return last + 1;
            }
        }
        return 0;
    }

    private static void render_escaped(Appendable out, String value) throws IOException {
        for (int i=0; i<value.length(); i++) {
            char c = value.charAt(i);
            if (c == '/' || c == '\\') {
                out.append('\\');
            }
            out.append(c);
        }
    }

    public static String render(String... values) throws IOException {
        StringBuilder sb = new StringBuilder();
        Util.render(sb, values);
        return sb.toString();
    }

    public static String escapeLineTerminator(String lineTerminator) {
        if (lineTerminator.equals("\r\n")) {
            return "\\r\\n";
        } else if (lineTerminator.equals("\n")) {
            return "\\n";
        } else if (lineTerminator.equals("\r")) {
            return "\\r";
        } else {
            return lineTerminator;
        }
    }

    public static String unescapeLineTerminator(String lineTerminatorRepr) {
        if (lineTerminatorRepr.equals("\\r\\n")) {
            return "\r\n";
        }
        if (lineTerminatorRepr.equals("\\n")) {
            return "\n";
        }
        if (lineTerminatorRepr.equals("\\r")) {
            return "\r";
        } else {
            return lineTerminatorRepr;
        }
    }

    public static File withExtension(File csvFile, String newExtension) {
        String absolutePath = csvFile.getAbsolutePath();
        int dotIndex = absolutePath.lastIndexOf(".");
        if (dotIndex == -1) {
            return new File(absolutePath + newExtension);
        } else {
            return new File(absolutePath.substring(0, dotIndex) + newExtension);
        }
    }

    public static String replaceChar(String text, String ch, String newCh) {
        if (ch == null || ch.isEmpty()) {
            return text;
        } else {
            return text.replace(ch, newCh);
        }
    }

    public static String getLocaleString(Locale locale) {
        return locale.getLanguage() + "_" + locale.getCountry();
    }

    public static List<String> header(MetaCSVRecord headerRecord) {
        List<String> ret = new ArrayList<String>(headerRecord.size());
        for (Object f : headerRecord) {
            ret.add(f.toString());
        }
        return ret;
    }

    public static String join(String[] chunks, String delimiter) {
        if (chunks.length == 0) {
            return "";
        } else if (chunks.length == 1) {
            return chunks[0];
        } else {
            StringBuilder sb = new StringBuilder(chunks[0]);
            for (int i = 1; i < chunks.length; i++) {
                sb.append(delimiter).append(chunks[i]);
            }
            return sb.toString();
        }
    }

    public static String formatLong(long n, String thousandsSeparator) {
        String text = Long.toString(n);
        if (thousandsSeparator == null || thousandsSeparator.isEmpty()) {
            return text;
        }
        boolean isNegative = n < 0;
        int len = text.length();
        StringBuilder ret = new StringBuilder();
        appendIntegerPart(ret, text, isNegative, len, thousandsSeparator);
        return ret.toString();
    }

    public static String formatDouble(double d, String thousandsSeparator,
                                      String decimalSeparator) {
        String text = Double.toString(d);
        boolean isNegative = d < 0;
        return formatNumber(text, isNegative, thousandsSeparator, decimalSeparator);
    }

    public static String formatBigDecimal(BigDecimal bd, String thousandsSeparator,
                                          String decimalSeparator) {
        String text = bd.toString();
        if (!text.contains(".")) {
            text += ".0";
        } else {
            int i = text.length() - 1;
            while (i >= 0 && text.charAt(i) == '0') {
                i--;
            }
            if (text.charAt(i) == '.') {
                text = text.substring(0, i + 2);
            } else {
                text = text.substring(0, i + 1);
            }
        }
        boolean isNegative = bd.signum() == -1;
        return formatNumber(text, isNegative, thousandsSeparator, decimalSeparator);
    }

    private static String formatNumber(String text, boolean isNegative, String thousandsSeparator,
                                       String decimalSeparator) {
        boolean defaultThousandsSeparator =
                thousandsSeparator == null || thousandsSeparator.isEmpty();
        boolean defaultDecimalSeparator = decimalSeparator == null || decimalSeparator.equals(".");
        if (defaultThousandsSeparator && defaultDecimalSeparator) {
            return text;
        }
        int sepIndex = text.indexOf(".");
        StringBuilder ret = new StringBuilder();
        if (defaultThousandsSeparator) {
            ret.append(text, 0, sepIndex);
        } else {
            appendIntegerPart(ret, text, isNegative, sepIndex, thousandsSeparator);
        }
        if (defaultDecimalSeparator) {
            ret.append(text, sepIndex, text.length());
        } else {
            ret.append(decimalSeparator);
            ret.append(text, sepIndex + 1, text.length());
        }
        return ret.toString();
    }

    public static void appendIntegerPart(StringBuilder ret, String text, boolean isNegative,
                                         int limit, String thousandsSeparator) {
        int s;
        int t;
        if (isNegative) {
            s = 1;
            t = ((limit - 1) % 3) + 1;
            if (t == 1) {
                t = 4;
            }
            ret.append("-");
        } else {
            s = 0;
            t = limit % 3;
            if (t == 0) {
                t = 3;
            }
        }
        while (t < limit) {
            ret.append(text, s, t);
            ret.append(thousandsSeparator);
            s = t;
            t += 3;
        }
        ret.append(text, s, t);
    }

    public static long parseLong(String s, String thousandsSeparator) {
        String text;
        if (thousandsSeparator == null || thousandsSeparator.isEmpty()) {
            text = s;
        } else {
            text = Util.replaceChar(s, thousandsSeparator, "");
        }
        return Long.parseLong(text);
    }

    public static double parseDouble(String s, String thousandsSeparator,
                                     String decimalSeparator) {
        String text = normalizeText(s, thousandsSeparator, decimalSeparator);
        return Double.parseDouble(text);
    }

    private static String normalizeText(String s, String thousandsSeparator,
                                        String decimalSeparator) {
        String text;
        if (thousandsSeparator == null || thousandsSeparator.isEmpty()) {
            text = s;
        } else {
            text = Util.replaceChar(s, thousandsSeparator, "");
        }
        if (!(decimalSeparator == null || decimalSeparator.equals("."))) {
            text = Util.replaceChar(text, decimalSeparator, ".");
        }
        return text;
    }

    public static BigDecimal parseBigDecimal(String s, String thousandsSeparator,
                                             String decimalSeparator) {
        String text = normalizeText(s, thousandsSeparator, decimalSeparator);
        return new BigDecimal(text);
    }

    public static Locale getLocale(String locale) {
        Locale instance;
        String[] s = locale.split("_");
        if (s.length == 2) {
            instance = new Locale(s[0], s[1]);
        } else {
            instance = new Locale(s[0]);
        }
        return instance;
    }
}
