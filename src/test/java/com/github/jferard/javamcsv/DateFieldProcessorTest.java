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

import com.github.jferard.javamcsv.description.DateFieldDescription;
import com.github.jferard.javamcsv.description.DatetimeFieldDescription;
import com.github.jferard.javamcsv.processor.FieldProcessor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateFieldProcessorTest {

    private FieldProcessor<Date> processor;
    private FieldProcessor<Date> dtProcessor;

    @Before
    public void setUp() {
        processor = new DateFieldDescription(new SimpleDateFormat("yyyy-MM-dd", Locale.US), "en_US")
                .toFieldProcessor("NULL");
        dtProcessor = DatetimeFieldDescription.create("yyyy-MM-dd'T'HH:mm:ss", "en_US")
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
        Assert.assertEquals(c.getTime(), processor.toObject("2020-11-21"));
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
        Calendar c = GregorianCalendar.getInstance(Locale.US);
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
        Calendar c = GregorianCalendar.getInstance(Locale.US);
        c.setTimeZone(Util.UTC_TIME_ZONE);
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
}