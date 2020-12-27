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

public class TextFieldProcessorTest {

    private FieldProcessor<String> processor;

    @Before
    public void setUp() {
        processor = TextFieldDescription.INSTANCE.toFieldProcessor("NULL");
    }

    @Test
    public void testNullToObject() throws MetaCSVReadException {
        Assert.assertNull(processor.toObject(null));
        Assert.assertNull(processor.toObject("NULL"));
    }

    @Test
    public void testToObject() throws MetaCSVReadException {
        Assert.assertEquals("foo", processor.toObject("foo"));
    }

    @Test
    public void testToString() throws MetaCSVWriteException {
        Assert.assertEquals("NULL", processor.toString(null));
        Assert.assertEquals("bar", processor.toString("bar"));
    }
}