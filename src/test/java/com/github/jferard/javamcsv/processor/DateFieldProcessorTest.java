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

package com.github.jferard.javamcsv.processor;

import com.github.jferard.javamcsv.MetaCSVReadException;
import com.github.jferard.javamcsv.Util;
import com.github.jferard.javamcsv.description.DateFieldDescription;
import com.github.jferard.javamcsv.description.DatetimeFieldDescription;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateFieldProcessorTest {

    private FieldProcessor<Date> processor;
    private FieldProcessor<Date> dtProcessor;

    @Before
    public void setUp() {
        processor = new DateFieldDescription(Util.CANONICAL_DATE_FORMAT, "en_US")
                .toFieldProcessor("NULL");
        dtProcessor = new DatetimeFieldDescription(Util.CANONICAL_DATETIME_FORMAT, "en_US")
                .toFieldProcessor("NULL");
    }

    @Test
    public void testNullDateToObject() throws MetaCSVReadException {
        Assert.assertNull(processor.toObject(null));
        Assert.assertNull(processor.toObject("NULL"));
    }

    @Test
    public void testDateToObject() throws MetaCSVReadException {
        Calendar c = GregorianCalendar.getInstance(Locale.US);
        c.setTimeInMillis(0);
        c.set(2020, Calendar.NOVEMBER, 21, 0, 0, 0);
        c.setTimeZone(Util.UTC_TIME_ZONE);
        Assert.assertEquals(c.getTime(), processor.toObject("2020-11-21"));
    }

    @Test
    public void testUncompleteDateToObject() throws MetaCSVReadException {
        final FieldProcessor<Date> processor2 =
                new DateFieldDescription(new SimpleDateFormat("yyyyMMdd", Locale.US), "en_US")
                        .toFieldProcessor("NULL");
        Assert.assertThrows(MetaCSVReadException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                processor2.toObject("2020    ");
            }
        });
    }

    @Test(expected = MetaCSVReadException.class)
    public void testWrongDateToObject() throws MetaCSVReadException {
        processor.toObject("foo");
    }

    @Test
    public void testNullDateToString() {
        Assert.assertEquals("NULL", processor.toString(null));
    }

    @Test
    public void testDateToString() {
        Calendar c = GregorianCalendar.getInstance(Util.UTC_TIME_ZONE, Locale.US);
        c.setTimeInMillis(0);
        c.set(2020, Calendar.NOVEMBER, 21, 0, 0, 0);
        Assert.assertEquals("2020-11-21", processor.toString(c.getTime()));
    }

    @Test
    public void testNullDatetimeToObject() throws MetaCSVReadException {
        Assert.assertNull(dtProcessor.toObject(null));
        Assert.assertNull(dtProcessor.toObject("NULL"));
    }

    @Test
    public void testDatetimeToObject() throws MetaCSVReadException {
        Calendar c = GregorianCalendar.getInstance(Util.UTC_TIME_ZONE, Locale.US);
        c.setTimeInMillis(0);
        c.set(2020, Calendar.NOVEMBER, 21, 3, 2, 1);
        Assert.assertEquals(c.getTime(), dtProcessor.toObject("2020-11-21T03:02:01"));
    }

    @Test(expected = MetaCSVReadException.class)
    public void testWrongDatetimeToObject() throws MetaCSVReadException {
        dtProcessor.toObject("bar");
    }

    @Test
    public void testNullDatetimeToString() {
        Assert.assertEquals("NULL", dtProcessor.toString(null));
    }

    @Test
    public void testDatetimeToString() {
        Calendar c = GregorianCalendar.getInstance(Locale.US);
        c.setTimeZone(Util.UTC_TIME_ZONE);
        c.setTimeInMillis(0);
        c.set(2020, Calendar.NOVEMBER, 21, 3, 2, 1);
        Assert.assertEquals("2020-11-21T03:02:01", dtProcessor.toString(c.getTime()));
    }

    @Test
    public void testMilliseconds() throws MetaCSVReadException {
        FieldProcessor<Date> processor =
                DatetimeFieldDescription.create("yyyy-MM-dd'T'HH:mm:ss", "en_US")
                        .toFieldProcessor("NULL");
        Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.setTimeInMillis(0);
        cal.set(2021, Calendar.JANUARY, 12, 15, 34, 25);
        Assert.assertEquals(cal.getTime(), processor.toObject("2021-01-12T15:34:25.1245"));
    }

    @Test
    public void testHours() throws MetaCSVReadException {
        FieldProcessor<Date> processor =
                DatetimeFieldDescription.create("yyyy-MM-dd", "en_US")
                        .toFieldProcessor("NULL");
        Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.setTimeInMillis(0);
        cal.set(2021, Calendar.JANUARY, 12, 0, 0, 0);
        Assert.assertEquals(cal.getTime(), processor.toObject("2021-01-12T15:34:25.1245"));
    }

    @Test
    public void testCast() throws MetaCSVReadException {
        FieldProcessor<Date> processor =
                DatetimeFieldDescription.create("yyyy-MM-dd", "en_US")
                        .toFieldProcessor("NULL");
        Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.setTimeInMillis(0);
        cal.set(2021, Calendar.JANUARY, 12, 0, 0, 0);
        Assert.assertEquals(cal.getTime(), processor.cast(cal));
        cal.setTimeInMillis(123456789);
        Assert.assertEquals(cal.getTime(), processor.cast(123456789));
        Assert.assertEquals(new Date(123), processor.cast(new java.sql.Date(123)));
    }

    @Test
    public void testWrongCast() throws MetaCSVReadException {
        final FieldProcessor<Date> processor =
                new DatetimeFieldDescription(Util.CANONICAL_DATE_FORMAT, "en_US")
                        .toFieldProcessor("NULL");
        Assert.assertThrows(ClassCastException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                processor.cast("foo");
            }
        });
    }

    @Test
    public void testCanonicalString() throws MetaCSVReadException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        simpleDateFormat.setTimeZone(Util.UTC_TIME_ZONE);
        DateFieldProcessor processor2 =
                new DateFieldProcessor(simpleDateFormat, "fr_FR", "NULL",
                        Util.CANONICAL_DATE_FORMAT);
        Assert.assertEquals("1975-04-20", processor2.toCanonicalString("20/04/1975"));
    }
}