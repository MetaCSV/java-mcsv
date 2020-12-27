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

public class MetaCSVDataTest {
    @Test(expected = MetaCSVDataException.class)
    public void test() throws MetaCSVDataException {
        new MetaCSVDataBuilder().encoding("ASCII").bom(true).build();
    }

    @Test
    public void testToString() throws MetaCSVDataException {
        MetaCSVData data = new MetaCSVDataBuilder().build();
        Assert.assertEquals("MetaCSVData(encoding=UTF-8, lineTerminator=\\r\\n" +
                        ", delimiter=,, doubleQuote=true, escapeChar=\0, quoteChar=\", skipInitialSpace=false, descriptionByColIndex={})",
                data.toString());
    }

    @Test
    public void testSig() throws MetaCSVDataException {
        MetaCSVData data = new MetaCSVDataBuilder().encoding("UTF-8-SIG").build();
        Assert.assertEquals(Util.UTF_8_CHARSET, data.getEncoding());
        Assert.assertTrue(data.isUtf8BOM());
    }
}