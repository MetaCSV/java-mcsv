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

package com.github.jferard.javamcsv.description;

import com.github.jferard.javamcsv.DataType;
import com.github.jferard.javamcsv.TestHelper;
import com.github.jferard.javamcsv.Util;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFieldDescriptionTest {
    private SimpleDateFormat dateFormat;
    private DateFieldDescription fieldDescription;

    @Before
    public void setUp() throws Exception {
        dateFormat = new SimpleDateFormat("yyyy-MM-ddZ");
        String locale = Util.getLocaleString(Locale.US);
        fieldDescription = new DateFieldDescription(dateFormat, locale);
    }

    @Test
    public void testDateNoLocale() throws IOException {
        Assert.assertEquals("date/yyyy-MM-ddZ", TestHelper.render(
                new DateFieldDescription(dateFormat, null)));
    }

    @Test
    public void testDateLocale() throws IOException {
        Assert.assertEquals("date/yyyy-MM-ddZ/en_US", TestHelper.render(
                fieldDescription));
    }

    @Test
    public void testDateToString() throws IOException {
        Assert.assertEquals("DateFieldDescription(yyyy-MM-ddZ, en_US)",
                fieldDescription.toString());
    }
    @Test
    public void testDataType() throws IOException {
        Assert.assertEquals(DataType.DATE, fieldDescription.getDataType());
    }
    @Test
    public void testJavaType() throws IOException {
        Assert.assertEquals(Date.class, fieldDescription.getJavaType());
    }
}
