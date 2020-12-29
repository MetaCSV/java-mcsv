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
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

public class FieldDescriptionTest {
    @Test
    public void testBoolean() throws IOException {
        Assert.assertEquals("boolean/T/", TestHelper.render(new BooleanFieldDescription("T", "")));
        Assert.assertEquals("boolean/T/F",
                TestHelper.render(new BooleanFieldDescription("T", "F")));
        Assert.assertEquals("BooleanFieldDescription(T, F)",
                new BooleanFieldDescription("T", "F").toString());
    }

    @Test
    public void testDate() throws IOException {
        Assert.assertEquals("date/yyyy-MM-ddZ", TestHelper.render(
                new DateFieldDescription(new SimpleDateFormat("yyyy-MM-ddZ"), null)));
        String locale = Util.getLocaleString(Locale.US);
        Assert.assertEquals("date/yyyy-MM-ddZ/en_US", TestHelper.render(
                new DateFieldDescription(new SimpleDateFormat("yyyy-MM-ddZ"), locale)));
        Assert.assertEquals("DateFieldDescription(yyyy-MM-ddZ, en_US)",
                new DateFieldDescription(new SimpleDateFormat("yyyy-MM-ddZ"), locale).toString());
    }

    @Test
    public void testDatetime() throws IOException {
        Assert.assertEquals("datetime/yyyy-MM-dd'T'HH:mm:ssZ", TestHelper.render(
                new DatetimeFieldDescription(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"),
                        null)));
        Assert.assertEquals("datetime/yyyy-MM-dd'T'HH:mm:ssZ/en_US", TestHelper.render(
                new DatetimeFieldDescription(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"),
                        Util.getLocaleString(Locale.US))));
        Assert.assertEquals("DatetimeDescription(yyyy-MM-dd'T'HH:mm:ssZ, en_US)",
                new DatetimeFieldDescription(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"),
                        Util.getLocaleString(Locale.US)).toString());
    }

    @Test
    public void testDecimalCurrency() throws IOException {
        FieldDescription<BigDecimal> numberDescription = new DecimalFieldDescription("", ".");
        Assert.assertEquals("currency/pre/$/decimal//.", TestHelper.render(
                new DecimalCurrencyFieldDescription(true, "$", numberDescription)));
        Assert.assertEquals("currency/post/€/decimal//.", TestHelper.render(
                new DecimalCurrencyFieldDescription(false, "€", numberDescription)));
        Assert.assertEquals("CurrencyFieldDescription(false, €, DecimalFieldDescription(, .))",
                new DecimalCurrencyFieldDescription(false, "€", numberDescription).toString());
    }

    @Test
    public void testIntegerCurrency() throws IOException {
        FieldDescription<Long> numberDescription = new IntegerFieldDescription(null);
        Assert.assertEquals("currency/pre/$/integer", TestHelper.render(
                new IntegerCurrencyFieldDescription(true, "$", numberDescription)));
        Assert.assertEquals("currency/post/€/integer", TestHelper.render(
                new IntegerCurrencyFieldDescription(false, "€", numberDescription)));
        Assert.assertEquals("CurrencyFieldDescription(false, €, IntegerFieldDescription(null))",
                new IntegerCurrencyFieldDescription(false, "€", numberDescription).toString());
    }

    @Test
    public void testFloat() throws IOException {
        Assert.assertEquals("float/ /,", TestHelper.render(new FloatFieldDescription(" ", ",")));
        Assert.assertEquals("FloatFieldDescription( , ,)",
                new FloatFieldDescription(" ", ",").toString());
    }

    @Test
    public void testDecimal() throws IOException {
        Assert.assertEquals("decimal/ /,",
                TestHelper.render(new DecimalFieldDescription(" ", ",")));
        Assert.assertEquals("DecimalFieldDescription( , ,)",
                new DecimalFieldDescription(" ", ",").toString());
    }

    @Test
    public void testInteger() throws IOException {
        Assert.assertEquals("integer", TestHelper.render(new IntegerFieldDescription("")));
        Assert.assertEquals("integer/..", TestHelper.render(new IntegerFieldDescription("..")));
        Assert.assertEquals("IntegerFieldDescription(.)",
                new IntegerFieldDescription(".").toString());
    }

    @Test
    public void testDecimalPercentage() throws IOException {
        FieldDescription<BigDecimal> numberDescription = new DecimalFieldDescription("", ".");
        Assert.assertEquals("percentage/pre/%/decimal//.", TestHelper.render(
                new DecimalPercentageFieldDescription(true, "%", numberDescription)));
        Assert.assertEquals("percentage/post/%/decimal//.", TestHelper.render(
                new DecimalPercentageFieldDescription(false, "%", numberDescription)));
        Assert.assertEquals("PercentageFieldDescription(false, %, DecimalFieldDescription(, .))",
                new DecimalPercentageFieldDescription(false, "%", numberDescription).toString());
    }

    @Test
    public void testFloatPercentage() throws IOException {
        FieldDescription<Double> numberDescription = new FloatFieldDescription("", ".");
        Assert.assertEquals("percentage/pre/%/float//.", TestHelper.render(
                new FloatPercentageFieldDescription(true, "%", numberDescription)));
        Assert.assertEquals("percentage/post/%/float//.", TestHelper.render(
                new FloatPercentageFieldDescription(false, "%", numberDescription)));
        Assert.assertEquals("PercentageFieldDescription(false, %, FloatFieldDescription(, .))",
                new FloatPercentageFieldDescription(false, "%", numberDescription).toString());
    }

    @Test
    public void testText() throws IOException {
        Assert.assertEquals("text", TestHelper.render(TextFieldDescription.INSTANCE));
        Assert.assertEquals("TextFieldDescription()", TextFieldDescription.INSTANCE.toString());
    }

    @Test
    public void testAny() throws IOException {
        Assert.assertEquals("any/a/b\\/c/d\\\\e", TestHelper.render(
                new AnyFieldDescription(Arrays.<String>asList("a", "b/c", "d\\e"))));
        Assert.assertEquals("any", TestHelper.render(
                new AnyFieldDescription(Collections.<String>emptyList())));
        Assert.assertEquals("AnyFieldDescription([a, b/c, d\\e])",
                new AnyFieldDescription(Arrays.<String>asList("a", "b/c", "d\\e")).toString());
    }
}