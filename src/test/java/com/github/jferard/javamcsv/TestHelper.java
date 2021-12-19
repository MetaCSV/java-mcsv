/*
 * java-mcsv - A MetaCSV library for Java
 *     Copyright (C) 2020-2021 J. Férard <https://github.com/jferard>
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

import com.github.jferard.javamcsv.processor.ProcessorProvider;
import com.github.jferard.javamcsv.processor.ReadFieldProcessor;
import com.github.jferard.javamcsv.description.FieldDescription;
import com.github.jferard.javamcsv.processor.FieldProcessor;
import com.github.jferard.javamcsv.processor.ReadProcessorProvider;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.junit.Assert;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class TestHelper {
    public static String UTF_8_CHARSET_NAME = Util.UTF_8_CHARSET_NAME;
    public static Charset UTF_8_CHARSET = Util.UTF_8_CHARSET;
    public static Charset ASCII_CHARSET = Util.ASCII_CHARSET;

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

    public static MetaCSVRecord createMetaRecord(final Object... values) throws IOException {
        Map<Integer, FieldProcessor<?>> processorByIndex = new HashMap<Integer, FieldProcessor<?>>();
        for (int c=0; c<values.length; c++) {
            final int col = c;
            processorByIndex.put(c, new FieldProcessor<Object>() {
                @Override
                public Object toObject(String text) throws MetaCSVReadException {
                    return values[col];
                }

                @Override
                public String toString(Object value) {
                    return value.toString();
                }

                @Override
                public String toCanonicalString(String text) throws MetaCSVReadException {
                    return values[col].toString();
                }

                @Override
                public Object cast(Object o) {
                    return o;
                }
            });
        }
        CSVRecord csvRecord = TestHelper.createRecord(values);
        ProcessorProvider provider = new ProcessorProvider(null, null) {
            @Override
            public FieldProcessor<?> getProcessor(final int c) {
                return new FieldProcessor<Object>() {
                    @Override
                    public Object toObject(String text) throws MetaCSVReadException {
                        return values[c];
                    }

                    @Override
                    public String toString(Object value) {
                        return value.toString();
                    }

                    @Override
                    public String toCanonicalString(String text) throws MetaCSVReadException {
                        return values[c].toString();
                    }

                    @Override
                    public Object cast(Object o) {
                        return o;
                    }
                };
            }
        };
        ReadProcessorProvider readProvider = new ReadProcessorProvider(null, null, null) {
            @Override
            public ReadFieldProcessor<?> getProcessor(final int c) {
                return new ReadFieldProcessor<Object>() {
                    @Override
                    public Object toObject(String text) {
                        return values[c];
                    }

                    @Override
                    public String toCanonicalString(String text) {
                        return values[c].toString();
                    }

                };
            }
        };
        return new MetaCSVRecord(csvRecord, provider, readProvider, null, TimeZone.getTimeZone("UTC"));
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

    public static void assertMetaEquals(MetaCSVRecord r1, MetaCSVRecord r2)
            throws MetaCSVReadException {
        Assert.assertEquals(r1.toList(), r2.toList());
    }

    public static InputStream getResourceAsStream(String name) {
        return TestHelper.class.getClassLoader().getResourceAsStream(name);
    }

    public static File getResourceAsFile(String name) throws URISyntaxException {
        return new File(
                TestHelper.class.getClassLoader().getResource(name).toURI().getPath());
    }

    public static String stringDescription(FieldDescription<?> description) throws IOException {
        StringBuilder sb = new StringBuilder();
        description.render(sb);
        return sb.toString();
    }

    public static byte[] readStream(InputStream in) throws IOException {
        ByteArrayOutputStream ret = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int count = in.read(buffer, 0, buffer.length);
        while (count != -1) {
            ret.write(buffer, 0, count);
            count = in.read(buffer, 0, buffer.length);
        }

        ret.flush();
        return ret.toByteArray();
    }

    public static String readReader(Reader r) throws IOException {
        StringBuilder ret = new StringBuilder();
        char[] buffer = new char[1024];
        int count = r.read(buffer, 0, buffer.length);
        while (count != -1) {
            ret.append(buffer, 0, count);
            count = r.read(buffer, 0, buffer.length);
        }
        return ret.toString();
    }

    public static InputStream boundedStream(final InputStream in, final long len) {
        return new InputStream() {
            long pos = 0;

            @Override
            public int read() throws IOException {
                if (pos >= len) {
                    return -1;
                }
                pos++;
                return in.read();
            }
        };
    }

    public static Reader boundedReader(final Reader reader, final long length) {
        return new Reader() {
            long pos = 0;

            @Override
            public int read(char[] cbuf, int off, int len) throws IOException {
                if (pos >= length) {
                    return -1;
                }
                pos++;
                int read = reader.read();
                if (read == -1) {
                    return -1;
                }
                cbuf[off] = (char) read;
                return 1;
            }

            @Override
            public void close() throws IOException {
                reader.close();
            }
        };
    }
}
