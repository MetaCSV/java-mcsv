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

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MetaCSVReaderTest {
    public static <T> List<T> toList(Iterable<T> iterable) {
        List<T> list = new ArrayList<T>();
        for (T e : iterable) {
            list.add(e);
        }
        return list;
    }

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
                        "data,col/1/type,\"currency/pre/$/float/,/.\"\r\n" +
                        "data,col/2/type,date/dd\\/MM\\/yyyy\r\n" +
                        "data,col/3/type,datetime/yyyy-MM-dd HH:mm:ss\r\n" +
                        "data,col/4/type,\"float/,/.\"\r\n" +
                        "data,col/5/type,\"integer/ \"\r\n" +
                        "data,col/6/type,\"percentage/post/%/float/,/.\"\r\n");
        MetaCSVReader reader = MetaCSVReader.create(is, metaIs);

        Map<Integer, String> expectedTypes = new HashMap<Integer, String>();
        expectedTypes.put(0, "boolean/T/F");
        expectedTypes.put(1, "currency/pre/$/float/,/.");
        expectedTypes.put(2, "date/dd\\/MM\\/yyyy");
        expectedTypes.put(3, "datetime/yyyy-MM-dd HH:mm:ss");
        expectedTypes.put(4, "float/,/.");
        expectedTypes.put(5, "integer/ ");
        expectedTypes.put(6, "percentage/post/%/float/,/.");

        Assert.assertEquals(expectedTypes, reader.getTypes());
        Iterator<MetaCSVRecord> iterator = reader.iterator();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(
                Arrays.asList("boolean", "currency", "date", "datetime", "float", "integer",
                        "percentage", "text"), toList(iterator.next()));
        Assert.assertTrue(iterator.hasNext());

        Calendar c = GregorianCalendar.getInstance(Locale.US);
        c.setTimeInMillis(0);
        c.set(2020, Calendar.DECEMBER, 1, 0, 0, 0);
        Assert.assertEquals(
                Arrays.asList(true, 15.0, c.getTime(), null, 10000.5, 12354, 0.565, "Foo"),
                toList(iterator.next()));
        Assert.assertTrue(iterator.hasNext());
        c.set(2020, Calendar.DECEMBER, 1, 9, 30, 55);
        Assert.assertEquals(
                Arrays.asList(false, -1900.5, null, c.getTime(), -520.8, -1000, -0.128, "Bar"),
                toList(iterator.next()));
        Assert.assertFalse(iterator.hasNext());
    }
}