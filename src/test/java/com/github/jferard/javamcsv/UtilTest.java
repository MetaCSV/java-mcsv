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

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class UtilTest extends TestCase {
    @Test
    public void testLineTerminator() {
        Assert.assertEquals("\\r\\n", Util.escapeLineTerminator("\r\n"));
        Assert.assertEquals("\\n", Util.escapeLineTerminator("\n"));
        Assert.assertEquals("\\r", Util.escapeLineTerminator("\r"));
        Assert.assertEquals("foo", Util.escapeLineTerminator("foo"));
        Assert.assertEquals("\r\n", Util.unescapeLineTerminator("\\r\\n"));
        Assert.assertEquals("\n", Util.unescapeLineTerminator("\\n"));
        Assert.assertEquals("\r", Util.unescapeLineTerminator("\\r"));
        Assert.assertEquals("foo", Util.unescapeLineTerminator("foo"));

    }

    @Test
    public void testParse() {
        Assert.assertEquals(Arrays.asList("a", "b\\c/d", "e"), Util.parse("a/b\\\\c\\/d/e"));
        Assert.assertEquals(Arrays.asList("a", "b\\c/d", "e\\f"), Util.parse("a/b\\\\c\\/d/e\\f"));
    }

    @Test
    public void testRender() throws IOException {
        Assert.assertEquals("a", Util.render("a", ""));
        Assert.assertEquals("a//b", Util.render("a", "", "b"));
        Assert.assertEquals("", Util.render("", "", ""));
    }

    @Test
    public void testExtension() {
        Assert.assertEquals(new File("/.mcsv"), Util.withExtension(new File("/"), ".mcsv"));
        Assert.assertEquals(new File("/a.mcsv"), Util.withExtension(new File("/a"), ".mcsv"));
        Assert.assertEquals(new File("/.mcsv"), Util.withExtension(new File("/.csv"), ".mcsv"));
        Assert.assertEquals(new File("/a.mcsv"), Util.withExtension(new File("/a.csv"), ".mcsv"));
    }

    @Test
    public void testJoin() {
        Assert.assertEquals("", Util.join(new String[]{}, ","));
        Assert.assertEquals("foo", Util.join(new String[]{"foo"}, ","));
        Assert.assertEquals("foo,bar,baz", Util.join(new String[]{"foo", "bar", "baz"}, ","));
    }
}