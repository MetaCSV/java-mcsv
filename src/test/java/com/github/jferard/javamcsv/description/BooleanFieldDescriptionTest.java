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

package com.github.jferard.javamcsv.description;

import com.github.jferard.javamcsv.DataType;
import com.github.jferard.javamcsv.TestHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class BooleanFieldDescriptionTest {
    private BooleanFieldDescription booleanFieldDescriptionTF;

    @Before
    public void setUp() throws Exception {
        booleanFieldDescriptionTF = new BooleanFieldDescription("T", "F");
    }

    @Test
    public void testNoFalse() throws IOException {
        Assert.assertEquals("boolean/T", TestHelper.render(new BooleanFieldDescription("T", "")));
    }

    @Test
    public void testSlashes() throws IOException {
        Assert.assertEquals("boolean/\\/\\\\/\\\\\\/",
                TestHelper.render(new BooleanFieldDescription("/\\", "\\/")));
    }

    @Test
    public void test() throws IOException {
        Assert.assertEquals("boolean/T/F",
                TestHelper.render(booleanFieldDescriptionTF));
    }

    @Test
    public void testToString() {
        Assert.assertEquals("BooleanFieldDescription(T, F)",
                booleanFieldDescriptionTF.toString());
    }

    @Test
    public void testDataType() {
        Assert.assertEquals(DataType.BOOLEAN,
                booleanFieldDescriptionTF.getDataType());
    }

    @Test
    public void testJavaType() {
        Assert.assertEquals(Boolean.class,
                booleanFieldDescriptionTF.getJavaType());
    }
}
