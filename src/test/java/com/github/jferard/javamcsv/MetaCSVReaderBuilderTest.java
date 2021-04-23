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

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

public class MetaCSVReaderBuilderTest {

    public static final String UTF_8 = "UTF-8";

    @Test
    public void test()
            throws IOException, MetaCSVReadException, MetaCSVDataException, MetaCSVParseException {
        ByteArrayInputStream is = TestHelper.utf8InputStream(
                "boolean,currency,date,datetime,float,integer,percentage,text\r\n" +
                        "T,$15,01/12/2020,NULL,\"10,000.5\",12 354,56.5%,Foo\r\n" +
                        "F,\"$-1,900.5\",NULL,2020-12-01 09:30:55,-520.8,-1 000,-12.8%,Bar\r\n");
        MetaCSVReader reader = new MetaCSVReaderBuilder().csvIn(is).metaCSVTriplets(
                Arrays.asList(
                        Arrays.asList("data", "null_value", "NULL"),
                        Arrays.asList("data", "col/0/type", "boolean/T/F"),
                        Arrays.asList("data", "col/1/type", "currency/pre/$/decimal/,/."),
                        Arrays.asList("data", "col/2/type", "date/dd\\/MM\\/yyyy"),
                        Arrays.asList("data", "col/3/type", "datetime/yyyy-MM-dd HH:mm:ss"),
                        Arrays.asList("data", "col/4/type", "float/,/."),
                        Arrays.asList("data", "col/5/type", "integer/ "),
                        Arrays.asList("data", "col/6/type", "percentage/post/%/float/,/.")
                )
        ).build();
        try {
            MetaCSVMetaData metaData = reader.getMetaData();
            Assert.assertEquals(TestHelper.stringDescription(metaData.getDescription(0)),
                    "boolean/T/F");
            Assert.assertEquals(TestHelper.stringDescription(metaData.getDescription(1)),
                    "currency/pre/$/decimal/,/.");
            Assert.assertEquals(TestHelper.stringDescription(metaData.getDescription(2)),
                    "date/dd\\/MM\\/yyyy");
            Assert.assertEquals(TestHelper.stringDescription(metaData.getDescription(3)),
                    "datetime/yyyy-MM-dd HH:mm:ss");
            Assert.assertEquals(TestHelper.stringDescription(metaData.getDescription(4)),
                    "float/,/.");
            Assert.assertEquals(TestHelper.stringDescription(metaData.getDescription(5)),
                    "integer/ ");
            Assert.assertEquals(TestHelper.stringDescription(metaData.getDescription(6)),
                    "percentage/post/%/float/,/.");
            Iterator<MetaCSVRecord> iterator = reader.iterator();
            Assert.assertTrue(iterator.hasNext());
            Assert.assertEquals(
                    Arrays.asList("boolean", "currency", "date", "datetime", "float", "integer",
                            "percentage", "text"), iterator.next().toList());
            Assert.assertTrue(iterator.hasNext());

            Calendar c = GregorianCalendar.getInstance(Locale.US);
            c.setTimeZone(Util.UTC_TIME_ZONE);
            c.setTimeInMillis(0);
            c.set(2020, Calendar.DECEMBER, 1, 0, 0, 0);
            Assert.assertEquals(
                    Arrays.asList(true, new BigDecimal("15"), c.getTime(), null, 10000.5, 12354L,
                            0.565,
                            "Foo"),
                    iterator.next().toList());
            Assert.assertTrue(iterator.hasNext());
            c.set(2020, Calendar.DECEMBER, 1, 9, 30, 55);
            Assert.assertEquals(
                    Arrays.asList(false, new BigDecimal("-1900.5"), null, c.getTime(), -520.8,
                            -1000L,
                            -0.128, "Bar"),
                    iterator.next().toList());
            Assert.assertFalse(iterator.hasNext());
        } finally {
            reader.close();
        }
    }

    @Test
    public void testCsvFile()
            throws MetaCSVDataException, MetaCSVReadException, MetaCSVParseException, IOException {
        File csvFile = File.createTempFile("test", ".csv");
        File mcsvFile = File.createTempFile("test", ".mcsv");
        csvFile.deleteOnExit();
        mcsvFile.deleteOnExit();

        Writer w = new OutputStreamWriter(new FileOutputStream(csvFile), UTF_8);
        w.write("a,b,c\r\n1,2,3\r\n");
        w.close();
        Writer mw = new OutputStreamWriter(new FileOutputStream(mcsvFile), UTF_8);
        mw.write("domain,key,value\r\ndata,col/1/type,integer\r\n");
        mw.close();

        MetaCSVReader reader = new MetaCSVReaderBuilder().csvFile(csvFile)
                .metaCSVFile(mcsvFile).build();
        try {
            Iterator<MetaCSVRecord> it = reader.iterator();
            Assert.assertTrue(it.hasNext());
            Assert.assertEquals(Arrays.asList("a", "b", "c"), it.next().toList());
            Assert.assertTrue(it.hasNext());
            Assert.assertEquals(Arrays.<Object>asList("1", 2L, "3"), it.next().toList());
            Assert.assertFalse(it.hasNext());
        } finally {
            reader.close();
        }
    }

    @Test
    public void testMetaParser()
            throws MetaCSVDataException, MetaCSVReadException, MetaCSVParseException, IOException {
        InputStream in = new ByteArrayInputStream("a,b,c\r\n1,2,3\r\n".getBytes(UTF_8));
        InputStream min = new ByteArrayInputStream(
                "domain,key,value\r\ndata,col/1/type,integer\r\n".getBytes(UTF_8));
        MetaCSVParser parser = new MetaCSVParserBuilder().metaIn(min).build();
        MetaCSVReader reader = new MetaCSVReaderBuilder().csvIn(in).metaParser(parser).build();
        Iterator<MetaCSVRecord> it = reader.iterator();
        try {
            Assert.assertTrue(it.hasNext());
            Assert.assertEquals(Arrays.asList("a", "b", "c"), it.next().toList());
            Assert.assertTrue(it.hasNext());
            Assert.assertEquals(Arrays.<Object>asList("1", 2L, "3"), it.next().toList());
            Assert.assertFalse(it.hasNext());
        } finally {
            reader.close();
        }
    }

    @Test
    public void testMetaData()
            throws MetaCSVDataException, MetaCSVReadException, MetaCSVParseException, IOException {
        InputStream in = new ByteArrayInputStream("a,b,c\r\n1,2,3\r\n".getBytes(UTF_8));
        InputStream min = new ByteArrayInputStream(
                "domain,key,value\r\ndata,col/1/type,integer\r\n".getBytes(UTF_8));
        MetaCSVData data = new MetaCSVParserBuilder().metaIn(min).buildData();
        MetaCSVReader reader = new MetaCSVReaderBuilder().csvIn(in).metaData(data).build();
        try {
            Iterator<MetaCSVRecord> it = reader.iterator();
            Assert.assertTrue(it.hasNext());
            Assert.assertEquals(Arrays.asList("a", "b", "c"), it.next().toList());
            Assert.assertTrue(it.hasNext());
            Assert.assertEquals(Arrays.<Object>asList("1", 2L, "3"), it.next().toList());
            Assert.assertFalse(it.hasNext());
        } finally {
            reader.close();
        }
    }

    @Test
    public void testTZOnError()
            throws MetaCSVDataException, MetaCSVReadException, MetaCSVParseException, IOException {
        InputStream in = new ByteArrayInputStream("a,b,c\r\nfoo,2021-04-20,3\r\n".getBytes(UTF_8));
        InputStream min = new ByteArrayInputStream(
                "domain,key,value\r\ndata,col/0/type,integer\ndata,col/1/type,date/yyyy-MM-dd\n"
                        .getBytes(UTF_8));
        MetaCSVData data = new MetaCSVParserBuilder().metaIn(min).buildData();
        MetaCSVReader reader = new MetaCSVReaderBuilder().csvIn(in).metaData(data).timeZone(
                TimeZone.getTimeZone("GMT")).onError(OnError.NULL).build();
        try {
            Iterator<MetaCSVRecord> it = reader.iterator();
            Assert.assertTrue(it.hasNext());
            Assert.assertEquals(Arrays.asList("a", "b", "c"), it.next().toList());
            Assert.assertTrue(it.hasNext());
            Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT"));
            cal.setTimeInMillis(0);
            cal.set(2021, Calendar.APRIL, 20);
            Assert.assertEquals(Arrays.<Object>asList(null, cal.getTime(), "3"),
                    it.next().toList());
            Assert.assertFalse(it.hasNext());
        } finally {
            reader.close();
        }
    }
}