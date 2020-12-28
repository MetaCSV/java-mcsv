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
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

public class IntegerCurrencyFieldProcessorTest {
    private FieldProcessor<Integer> processorPre;
    private FieldProcessor<Integer> processorPost;

    @Before
    public void setUp() {
        processorPre = new IntegerCurrencyFieldDescription(true, "$",
                new IntegerFieldDescription(null)
        ).toFieldProcessor("NULL");
        processorPost = new IntegerCurrencyFieldDescription(false, "€",
                new IntegerFieldDescription(null)
        ).toFieldProcessor("NULL");
    }

    @Test
    public void testNullToObject() throws MetaCSVReadException {
        Assert.assertNull(processorPre.toObject(null));
        Assert.assertNull(processorPre.toObject("NULL"));
    }

    @Test(expected = MetaCSVReadException.class)
    public void testWrongPreToObject() throws MetaCSVReadException {
        Assert.assertNull(processorPre.toObject("€10.0"));
    }

    @Test
    public void testRightPreToObject() throws MetaCSVReadException {
        Assert.assertEquals(10, (int) processorPre.toObject("$10"));
    }

    @Test(expected = MetaCSVReadException.class)
    public void testWrongPostToObject() throws MetaCSVReadException {
        Assert.assertNull(processorPost.toObject("10$"));
    }

    @Test
    public void testRightPostToObject() throws MetaCSVReadException {
        Assert.assertEquals(10, (int) processorPost.toObject("10 €"));
    }

    @Test
    public void testNullToString() {
        Assert.assertEquals("NULL", processorPre.toString(null));
    }

    @Test
    public void testPreToString() {
        Assert.assertEquals("$2560", processorPre.toString(2560));
    }

    @Test
    public void testPostToString() {
        Assert.assertEquals("2560€", processorPost.toString(2560));
    }

    @Test
    public void testIntegerToString() {
        FieldProcessor<Integer> processor = new IntegerCurrencyFieldDescription(false, "€",
                IntegerFieldDescription.INSTANCE).toFieldProcessor("NULL");
        Assert.assertEquals("17€", processor.toString(17));
    }
}