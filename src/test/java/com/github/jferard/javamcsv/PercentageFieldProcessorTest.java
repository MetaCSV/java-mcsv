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

public class PercentageFieldProcessorTest {
    private FieldProcessor<Double> processorPre;
    private FieldProcessor<Double> processorPost;

    @Before
    public void setUp() {
        processorPre = new PercentageFieldDescription(true, "%",
                new FloatFieldDescription(null, ".")
        ).toFieldProcessor("NULL");
        processorPost = new PercentageFieldDescription(false, "%",
                new FloatFieldDescription(null, ",")
        ).toFieldProcessor("NULL");
    }

    @Test
    public void testNullToObject() throws MetaCSVReadException {
        Assert.assertNull(processorPre.toObject(null));
        Assert.assertNull(processorPre.toObject("NULL"));
    }

    @Test(expected = MetaCSVReadException.class)
    public void testWrongPreToObject() throws MetaCSVReadException {
        Assert.assertNull(processorPre.toObject("10.0"));
    }

    @Test
    public void testRightPreToObject() throws MetaCSVReadException {
        Assert.assertEquals(0.1, (double) processorPre.toObject("%10.0"), 0.001);
    }

    @Test(expected = MetaCSVReadException.class)
    public void testWrongPostToObject() throws MetaCSVReadException {
        Assert.assertNull(processorPost.toObject("%10,0"));
    }

    @Test
    public void testRightPostToObject() throws MetaCSVReadException {
        Assert.assertEquals(0.1, (double) processorPost.toObject("10,0 %"), 0.001);
    }

    @Test
    public void testNullToString() throws MetaCSVWriteException {
        Assert.assertEquals("NULL", processorPre.toString(null));
    }

    @Test
    public void testPreToString() throws MetaCSVWriteException {
        Assert.assertEquals("%1720", processorPre.toString(17.2));
    }

    @Test
    public void testPostToString() throws MetaCSVWriteException {
        Assert.assertEquals("1720%", processorPost.toString(17.2));
    }
}