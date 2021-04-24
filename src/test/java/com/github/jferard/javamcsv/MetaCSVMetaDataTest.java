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

import com.github.jferard.javamcsv.description.IntegerFieldDescription;
import com.github.jferard.javamcsv.description.TextFieldDescription;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class MetaCSVMetaDataTest {
    @Test
    public void testDescription() throws MetaCSVDataException, IOException {
        MetaCSVData data =
                new MetaCSVDataBuilder().colType(1, IntegerFieldDescription.INSTANCE).build();
        MetaCSVMetaData metaData = data.getMetaData();
        Assert.assertEquals(TextFieldDescription.INSTANCE, metaData.getDescription(0));
        Assert.assertEquals(DataType.TEXT, metaData.getDataType(0));
        Assert.assertEquals(String.class, metaData.getJavaType(0));
        Assert.assertEquals(IntegerFieldDescription.INSTANCE, metaData.getDescription(1));
        Assert.assertEquals(DataType.INTEGER, metaData.getDataType(1));
        Assert.assertEquals(Long.class, metaData.getJavaType(1));

    }

    @Test
    public void testGetDescription() throws MetaCSVDataException, IOException {
        MetaCSVMetaData data = new MetaCSVDataBuilder().build().getMetaData();
        Assert.assertEquals(TextFieldDescription.INSTANCE, data.getDescription(0));
        Assert.assertEquals(TextFieldDescription.INSTANCE, data.getDescription(0, TextFieldDescription.class));
    }
}