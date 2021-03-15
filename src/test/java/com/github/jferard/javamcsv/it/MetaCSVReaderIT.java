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

package com.github.jferard.javamcsv.it;

import com.github.jferard.javamcsv.DataType;
import com.github.jferard.javamcsv.description.FieldDescription;
import com.github.jferard.javamcsv.processor.FieldProcessor;
import com.github.jferard.javamcsv.MetaCSVDataException;
import com.github.jferard.javamcsv.MetaCSVMetaData;
import com.github.jferard.javamcsv.MetaCSVParseException;
import com.github.jferard.javamcsv.MetaCSVReadException;
import com.github.jferard.javamcsv.MetaCSVReader;
import com.github.jferard.javamcsv.MetaCSVReaderBuilder;
import com.github.jferard.javamcsv.MetaCSVRecord;
import com.github.jferard.javamcsv.description.ObjectFieldDescription;
import com.github.jferard.javamcsv.ObjectTypeParser;
import com.github.jferard.javamcsv.ReadError;
import com.github.jferard.javamcsv.TestHelper;
import com.github.jferard.javamcsv.Util;
import org.junit.Assert;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MetaCSVReaderIT {
    public static <T> List<String> toRep(Iterable<T> iterable) {
        List<String> list = new ArrayList<String>();
        for (T e : iterable) {
            list.add(e.toString() + " (" + e.getClass() + ")");
        }
        return list;
    }

    @Test
    public void testMeta()
            throws IOException, MetaCSVParseException, MetaCSVReadException, MetaCSVDataException {
        InputStream is =
                TestHelper.getResourceAsStream("meta_csv.mcsv");
        InputStream metaIs =
                TestHelper.getResourceAsStream("meta_csv.mcsv");
        MetaCSVReader reader = MetaCSVReader.create(is, metaIs);
        MetaCSVMetaData metaData = reader.getMetaData();
        Assert.assertEquals(TestHelper.stringDescription(metaData.getDescription(0)), "text");
        Assert.assertEquals(TestHelper.stringDescription(metaData.getDescription(1)), "text");
        Assert.assertEquals(TestHelper.stringDescription(metaData.getDescription(2)), "object");
        Iterator<MetaCSVRecord> iterator = reader.iterator();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(
                Arrays.asList("domain", "key", "value"), iterator.next().toList());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(
                Arrays.asList("file", "encoding", "utf-8"), iterator.next().toList());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(
                Arrays.asList("file", "line_terminator", "\\r\\n"),
                iterator.next().toList());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(
                Arrays.asList("csv", "delimiter", ","), iterator.next().toList());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(
                Arrays.asList("csv", "double_quote", "true"), iterator.next().toList());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(
                Arrays.asList("csv", "quote_char", "\""), iterator.next().toList());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(
                Arrays.asList("data", "col/0/type", "text"), iterator.next().toList());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(
                Arrays.asList("data", "col/1/type", "text"), iterator.next().toList());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(
                Arrays.asList("data", "col/2/type", "object"), iterator.next().toList());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testLongFile()
            throws IOException, MetaCSVParseException, URISyntaxException, MetaCSVReadException,
            MetaCSVDataException {
        File f = TestHelper.getResourceAsFile("20201001-bal-216402149.csv");
        MetaCSVReader reader;
        reader = MetaCSVReader.create(f);
        MetaCSVMetaData metaData = reader.getMetaData();
        Assert.assertEquals(TestHelper.stringDescription(metaData.getDescription(3)), "integer");
        Assert.assertEquals(TestHelper.stringDescription(metaData.getDescription(7)), "float//.");
        Assert.assertEquals(TestHelper.stringDescription(metaData.getDescription(8)), "float//.");
        Assert.assertEquals(TestHelper.stringDescription(metaData.getDescription(9)), "float//.");
        Assert.assertEquals(TestHelper.stringDescription(metaData.getDescription(10)), "float//.");
        Assert.assertEquals(TestHelper.stringDescription(metaData.getDescription(12)),
                "date/yyyy-MM-dd");
        Iterator<MetaCSVRecord> iterator = reader.iterator();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(
                Arrays.asList("cle_interop", "uid_adresse", "voie_nom", "numero", "suffixe",
                        "commune_nom", "position", "x", "y", "long", "lat", "source",
                        "date_der_maj", "refparc", "voie_nom_eu", "complement"),
                iterator.next().toList());
        Assert.assertTrue(iterator.hasNext());
        Date date = getDate(2020, Calendar.JUNE, 11);
        Assert.assertEquals(
                Arrays.<Object>asList("64214_0010_00700", null, "Route du Pays de Soule", 700L,
                        null,
                        "Espès-undurein", "entrée", 385432.96, 6250383.75,
                        -0.8748110149745267, 43.28315047649357, "Commune de Espès-undurein",
                        date, "ZB0188", "Xiberoko errepidea", null),
                iterator.next().toList());
        while (iterator.hasNext()) {
            iterator.next();
        }
    }

    private Date getDate(int year, int month, int day) {
        Calendar c = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.US);
        c.setTimeInMillis(0);
        c.set(year, month, day, 0, 0, 0);
        return c.getTime();
    }

    private Date getDatetime(int year, int month, int day, int hour, int minute, int second) {
        Calendar c = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.US);
        c.setTimeInMillis(0);
        c.set(year, month, day, hour, minute, second);
        return c.getTime();
    }

    @Test
    public void testExample()
            throws IOException, MetaCSVParseException, URISyntaxException, MetaCSVReadException,
            MetaCSVDataException {
        File f = TestHelper.getResourceAsFile("example.csv");
        MetaCSVReader reader;
        reader = MetaCSVReader.create(f);
        MetaCSVMetaData metaData = reader.getMetaData();
        Assert.assertEquals(TestHelper.stringDescription(metaData.getDescription(1)),
                "date/yyyy-MM-dd");
        Assert.assertEquals(TestHelper.stringDescription(metaData.getDescription(2)), "integer");
        Iterator<MetaCSVRecord> iterator = reader.iterator();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(
                Arrays.asList("name", "date", "count"), iterator.next().toList());
        Assert.assertTrue(iterator.hasNext());
        Date date = getDate(2020, Calendar.NOVEMBER, 21);
        Assert.assertEquals(Arrays.<Object>asList("foo", date, 15L),
                iterator.next().toList());
        Assert.assertTrue(iterator.hasNext());
        date = getDate(2020, Calendar.NOVEMBER, 22);
        Assert.assertEquals(Arrays.<Object>asList("bar", date, -8L),
                iterator.next().toList());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void test()
            throws IOException, MetaCSVReadException, MetaCSVDataException, MetaCSVParseException {
        InputStream csvIn =
                new URL("https://raw.githubusercontent.com/MetaCSV/MetaCSV/main/examples/example1.csv")
                        .openStream();
        try {
            InputStream metaIn =
                    new URL("https://raw.githubusercontent.com/MetaCSV/MetaCSV/main/examples/example1.mcsv")
                            .openStream();
            try {
                MetaCSVReader reader =
                        new MetaCSVReaderBuilder().csvIn(csvIn).metaIn(metaIn).objectParser(
                                new ObjectTypeParser() {
                                    @Override
                                    public FieldDescription<?> parse(
                                            final List<String> parameters) {
                                        if (parameters.size() > 0 &&
                                                parameters.get(0).equals("URL")) {
                                            return getURLFieldDescription(parameters);
                                        } else {
                                            return new ObjectFieldDescription(parameters);
                                        }
                                    }
                                }).timeZone(TimeZone.getTimeZone("UTC")).build();
                Iterator<MetaCSVRecord> it = reader.iterator();

                Assert.assertTrue(it.hasNext());
                MetaCSVRecord headerRecord = it.next();
                checkHeaderRecord(headerRecord);

                Assert.assertTrue(it.hasNext());
                final MetaCSVRecord firstRecord = it.next();
                checkFirstRecord(firstRecord);

                Assert.assertTrue(it.hasNext());
                final MetaCSVRecord secondRecord = it.next();
                checkSecondRecord(secondRecord);

                Assert.assertTrue(it.hasNext());
                final MetaCSVRecord thirdRecord = it.next();
                checkThirdRecord(thirdRecord);

                Assert.assertFalse(it.hasNext());
            } finally {
                metaIn.close();
            }
        } finally {
            csvIn.close();
        }
    }

    private void checkHeaderRecord(MetaCSVRecord headerRecord) throws MetaCSVReadException {
        Assert.assertEquals(
                Arrays.asList("a boolean", "a currency", "another currency", "a date",
                        "a datetime", "a decimal", "a float",
                        "an integer", "a percentage", "another percentage", "a text",
                        "an URL"),
                headerRecord.toList());
        Assert.assertEquals("a boolean", headerRecord.getText(0));
        Assert.assertEquals("a currency", headerRecord.getText(1));
        Assert.assertEquals("another currency", headerRecord.getText(2));
        Assert.assertEquals("a date", headerRecord.getText(3));
        Assert.assertEquals("a datetime", headerRecord.getText(4));
        Assert.assertEquals("a decimal", headerRecord.getText(5));
        Assert.assertEquals("a float", headerRecord.getText(6));
        Assert.assertEquals("an integer", headerRecord.getText(7));
        Assert.assertEquals("a percentage", headerRecord.getText(8));
        Assert.assertEquals("another percentage", headerRecord.getText(9));
        Assert.assertEquals("a text", headerRecord.getText(10));
        Assert.assertEquals("an URL", headerRecord.getText(11));
    }

    private void checkFirstRecord(MetaCSVRecord firstRecord)
            throws MalformedURLException, MetaCSVReadException {
        Assert.assertEquals(Arrays.<Object>asList(true, 5L, new BigDecimal("10.3"),
                getDate(1975, Calendar.APRIL, 20),
                getDatetime(1975, Calendar.APRIL, 20, 13, 45, 0), new BigDecimal("10.3"),
                1232.0d, 56L, 0.56d, new BigDecimal("0.782"), "meta csv |",
                new URL("https://github.com/jferard")),
                firstRecord.toList());
        Assert.assertTrue(firstRecord.getBoolean(0));
        Assert.assertEquals(Long.valueOf(5), firstRecord.getInteger(1));
        Assert.assertEquals(new BigDecimal("10.3"), firstRecord.getDecimal(2));
        Assert.assertEquals(getDate(1975, Calendar.APRIL, 20), firstRecord.getDate(3));
        Assert.assertEquals(getDatetime(1975, Calendar.APRIL, 20, 13, 45, 0),
                firstRecord.getDatetime(4));
        Assert.assertEquals(new BigDecimal("10.3"), firstRecord.getDecimal(5));
        Assert.assertEquals(1232.0d, firstRecord.getFloat(6), 0.01);
        Assert.assertEquals(Long.valueOf(56), firstRecord.getInteger(7));
        Assert.assertEquals(0.56d, firstRecord.getFloat(8), 0.01);
        Assert.assertEquals(new BigDecimal("0.782"), firstRecord.getDecimal(9));
        Assert.assertEquals("meta csv |", firstRecord.getText(10));
        Assert.assertEquals(new URL("https://github.com/jferard"), firstRecord.getObject(11));
    }

    private void checkSecondRecord(MetaCSVRecord secondRecord) throws MetaCSVReadException {
        Assert.assertEquals(Arrays.asList(null, 17L, new BigDecimal("10.8"),
                getDate(2017, Calendar.NOVEMBER, 21), null, new BigDecimal("1.03"), -2.5d,
                76L, 0.58d, new BigDecimal("0.182"), "\"java-mcsv\"", null),
                secondRecord.toList());
        Assert.assertNull(secondRecord.getBoolean(0));
        Assert.assertEquals(Long.valueOf(17), secondRecord.getInteger(1));
        Assert.assertEquals(new BigDecimal("10.8"), secondRecord.getDecimal(2));
        Assert.assertEquals(getDate(2017, Calendar.NOVEMBER, 21), secondRecord.getDate(3));
        Assert.assertNull(secondRecord.getDatetime(4));
        Assert.assertEquals(new BigDecimal("1.03"), secondRecord.getDecimal(5));
        Assert.assertEquals(-2.5d, secondRecord.getFloat(6), 0.01);
        Assert.assertEquals(Long.valueOf(76), secondRecord.getInteger(7));
        Assert.assertEquals(0.58d, secondRecord.getFloat(8), 0.01);
        Assert.assertEquals(new BigDecimal("0.182"), secondRecord.getDecimal(9));
        Assert.assertEquals("\"java-mcsv\"", secondRecord.getText(10));
        Assert.assertNull(null, secondRecord.getObject(11));
    }

    private void checkThirdRecord(final MetaCSVRecord thirdRecord)
            throws MetaCSVReadException {
        Assert.assertEquals(Arrays.asList(false, new ReadError("7", "currency/post/€/integer"), new BigDecimal("17.8"),
                getDate(2003, Calendar.JUNE, 3), null, new BigDecimal("32.5"), -2456.5d,
                1786L, 0.85d, new BigDecimal("0.128"), "py-mcsv", null),
                thirdRecord.toList());
        Assert.assertFalse(thirdRecord.getBoolean(0));
        Assert.assertThrows(MetaCSVReadException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                thirdRecord.getObject(1);
            }
        });
        Assert.assertEquals(new BigDecimal("17.8"), thirdRecord.getDecimal(2));
        Assert.assertEquals(getDate(2003, Calendar.JUNE, 3), thirdRecord.getDate(3));
        Assert.assertNull(thirdRecord.getDatetime(4));
        Assert.assertEquals(new BigDecimal("32.5"), thirdRecord.getDecimal(5));
        Assert.assertEquals(-2456.5d, thirdRecord.getFloat(6), 0.01);
        Assert.assertEquals(Long.valueOf(1786), thirdRecord.getInteger(7));
        Assert.assertEquals(0.85d, thirdRecord.getFloat(8), 0.01);
        Assert.assertEquals(new BigDecimal("0.128"), thirdRecord.getDecimal(9));
        Assert.assertEquals("py-mcsv", thirdRecord.getText(10));
        Assert.assertNull(null, thirdRecord.getObject(11));
    }

    private FieldDescription<URL> getURLFieldDescription(final List<String> parameters) {
        return new FieldDescription<URL>() {
            @Override
            public void render(Appendable out)
                    throws IOException {
                Util.render(out, parameters.toArray(new String[]{}));
            }

            @Override
            public FieldProcessor<URL> toFieldProcessor(
                    final String nullValue) {
                return getURLFieldProcessor(nullValue);
            }

            @Override
            public Class<URL> getJavaType() {
                return URL.class;
            }

            @Override
            public DataType getDataType() {
                return DataType.OBJECT;
            }
        };
    }

    private FieldProcessor<URL> getURLFieldProcessor(final String nullValue) {
        return new FieldProcessor<URL>() {
            @Override
            public URL toObject(String text)
                    throws MetaCSVReadException {
                if (text == null || text.trim().equals(nullValue)) {
                    return null;
                }
                try {
                    return new URL(text.trim());
                } catch (MalformedURLException e) {
                    throw new MetaCSVReadException(e);
                }
            }

            @Override
            public String toString(URL url) {
                if (url == null) {
                    return nullValue;
                } else {
                    return url.toString();
                }
            }
        };
    }

}