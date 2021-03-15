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
import java.math.BigDecimal;

public class DecimalFieldDescriptionTest {

    private DecimalFieldDescription fieldDescription;

    @Before
    public void setUp() throws Exception {
        fieldDescription = new DecimalFieldDescription(" ", ",");
    }

    @Test
    public void testDecimal() throws IOException {
        Assert.assertEquals("decimal/ /,", TestHelper.render(fieldDescription));
    }

    @Test
    public void testDataType() {
        Assert.assertEquals(DataType.DECIMAL, fieldDescription.getDataType());
    }

    @Test
    public void testJavaType() {
        Assert.assertEquals(BigDecimal.class, fieldDescription.getJavaType());
    }

    @Test
    public void testToString() throws IOException {
        Assert.assertEquals("DecimalFieldDescription( , ,)",
                fieldDescription.toString());
    }
}
