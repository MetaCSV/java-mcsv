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
import java.util.Iterator;

public class CSVRecordIteratorTest {
    @Test
    public void test() throws IOException {
        MetaCSVRecord record = TestHelper.createMetaRecord(true, 1L, "foo");
        Iterator<Object> it = record.iterator();
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(true, it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(1L, it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals("foo", it.next());
        Assert.assertFalse(it.hasNext());
    }
}