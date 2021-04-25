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
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class MetaCSVWriterBuilderTest {
    @Test
    public void testCsvFile()
            throws MetaCSVDataException, MetaCSVReadException, MetaCSVParseException, IOException {
        File csvFile = File.createTempFile("test", ".csv");
        csvFile.deleteOnExit();

        MetaCSVData data =
                new MetaCSVDataBuilder().colType(1, IntegerFieldDescription.INSTANCE).build();
        MetaCSVWriter writer = new MetaCSVWriterBuilder().csvFile(csvFile)
                .metaData(data).build();
        try {
            writer.writeHeader(Arrays.asList("a", "b", "c"));
            writer.writeRow(Arrays.<Object>asList("1", 2L, "3"));
        } finally {
            writer.close();
        }

        Assert.assertEquals("a,b,c\r\n" +
                "1,2,3\r\n", TestHelper.toString(csvFile));
        Assert.assertEquals("domain,key,value\r\n" +
                        "data,col/1/type,integer\r\n",
                TestHelper.toString(Util.withExtension(csvFile, ".mcsv")));
    }

    @Test
    public void testCsvMCsvFiles()
            throws MetaCSVDataException, MetaCSVReadException, MetaCSVParseException, IOException {
        File csvFile = File.createTempFile("test", ".csv");
        File mcsvFile = File.createTempFile("test", ".mcsv");
        csvFile.deleteOnExit();
        mcsvFile.deleteOnExit();

        MetaCSVData data =
                new MetaCSVDataBuilder().colType(1, IntegerFieldDescription.INSTANCE).build();
        MetaCSVWriter writer = new MetaCSVWriterBuilder().csvFile(csvFile)
                .metaCSVFile(mcsvFile).metaData(data).build();
        try {
            writer.writeHeader(Arrays.asList("a", "b", "c"));
            writer.writeRow(Arrays.<Object>asList("1", 2L, "3"));
        } finally {
            writer.close();
        }

        Assert.assertEquals("a,b,c\r\n" +
                "1,2,3\r\n", TestHelper.toString(csvFile));
        Assert.assertEquals("domain,key,value\r\n" +
                "data,col/1/type,integer\r\n", TestHelper.toString(mcsvFile));
    }

    @Test
    public void testRenderer()
            throws MetaCSVDataException, MetaCSVReadException, MetaCSVParseException, IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream metaOut = new ByteArrayOutputStream();

        MetaCSVData data =
                new MetaCSVDataBuilder().colType(1, IntegerFieldDescription.INSTANCE).build();
        MetaCSVRenderer renderer = MetaCSVRenderer.create(metaOut);
        MetaCSVWriter writer = new MetaCSVWriterBuilder().metaRenderer(renderer).out(out)
                .metaData(data).build();
        try {
            writer.writeHeader(Arrays.asList("a", "b", "c"));
            writer.writeRow(Arrays.<Object>asList("1", 2L, "3"));
        } finally {
            writer.close();
        }

        Assert.assertEquals("a,b,c\r\n" +
                "1,2,3\r\n", out.toString(TestHelper.UTF_8_CHARSET_NAME));
        Assert.assertEquals("domain,key,value\r\n" +
                "data,col/1/type,integer\r\n", metaOut.toString(TestHelper.UTF_8_CHARSET_NAME));
    }

    @Test
    public void testOnError()
            throws MetaCSVDataException, MetaCSVReadException, MetaCSVParseException, IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream metaOut = new ByteArrayOutputStream();

        MetaCSVData data =
                new MetaCSVDataBuilder().colType(1, IntegerFieldDescription.INSTANCE).build();
        MetaCSVWriter writer = new MetaCSVWriterBuilder().out(out).metaOut(metaOut)
                .metaData(data).onError(OnError.TEXT).build();
        try {
            writer.writeHeader(Arrays.asList("a", "b", "c"));
            writer.writeRow(Arrays.<Object>asList("1", OnError.TEXT, "3"));
        } finally {
            writer.close();
        }

        Assert.assertEquals("a,b,c\r\n" +
                "1,TEXT,3\r\n", out.toString(TestHelper.UTF_8_CHARSET_NAME));
        Assert.assertEquals("domain,key,value\r\n" +
                "data,col/1/type,integer\r\n", metaOut.toString(TestHelper.UTF_8_CHARSET_NAME));
    }
}