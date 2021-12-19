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

import com.github.jferard.javamcsv.description.CurrencyDecimalFieldDescription;
import com.github.jferard.javamcsv.description.IntegerFieldDescription;
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

public class MetaCSVReaderTest {
    @Test
    public void testBOM()
            throws IOException, MetaCSVReadException, MetaCSVDataException, MetaCSVParseException {
        ByteArrayInputStream is = TestHelper.bomUtf8InputStream(
                "a,b\r\n" +
                        "1,2\r\n");
        ByteArrayInputStream metaIs = TestHelper.utf8InputStream(
                "domain,key,value\r\n" +
                        "file,bom,true\r\n");
        MetaCSVReader reader = MetaCSVReader.create(is, metaIs);
    }

    @Test(expected = MetaCSVReadException.class)
    public void testBOMMissing()
            throws IOException, MetaCSVReadException, MetaCSVDataException, MetaCSVParseException {
        ByteArrayInputStream is = TestHelper.utf8InputStream(
                "a,b\r\n" +
                        "1,2\r\n");
        ByteArrayInputStream metaIs = TestHelper.utf8InputStream(
                "domain,key,value\r\n" +
                        "file,bom,true\r\n");
        MetaCSVReader reader = MetaCSVReader.create(is, metaIs);
    }

    @Test
    public void test()
            throws IOException, MetaCSVParseException, MetaCSVReadException, MetaCSVDataException {
        ByteArrayInputStream is = TestHelper.utf8InputStream(
                "boolean,currency,date,datetime,float,integer,percentage,text\r\n" +
                        "T,$15,01/12/2020,NULL,\"10,000.5\",12 354,56.5%,Foo\r\n" +
                        "F,\"$-1,900.5\",NULL,2020-12-01 09:30:55,-520.8,-1 000,-12.8%,Bar\r\n");
        ByteArrayInputStream metaIs = TestHelper.utf8InputStream(
                "domain,key,value\r\n" +
                        "data,null_value,NULL\r\n" +
                        "data,col/0/type,boolean/T/F\r\n" +
                        "data,col/1/type,\"currency/pre/$/decimal/,/.\"\r\n" +
                        "data,col/2/type,date/dd\\/MM\\/yyyy\r\n" +
                        "data,col/3/type,datetime/yyyy-MM-dd HH:mm:ss\r\n" +
                        "data,col/4/type,\"float/,/.\"\r\n" +
                        "data,col/5/type,\"integer/ \"\r\n" +
                        "data,col/6/type,\"percentage/post/%/float/,/.\"\r\n");
        MetaCSVReader reader = MetaCSVReader.create(is, metaIs);
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
    public void testCanonical()
            throws IOException, MetaCSVParseException, MetaCSVReadException, MetaCSVDataException {
        ByteArrayInputStream is = TestHelper.utf8InputStream(
                "boolean,currency,date,datetime,float,integer,percentage,text\r\n" +
                        "T,$15,01/12/2020,NULL,\"10,000.5\",12 354,56.5%,Foo\r\n" +
                        "F,\"$-1,900.5\",NULL,2020-12-01 09:30:55,-520.8,-1 000,-12.8%,Bar\r\n");
        ByteArrayInputStream metaIs = TestHelper.utf8InputStream(
                "domain,key,value\r\n" +
                        "data,null_value,NULL\r\n" +
                        "data,col/0/type,boolean/T/F\r\n" +
                        "data,col/1/type,\"currency/pre/$/decimal/,/.\"\r\n" +
                        "data,col/2/type,date/dd\\/MM\\/yyyy\r\n" +
                        "data,col/3/type,datetime/yyyy-MM-dd HH:mm:ss\r\n" +
                        "data,col/4/type,\"float/,/.\"\r\n" +
                        "data,col/5/type,\"integer/ \"\r\n" +
                        "data,col/6/type,\"percentage/post/%/float/,/.\"\r\n");
        MetaCSVReader reader = MetaCSVReader.create(is, metaIs);
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
                    Arrays.asList("true", "15", "2020-12-01", "", "10000.5", "12354",
                            "0.565", "Foo"),
                    iterator.next().toCanonicalList());
            Assert.assertTrue(iterator.hasNext());
            c.set(2020, Calendar.DECEMBER, 1, 9, 30, 55);
            Assert.assertEquals(
                    Arrays.asList("false", "-1900.5", "", "2020-12-01T09:30:55", "-520.8",
                            "-1000", "-0.128", "Bar"),
                    iterator.next().toCanonicalList());
            Assert.assertFalse(iterator.hasNext());
        } finally {
            reader.close();
        }
    }

    @Test
    public void test2()
            throws IOException, MetaCSVReadException, MetaCSVDataException, MetaCSVParseException {
        ByteArrayInputStream is = TestHelper.utf8InputStream(
                "boolean,currency,date,datetime,float,integer,percentage,text\r\n" +
                        "T,$15,01/12/2020,NULL,\"10,000.5\",12 354,56.5%,Foo\r\n" +
                        "F,\"$-1,900.5\",NULL,2020-12-01 09:30:55,-520.8,-1 000,-12.8%,Bar\r\n");
        MetaCSVReader reader = MetaCSVReader
                .create(is, "data,null_value,NULL", "data,col/0/type,boolean/T/F",
                        "data,col/1/type,\"currency/pre/$/decimal/,/.\"",
                        "data,col/2/type,date/dd\\/MM\\/yyyy",
                        "data,col/3/type,datetime/yyyy-MM-dd HH:mm:ss",
                        "data,col/4/type,\"float/,/.\"", "data,col/5/type,\"integer/ \"",
                        "data,col/6/type,\"percentage/post/%/float/,/.\"");

        try {
            MetaCSVMetaData metaData = reader.getMetaData();
            Assert.assertEquals(TestHelper.stringDescription(metaData.getDescription(0)),
                    "boolean/T/F");
            Assert.assertEquals(TestHelper.stringDescription(metaData.getDescription(1)),
                    "currency/pre/$/decimal/,/.");
            Assert.assertEquals(metaData.getDescription(1, CurrencyDecimalFieldDescription.class)
                            .getCurrencySymbol(),
                    "$");
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
    public void testCreateCsvFile()
            throws IOException, MetaCSVReadException, MetaCSVDataException, MetaCSVParseException {
        File csvFile = File.createTempFile("test", ".csv");
        File mcsvFile = Util.withExtension(csvFile, ".mcsv");
        csvFile.deleteOnExit();
        mcsvFile.deleteOnExit();

        Writer w = new OutputStreamWriter(new FileOutputStream(csvFile), TestHelper.UTF_8_CHARSET);
        w.write("a,b,c\r\n1,2,3\r\n");
        w.close();
        Writer mw =
                new OutputStreamWriter(new FileOutputStream(mcsvFile), TestHelper.UTF_8_CHARSET);
        mw.write("domain,key,value\r\ndata,col/1/type,integer\r\n");
        mw.close();

        MetaCSVReader reader = MetaCSVReader.create(csvFile);
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
    public void testCreate()
            throws IOException, MetaCSVReadException, MetaCSVDataException, MetaCSVParseException {
        File csvFile = File.createTempFile("test", ".csv");
        File mcsvFile = File.createTempFile("test", ".mcsv");
        csvFile.deleteOnExit();
        mcsvFile.deleteOnExit();

        Writer w = new OutputStreamWriter(new FileOutputStream(csvFile), TestHelper.UTF_8_CHARSET);
        w.write("a,b,c\r\n1,2,3\r\n");
        w.close();
        Writer mw =
                new OutputStreamWriter(new FileOutputStream(mcsvFile), TestHelper.UTF_8_CHARSET);
        mw.write("domain,key,value\r\ndata,col/1/type,integer\r\n");
        mw.close();

        MetaCSVReader reader = MetaCSVReader.create(csvFile, mcsvFile);
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
    public void testCreateStringArray()
            throws IOException, MetaCSVReadException, MetaCSVDataException, MetaCSVParseException {
        File csvFile = File.createTempFile("test", ".csv");
        csvFile.deleteOnExit();

        Writer w = new OutputStreamWriter(new FileOutputStream(csvFile), TestHelper.UTF_8_CHARSET);
        w.write("a,b,c\r\n1,2,3\r\n");
        w.close();

        MetaCSVReader reader = MetaCSVReader.create(csvFile, "data,col/1/type,integer");
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
    public void testCreateInputStream()
            throws IOException, MetaCSVReadException, MetaCSVDataException, MetaCSVParseException {
        InputStream in =
                new ByteArrayInputStream("a,b,c\r\n1,2,3\r\n".getBytes(TestHelper.UTF_8_CHARSET));
        MetaCSVData data =
                new MetaCSVDataBuilder().colType(1, IntegerFieldDescription.INSTANCE).build();
        MetaCSVReader reader = MetaCSVReader.create(in, data);
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
}