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

import org.junit.Assert;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class MetaCSVParserBuilderTest {
    @Test
    public void testFileDomain() throws IOException, MetaCSVParseException, MetaCSVDataException {
        ByteArrayInputStream in = TestHelper.utf8InputStream(
                        "file,encoding,ascii\r\n" +
                        "file,bom,false\r\n" +
                        "file,line_terminator,\\n\r\n");
        MetaCSVData data = new MetaCSVParserBuilder().metaIn(in).header(false).buildData();
        Assert.assertEquals(TestHelper.ASCII_CHARSET, data.getEncoding());
        Assert.assertFalse(data.isUtf8BOM());
        Assert.assertEquals("\n", data.getLineTerminator());
    }

    @Test
    public void testMetaNull() throws IOException, MetaCSVParseException, MetaCSVDataException {
        ByteArrayInputStream in = TestHelper.utf8InputStream(
                "file,encoding,ascii\r\n" +
                        "file,bom,false\r\n" +
                        "file,line_terminator,\\n\r\n");
        Assert.assertThrows(MetaCSVParseException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                new MetaCSVParserBuilder().buildData();
            }
        });
    }
}