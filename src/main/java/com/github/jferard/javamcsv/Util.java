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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class Util {
    public static final String CRLF = "\r\n";
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

    public static void render(Appendable out, String... values) throws IOException {
        int len = getActualLength(values);
        if (len == 0) {
            return;
        }
        String first = values[0];
        out.append(Util.render(first));
        for (int i = 1; i < len; i++) {
            out.append('/');
            out.append(Util.render(values[i]));
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

    private static String render(String value) {
        return value.replace("\\", "\\\\").replace("/", "\\/");
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
}
