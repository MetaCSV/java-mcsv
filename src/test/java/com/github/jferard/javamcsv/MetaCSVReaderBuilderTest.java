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
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;

public class MetaCSVReaderBuilderTest {
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
        MetaCSVMetaData metaData = reader.getMetaData();
        Assert.assertEquals(TestHelper.stringDescription(metaData.getDescription(0)),
                "boolean/T/F");
        Assert.assertEquals(TestHelper.stringDescription(metaData.getDescription(1)),
                "currency/pre/$/decimal/,/.");
        Assert.assertEquals(TestHelper.stringDescription(metaData.getDescription(2)),
                "date/dd\\/MM\\/yyyy");
        Assert.assertEquals(TestHelper.stringDescription(metaData.getDescription(3)),
                "datetime/yyyy-MM-dd HH:mm:ss");
        Assert.assertEquals(TestHelper.stringDescription(metaData.getDescription(4)), "float/,/.");
        Assert.assertEquals(TestHelper.stringDescription(metaData.getDescription(5)), "integer/ ");
        Assert.assertEquals(TestHelper.stringDescription(metaData.getDescription(6)),
                "percentage/post/%/float/,/.");
        Iterator<MetaCSVRecord> iterator = reader.iterator();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(
                Arrays.asList("boolean", "currency", "date", "datetime", "float", "integer",
                        "percentage", "text"), TestHelper.toList(iterator.next()));
        Assert.assertTrue(iterator.hasNext());

        Calendar c = GregorianCalendar.getInstance(Locale.US);
        c.setTimeInMillis(0);
        c.set(2020, Calendar.DECEMBER, 1, 0, 0, 0);
        Assert.assertEquals(
                Arrays.asList(true, new BigDecimal("15"), c.getTime(), null, 10000.5, 12354L, 0.565,
                        "Foo"),
                TestHelper.toList(iterator.next()));
        Assert.assertTrue(iterator.hasNext());
        c.set(2020, Calendar.DECEMBER, 1, 9, 30, 55);
        Assert.assertEquals(
                Arrays.asList(false, new BigDecimal("-1900.5"), null, c.getTime(), -520.8, -1000L,
                        -0.128, "Bar"),
                TestHelper.toList(iterator.next()));
        Assert.assertFalse(iterator.hasNext());
    }

}