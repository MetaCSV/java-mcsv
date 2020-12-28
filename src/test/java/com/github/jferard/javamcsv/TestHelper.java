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

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.junit.Assert;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestHelper {
    public static String UTF_8_CHARSET_NAME = "UTF-8";
    public static Charset UTF_8_CHARSET = Charset.forName("UTF-8");
    public static Charset ASCII_CHARSET = Charset.forName("US-ASCII");

    public static String render(FieldDescription<?> fd) throws IOException {
        Appendable sb = new StringBuilder();
        fd.render(sb);
        return sb.toString();
    }

    public static ByteArrayInputStream utf8InputStream(String s) throws
            UnsupportedEncodingException {
        return new ByteArrayInputStream(s.getBytes(UTF_8_CHARSET_NAME));
    }

    public static ByteArrayInputStream bomUtf8InputStream(String s) throws
            UnsupportedEncodingException {
        byte[] bytes = s.getBytes(UTF_8_CHARSET_NAME);
        byte[] bomBytes = new byte[bytes.length + 3];
        bomBytes[0] = (byte) 0xEF;
        bomBytes[1] = (byte) 0xBB;
        bomBytes[2] = (byte) 0xBF;
        System.arraycopy(bytes, 0, bomBytes, 3, bytes.length);
        return new ByteArrayInputStream(bomBytes);
    }

    public static CSVRecord createRecord(Object... values) throws IOException {
        CSVFormat format = CSVFormat.DEFAULT;
        Appendable out = new StringBuilder();
        CSVPrinter printer = new CSVPrinter(out, format);
        printer.printRecord(values);
        CSVParser parser = new CSVParser(new StringReader(out.toString()), format);
        return parser.iterator().next();
    }

    public static MetaCSVRecord createMetaRecord(Object... values) throws IOException {
        return new MetaCSVRecord(TestHelper.createRecord(values), Arrays.<Object>asList(values));
    }

    public static <T> List<T> toList(Iterable<T> iterable) {
        List<T> list = new ArrayList<T>();
        for (T e : iterable) {
            list.add(e);
        }
        return list;
    }

    public static String toString(final File file) throws IOException {
        StringBuilder ret = new StringBuilder();
        char[] buf = new char[1024];

        Reader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), Util.UTF_8_CHARSET));
        try {
            int r = reader.read(buf);
            while (r != -1) {
                ret.append(buf, 0, r);
                r = reader.read(buf);
            }
        } finally {
            reader.close();
        }
        return ret.toString();
    }

    public static void assertMetaEquals(MetaCSVRecord r1, MetaCSVRecord r2) {
        Assert.assertEquals(toList(r1), toList(r2));
    }
}
