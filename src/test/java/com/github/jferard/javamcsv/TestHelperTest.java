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
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

public class TestHelperTest {
    @Test
    public void testBoundedStream0() throws IOException {
        InputStream in =
                TestHelper.boundedStream(new ByteArrayInputStream(new byte[]{1, 5, 7}), 0);
        Assert.assertArrayEquals(new byte[] {}, TestHelper.readStream(in));
    }

    @Test
    public void testBoundedStream2() throws IOException {
        InputStream in =
                TestHelper.boundedStream(new ByteArrayInputStream(new byte[]{1, 5, 7}), 2);
        Assert.assertArrayEquals(new byte[] {1, 5}, TestHelper.readStream(in));
    }

    @Test
    public void testBoundedStream4() throws IOException {
        InputStream in =
                TestHelper.boundedStream(new ByteArrayInputStream(new byte[]{1, 5, 7}), 4);
        Assert.assertArrayEquals(new byte[] {1, 5, 7}, TestHelper.readStream(in));
    }

    @Test
    public void testBoundedReader0() throws IOException {
        Reader r =
                TestHelper.boundedReader(new StringReader("bar"), 0);
        Assert.assertEquals("", TestHelper.readReader(r));
    }

    @Test
    public void testBoundedReader2() throws IOException {
        Reader r =
                TestHelper.boundedReader(new StringReader("bar"), 2);
        Assert.assertEquals("ba", TestHelper.readReader(r));
    }

    @Test
    public void testBoundedReader4() throws IOException {
        Reader r =
                TestHelper.boundedReader(new StringReader("bar"), 4);
        Assert.assertEquals("bar", TestHelper.readReader(r));
    }

}