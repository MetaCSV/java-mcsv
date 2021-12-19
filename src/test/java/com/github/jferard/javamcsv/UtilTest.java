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

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Locale;

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
        Assert.assertEquals(Arrays.asList("boolean", "/\\", "\\/"), Util.parse("boolean/\\/\\\\/\\\\\\/"));
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

    @Test
    public void testFormatLong() {
        Assert.assertEquals("123", Util.formatLong(123L, ""));
        Assert.assertEquals("12345", Util.formatLong(12345L, ""));
        Assert.assertEquals("123456", Util.formatLong(123456L, null));
        Assert.assertEquals("123", Util.formatLong(123L, "~~"));
        Assert.assertEquals("12~~345", Util.formatLong(12345L, "~~"));
        Assert.assertEquals("123~~456", Util.formatLong(123456L, "~~"));
        Assert.assertEquals("0", Util.formatLong(0, "<>"));
    }

    @Test
    public void testFormatLongNeg() {
        Assert.assertEquals("-12~~345", Util.formatLong(-12345L, "~~"));
        Assert.assertEquals("-123~~456", Util.formatLong(-123456L, "~~"));
    }

    @Test
    public void testParseLong() {
        Assert.assertEquals(123456L, Util.parseLong("123456", null));
        Assert.assertEquals(12345L, Util.parseLong("12345", ""));
        Assert.assertEquals(123456L, Util.parseLong("123~~456", "~~"));
        Assert.assertEquals(12345L, Util.parseLong("12~~345", "~~"));
        Assert.assertEquals(123L, Util.parseLong("123", "~~"));
        Assert.assertEquals(0L, Util.parseLong("0", "~~"));
    }

    @Test
    public void testParseLongNeg() {
        Assert.assertEquals(-12345L, Util.parseLong("-12~~345", "~~"));
        Assert.assertEquals(-123456L, Util.parseLong("-123~~456", "~~"));
    }

    @Test
    public void testFormatDouble() {
        Assert.assertEquals("123.0", Util.formatDouble(123, null, "."));
        Assert.assertEquals("1234.56", Util.formatDouble(1234.56, null, "."));
        Assert.assertEquals("1234.5", Util.formatDouble(1234.5, "", "."));
        Assert.assertEquals("123.0", Util.formatDouble(123, "~~", "."));
        Assert.assertEquals("12~~345.6", Util.formatDouble(12345.6, "~~", "."));
        Assert.assertEquals("12~~345.0", Util.formatDouble(12345.0, "~~", "."));
        Assert.assertEquals("0.0", Util.formatDouble(0, "<>", "."));
    }

    @Test
    public void testFormatDoubleNeg() {
        Assert.assertEquals("-1~~234,56", Util.formatDouble(-1234.56, "~~", ","));
    }

    @Test
    public void testParseDouble() {
        Assert.assertEquals(123, Util.parseDouble("123.0", null, "."), 0.001);
        Assert.assertEquals(1234.56, Util.parseDouble("1234.56", null, "."), 0.001);
        Assert.assertEquals(1234.5, Util.parseDouble("1234.5", "", "."), 0.001);
        Assert.assertEquals(123, Util.parseDouble("123.0", "~~", "."), 0.001);
        Assert.assertEquals(12345.6, Util.parseDouble("12~~345.6", "~~", "."), 0.001);
        Assert.assertEquals(12345.0, Util.parseDouble("12~~345.0", "~~", "."), 0.001);
        Assert.assertEquals(0, Util.parseDouble("0.0", "<>", "."), 0.001);
    }

    @Test
    public void testParseDoubleNeg() {
        Assert.assertEquals(-1234.56, Util.parseDouble("-1~~234,56", "~~", ","), 0.001);
    }

    @Test
    public void testFormatBigDecimal() {
        Assert.assertEquals("123.0", Util.formatBigDecimal(new BigDecimal("123"), null, "."));
        Assert.assertEquals("123.0", Util.formatBigDecimal(new BigDecimal("123."), null, "."));
        Assert.assertEquals("123.0", Util.formatBigDecimal(new BigDecimal("123.0"), null, "."));
        Assert.assertEquals("123.0", Util.formatBigDecimal(new BigDecimal("123.00"), null, "."));
        Assert.assertEquals("1234.56", Util.formatBigDecimal(new BigDecimal("1234.56"), null, "."));
        Assert.assertEquals("1234.5", Util.formatBigDecimal(new BigDecimal("1234.5"), "", "."));
        Assert.assertEquals("123.0", Util.formatBigDecimal(new BigDecimal("123.0"), "~~", "."));
        Assert.assertEquals("12~~345.6",
                Util.formatBigDecimal(new BigDecimal("12345.6"), "~~", "."));
        Assert.assertEquals("12~~345.0",
                Util.formatBigDecimal(new BigDecimal("12345.0"), "~~", "."));
        Assert.assertEquals("0.0", Util.formatBigDecimal(new BigDecimal("0.0"), "<>", "."));
    }

    @Test
    public void testFormatBigDecimalNeg() {
        Assert.assertEquals("-1~~234,56", Util.formatDouble(-1234.56, "~~", ","));
    }

    @Test
    public void testParseBigDecimal() {
        Assert.assertEquals(new BigDecimal("123.0"), Util.parseBigDecimal("123.0", null, "."));
        Assert.assertEquals(new BigDecimal("1234.56"), Util.parseBigDecimal("1234.56", null, "."));
        Assert.assertEquals(new BigDecimal("1234.5"), Util.parseBigDecimal("1234.5", "", "."));
        Assert.assertEquals(new BigDecimal("123.0"), Util.parseBigDecimal("123.0", "~~", "."));
        Assert.assertEquals(new BigDecimal("12345.6"),
                Util.parseBigDecimal("12~~345.6", "~~", "."));
        Assert.assertEquals(new BigDecimal("12345.0"),
                Util.parseBigDecimal("12~~345.0", "~~", "."));
        Assert.assertEquals(new BigDecimal("0.0"), Util.parseBigDecimal("0.0", "<>", "."));
    }

    @Test
    public void testParseBigDecimalNeg() {
        Assert.assertEquals(new BigDecimal("-1234.56"),
                Util.parseBigDecimal("-1~~234,56", "~~", ","));
    }

    @Test
    public void testGetLocale() {
        Assert.assertEquals(Locale.US, Util.getLocale("en_US"));
        Assert.assertEquals(Locale.ENGLISH, Util.getLocale("en"));
    }

    @Test
    public void testReplaceChar() {
        Assert.assertEquals("foo", Util.replaceChar("foo", null, "a"));
    }

    @Test
    public void testCleanCurrencyText() throws MetaCSVReadException {
        Assert.assertEquals("10", Util.cleanCurrencyText("10 €", false, "€"));
    }
}