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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateTest {
    public static final TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");

    @Test
    public void testWithTZ() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ssZ");
        format.setTimeZone(DateTest.UTC_TIME_ZONE);
        Date d = format.parse("2020.01.01 00:00:00-0800");
        Calendar c = Calendar.getInstance(DateTest.UTC_TIME_ZONE);
        c.setTimeInMillis(0);
        c.set(2020, Calendar.JANUARY, 1, 8, 0, 0);
        Assert.assertEquals(c.getTime(), d);
    }

    @Test
    public void testChangeTZ() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        format.setTimeZone(DateTest.UTC_TIME_ZONE);
        Date d = format.parse("2020.01.02 03:04:05");
        Calendar c = Calendar.getInstance(UTC_TIME_ZONE);
        c.setTimeInMillis(0);
        c.set(2020, Calendar.JANUARY, 2, 3, 4, 5);
        Assert.assertEquals(c.getTime(), d);

        // but if actual TimeZone was not UTC...
        TimeZone actualTimeZone = TimeZone.getTimeZone("GMT-8");

        Date d2 = new Date(d.getTime() + DateTest.UTC_TIME_ZONE.getRawOffset() - actualTimeZone.getRawOffset());
        Calendar c2 = Calendar.getInstance(actualTimeZone);
        c2.setTimeInMillis(0);
        c2.set(2020, Calendar.JANUARY, 2, 3, 4, 5);
        // c2 : 2020-01-02 11:04:05 GMT

        Assert.assertEquals(c2.getTime(), d2);
    }
}
