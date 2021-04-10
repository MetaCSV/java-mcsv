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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class MetaCSVRecordTest {
    @Test
    public void testToString() throws IOException {
        MetaCSVRecord metaRecord = TestHelper.createMetaRecord("foo", "bar", 1);
        Assert.assertEquals(
                "MetaCSVRecord{record=CSVRecord [comment=null, mapping=null, recordNumber=1, values=[foo, bar, 1]]}",
                metaRecord.toString());
    }

    @Test
    public void testSize() throws IOException {
        MetaCSVRecord metaRecord = TestHelper.createMetaRecord("foo", "bar", 1);
        Assert.assertEquals(3, metaRecord.size());
    }

    @Test
    public void testBoolean() throws IOException, MetaCSVReadException {
        MetaCSVRecord metaRecord = TestHelper.createMetaRecord("foo", "bar", true);
        Assert.assertTrue(
                metaRecord.getBoolean(2));
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testBooleanIndex() throws IOException, MetaCSVReadException {
        MetaCSVRecord metaRecord = TestHelper.createMetaRecord("foo", "bar", true);
        Assert.assertTrue(
                metaRecord.getBoolean(3));
    }

    @Test(expected = MetaCSVCastException.class)
    public void testNotBoolean() throws IOException, MetaCSVReadException {
        MetaCSVRecord metaRecord = TestHelper.createMetaRecord("foo", "bar", true);
        metaRecord.getBoolean(1);
    }

    @Test
    public void testDate() throws IOException, MetaCSVReadException {
        Calendar cal = GregorianCalendar.getInstance(Util.UTC_TIME_ZONE);
        cal.setTimeInMillis(1234567891011L);
        MetaCSVRecord metaRecord = TestHelper.createMetaRecord("foo", "bar", cal.getTime());
        Calendar expectedCal = GregorianCalendar.getInstance(Util.UTC_TIME_ZONE);
        expectedCal.setTimeInMillis(1234567891011L);
        expectedCal.set(Calendar.HOUR_OF_DAY, 0);
        expectedCal.set(Calendar.MINUTE, 0);
        expectedCal.set(Calendar.SECOND, 0);
        expectedCal.set(Calendar.MILLISECOND, 0);
        Assert.assertEquals(expectedCal.getTime(),
                metaRecord.getDate(2));
    }

    @Test(expected = MetaCSVCastException.class)
    public void testNotDate() throws IOException, MetaCSVReadException {
        Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis(1234567891011L);
        MetaCSVRecord metaRecord = TestHelper.createMetaRecord("foo", "bar", cal.getTime());
        metaRecord.getDate(1);
    }

    @Test
    public void testDecimal() throws IOException, MetaCSVReadException {
        MetaCSVRecord metaRecord = TestHelper.createMetaRecord("foo", "bar", BigDecimal.ONE);
        Assert.assertEquals(BigDecimal.ONE,
                metaRecord.getDecimal(2));
    }

    @Test(expected = MetaCSVCastException.class)
    public void testNotDecimal() throws IOException, MetaCSVReadException {
        MetaCSVRecord metaRecord = TestHelper.createMetaRecord("foo", "bar", BigDecimal.ONE);
        metaRecord.getDecimal(1);
    }

    @Test
    public void testDatetime() throws IOException, MetaCSVReadException {
        Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis(1234567891011l);
        MetaCSVRecord metaRecord = TestHelper.createMetaRecord("foo", "bar", cal.getTime());
        Calendar expectedCal = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
        expectedCal.setTimeInMillis(1234567891011l);
        Assert.assertEquals(expectedCal.getTime(),
                metaRecord.getDatetime(2));
    }

    @Test(expected = MetaCSVCastException.class)
    public void testNotDatetime() throws IOException, MetaCSVReadException {
        Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis(1234567891011l);
        MetaCSVRecord metaRecord = TestHelper.createMetaRecord("foo", "bar", cal.getTime());
        metaRecord.getDatetime(1);
    }


    @Test
    public void testFloat() throws IOException, MetaCSVReadException {
        MetaCSVRecord metaRecord = TestHelper.createMetaRecord("foo", "bar", 10.5);
        Assert.assertEquals(10.5,
                metaRecord.getFloat(2), 0.01);
    }

    @Test(expected = MetaCSVCastException.class)
    public void testNotFloat() throws IOException, MetaCSVReadException {
        MetaCSVRecord metaRecord = TestHelper.createMetaRecord("foo", "bar", 10.5);
        metaRecord.getFloat(1);
    }

    @Test
    public void testInteger() throws IOException, MetaCSVReadException {
        MetaCSVRecord metaRecord = TestHelper.createMetaRecord("foo", "bar", 2020);
        Assert.assertEquals(Long.valueOf(2020),
                metaRecord.getInteger(2));
    }

    @Test(expected = MetaCSVCastException.class)
    public void testNotInteger() throws IOException, MetaCSVReadException {
        MetaCSVRecord metaRecord = TestHelper.createMetaRecord("foo", "bar", 2020);
        metaRecord.getInteger(1);
    }

    @Test
    public void testText() throws IOException, MetaCSVReadException {
        MetaCSVRecord metaRecord = TestHelper.createMetaRecord("foo", "bar", 10.5);
        Assert.assertEquals("foo",
                metaRecord.getText(0));
    }

    @Test(expected = MetaCSVCastException.class)
    public void testNotText() throws IOException, MetaCSVReadException {
        MetaCSVRecord metaRecord = TestHelper.createMetaRecord("foo", "bar", 10.5);
        metaRecord.getText(2);
    }

    @Test
    public void testEquals() throws IOException, MetaCSVReadException {
        TestHelper.assertMetaEquals(TestHelper.createMetaRecord("foo", "bar", 10.5),
                TestHelper.createMetaRecord("foo", "bar", 10.5));

    }
}