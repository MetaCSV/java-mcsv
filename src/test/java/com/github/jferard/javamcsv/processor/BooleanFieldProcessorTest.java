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

package com.github.jferard.javamcsv.processor;

import com.github.jferard.javamcsv.MetaCSVReadException;
import com.github.jferard.javamcsv.description.BooleanFieldDescription;
import com.github.jferard.javamcsv.processor.FieldProcessor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BooleanFieldProcessorTest {
    private FieldProcessor<Boolean> processor;

    @Before
    public void setUp() {
        processor = new BooleanFieldDescription("T", "F").toFieldProcessor("NULL");
    }

    @Test
    public void testToObject() throws MetaCSVReadException {
        Assert.assertNull(processor.toObject(null));
        Assert.assertNull(processor.toObject("NULL"));
        Assert.assertTrue(processor.toObject("T"));
        Assert.assertFalse(processor.toObject("F"));
        Assert.assertTrue(processor.toObject("t"));
        Assert.assertFalse(processor.toObject("f"));
    }

    @Test(expected = MetaCSVReadException.class)
    public void testToObjectError() throws MetaCSVReadException {
        Assert.assertFalse(processor.toObject("foo"));
    }

    @Test
    public void testToString() {
        Assert.assertEquals("NULL", processor.toString(null));
        Assert.assertEquals("T", processor.toString(true));
        Assert.assertEquals("F", processor.toString(false));
    }

    @Test
    public void testCase() throws MetaCSVReadException {
        FieldProcessor<Boolean> aProcessor =
                new BooleanFieldDescription("true", "false").toFieldProcessor("NULL");
        Assert.assertTrue(aProcessor.toObject("True"));
        Assert.assertFalse(aProcessor.toObject("False"));
    }
}