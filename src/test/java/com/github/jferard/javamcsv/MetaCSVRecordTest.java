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

import java.io.IOException;

public class MetaCSVRecordTest {
    @Test
    public void testToString() throws IOException {
        MetaCSVRecord metaRecord = TestHelper.createMetaRecord("foo", "bar", 1);
        Assert.assertEquals(
                "MetaCSVRecord(CSVRecord [comment=null, mapping=null, recordNumber=1, values=[foo, bar, 1]] ,[foo, bar, 1])",
                metaRecord.toString());
    }

    @Test
    public void testSize() throws IOException {
        MetaCSVRecord metaRecord = TestHelper.createMetaRecord("foo", "bar", 1);
        Assert.assertEquals(3, metaRecord.size());
    }

    @Test
    public void testBoolean() throws IOException {
        MetaCSVRecord metaRecord = TestHelper.createMetaRecord("foo", "bar", true);
        Assert.assertTrue(
                metaRecord.getBoolean(2));
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testBooleanIndex() throws IOException {
        MetaCSVRecord metaRecord = TestHelper.createMetaRecord("foo", "bar", true);
        Assert.assertTrue(
                metaRecord.getBoolean(3));
    }

    @Test(expected = MetaCSVCastException.class)
    public void testNotBoolean() throws IOException {
        MetaCSVRecord metaRecord = TestHelper.createMetaRecord("foo", "bar", true);
        metaRecord.getBoolean(1);
    }

    @Test
    public void testCurrency() throws IOException {
        MetaCSVRecord metaRecord = TestHelper.createMetaRecord("foo", "bar", 10.5);
        Assert.assertEquals(10.5,
                metaRecord.getCurrency(2), 0.01);
    }

    @Test(expected = MetaCSVCastException.class)
    public void testNotCurrency() throws IOException {
        MetaCSVRecord metaRecord = TestHelper.createMetaRecord("foo", "bar", 10.5);
        metaRecord.getCurrency(1);
    }

    @Test
    public void testText() throws IOException {
        MetaCSVRecord metaRecord = TestHelper.createMetaRecord("foo", "bar", 10.5);
        Assert.assertEquals("foo",
                metaRecord.getText(0));
    }

    @Test(expected = MetaCSVCastException.class)
    public void testNotText() throws IOException {
        MetaCSVRecord metaRecord = TestHelper.createMetaRecord("foo", "bar", 10.5);
        metaRecord.getText(2);
    }

    @Test
    public void testEquals() throws IOException {
        TestHelper.assertMetaEquals(TestHelper.createMetaRecord("foo", "bar", 10.5),
                TestHelper.createMetaRecord("foo", "bar", 10.5));

    }
}