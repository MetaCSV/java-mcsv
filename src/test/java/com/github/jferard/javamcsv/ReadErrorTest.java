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

public class ReadErrorTest {
    @Test
    public void test() {
        ReadError re = new ReadError("foo", "bar");
        Assert.assertEquals("foo", re.getText());
        Assert.assertEquals("bar", re.getDescription());
    }

    @Test
    public void testToString() {
        ReadError re = new ReadError("foo", "bar");
        Assert.assertEquals("ReadError{text='foo', description='bar'}", re.toString());
    }

    @Test
    public void testHashCode() {
        ReadError re = new ReadError("foo", "bar");
        Assert.assertEquals(3246093, re.hashCode());
    }

    @Test
    public void testHashCodeNullText() {
        ReadError re = new ReadError(null, "bar");
        Assert.assertEquals(97299, re.hashCode());
    }

    @Test
    public void testHashCodeNullDescription() {
        ReadError re = new ReadError("foo", null);
        Assert.assertEquals(3148794, re.hashCode());
    }

    @Test
    public void testEquals() {
        ReadError re1 = new ReadError("foo", "bar");
        ReadError re2 = new ReadError("foo", "baz");
        ReadError reExpected = new ReadError("foo", "bar");
        Assert.assertEquals(re1, re1);
        Assert.assertEquals(reExpected, re1);
        Assert.assertNotEquals(re2, re1);
        Assert.assertNotEquals(null, re1);
        Assert.assertNotEquals(re1, null);
        Assert.assertNotEquals(new Object(), re1);
    }
}