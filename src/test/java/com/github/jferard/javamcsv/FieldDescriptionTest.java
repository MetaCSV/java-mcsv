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
        Assert.assertEquals("boolean/T", TestHelper.render(new BooleanFieldDescription("T", "")));
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
    public void testCurrencyDecimal() throws IOException {
        FieldDescription<BigDecimal> numberDescription = new DecimalFieldDescription("", ".");
        Assert.assertEquals("currency/pre/$/decimal//.", TestHelper.render(
                new CurrencyDecimalFieldDescription(true, "$", numberDescription)));
        Assert.assertEquals("currency/post/€/decimal//.", TestHelper.render(
                new CurrencyDecimalFieldDescription(false, "€", numberDescription)));
        Assert.assertEquals("CurrencyFieldDescription(false, €, DecimalFieldDescription(, .))",
                new CurrencyDecimalFieldDescription(false, "€", numberDescription).toString());
        Assert.assertEquals("€", new CurrencyDecimalFieldDescription(false, "€", numberDescription)
                .getCurrencySymbol());
    }

    @Test
    public void testCurrencyInteger() throws IOException {
        FieldDescription<Long> numberDescription = new IntegerFieldDescription(null);
        CurrencyIntegerFieldDescription instance =
                new CurrencyIntegerFieldDescription(true, "$", numberDescription);
        Assert.assertEquals("currency/pre/$/integer", TestHelper.render(instance));
        Assert.assertEquals("currency/post/€/integer", TestHelper.render(
                new CurrencyIntegerFieldDescription(false, "€", numberDescription)));
        Assert.assertEquals("CurrencyFieldDescription(true, $, IntegerFieldDescription(null))",
                instance.toString());
        Assert.assertEquals(DataType.CURRENCY_INTEGER, instance.getDataType());
        Assert.assertEquals(Long.class, instance.getJavaType());
        Assert.assertEquals("$", new CurrencyIntegerFieldDescription(true, "$", numberDescription)
                .getCurrencySymbol());
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
    public void testPercentageDecimal() throws IOException {
        FieldDescription<BigDecimal> numberDescription = new DecimalFieldDescription("", ".");
        PercentageDecimalFieldDescription instance =
                new PercentageDecimalFieldDescription(false, "%", numberDescription);

        Assert.assertEquals("percentage/pre/%/decimal//.", TestHelper.render(
                new PercentageDecimalFieldDescription(true, "%", numberDescription)));
        Assert.assertEquals("percentage/post/%/decimal//.", TestHelper.render(
                instance));
        Assert.assertEquals("PercentageFieldDescription(false, %, DecimalFieldDescription(, .))",
                instance.toString());
        Assert.assertEquals(DataType.PERCENTAGE_DECIMAL, instance.getDataType());
        Assert.assertEquals(BigDecimal.class, instance.getJavaType());
    }

    @Test
    public void testPercentageFloat() throws IOException {
        FieldDescription<Double> numberDescription = new FloatFieldDescription("", ".");
        Assert.assertEquals("percentage/pre/%/float//.", TestHelper.render(
                new PercentageFloatFieldDescription(true, "%", numberDescription)));
        Assert.assertEquals("percentage/post/%/float//.", TestHelper.render(
                new PercentageFloatFieldDescription(false, "%", numberDescription)));
        Assert.assertEquals("PercentageFieldDescription(false, %, FloatFieldDescription(, .))",
                new PercentageFloatFieldDescription(false, "%", numberDescription).toString());
    }

    @Test
    public void testText() throws IOException {
        FieldDescription<String> instance = TextFieldDescription.INSTANCE;
        Assert.assertEquals("text", TestHelper.render(instance));
        Assert.assertEquals("TextFieldDescription()", instance.toString());
        Assert.assertEquals(DataType.TEXT, instance.getDataType());
        Assert.assertEquals(String.class, instance.getJavaType());
    }

    @Test
    public void testObject() throws IOException {
        ObjectFieldDescription instance =
                new ObjectFieldDescription(Arrays.<String>asList("a", "b/c", "d\\e"));
        Assert.assertEquals("object/a/b\\/c/d\\\\e", TestHelper.render(instance));
        Assert.assertEquals(Arrays.<String>asList("a", "b/c", "d\\e"), instance.getParameters());
        Assert.assertEquals("object",
                TestHelper.render(new ObjectFieldDescription(Collections.<String>emptyList())));
        Assert.assertEquals("ObjectFieldDescription([a, b/c, d\\e])", instance.toString());
        Assert.assertEquals(DataType.OBJECT, instance.getDataType());
        Assert.assertEquals(Object.class, instance.getJavaType());
    }
}