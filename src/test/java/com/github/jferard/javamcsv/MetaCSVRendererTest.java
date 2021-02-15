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

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class MetaCSVRendererTest {
    @Test
    public void testDefaultDataVerbose() throws IOException, MetaCSVDataException {
        StringBuilder sb = new StringBuilder();
        MetaCSVRenderer renderer =
                MetaCSVRenderer.create(new CSVPrinter(sb, CSVFormat.DEFAULT), false);
        renderer.render(new MetaCSVDataBuilder().build());
        Assert.assertEquals("domain,key,value\r\n" +
                "file,encoding,UTF-8\r\n" +
                "file,bom,false\r\n" +
                "file,line_terminator,\\r\\n\r\n" +
                "csv,delimiter,\",\"\r\n" +
                "csv,double_quote,true\r\n" +
                "csv,quote_char,\"\"\"\"\r\n" +
                "csv,skip_initial_space,false\r\n" +
                "data,null_value,\r\n", sb.toString());
    }

    @Test
    public void testDefaultDataMinimal() throws IOException, MetaCSVDataException {
        StringBuilder sb = new StringBuilder();
        MetaCSVRenderer renderer =
                MetaCSVRenderer.create(new CSVPrinter(sb, CSVFormat.DEFAULT), true);
        renderer.render(new MetaCSVDataBuilder().build());
        Assert.assertEquals("domain,key,value\r\n", sb.toString());
    }

    @Test
    public void testVerbose() throws IOException, MetaCSVDataException {
        StringBuilder sb = new StringBuilder();
        MetaCSVRenderer renderer =
                MetaCSVRenderer.create(new CSVPrinter(sb, CSVFormat.DEFAULT), true);
        MetaCSVData data = new MetaCSVDataBuilder().doubleQuote(false).escapeChar('\\')
                .colType(0, new IntegerFieldDescription("")).build();
        renderer.render(data);
        Assert.assertEquals("domain,key,value\r\n" +
                "csv,double_quote,false\r\n" +
                "csv,escape_char,\\\r\n" +
                "data,col/0/type,integer\r\n", sb.toString());
    }

    @Test
    public void testAsciiMinimal() throws IOException, MetaCSVDataException {
        StringBuilder sb = new StringBuilder();
        MetaCSVRenderer renderer =
                MetaCSVRenderer.create(new CSVPrinter(sb, CSVFormat.DEFAULT), true);
        MetaCSVData data = new MetaCSVDataBuilder().encoding("ASCII").lineTerminator("\n")
                .delimiter(';').doubleQuote(false).escapeChar('\\').quoteChar('\'')
                .skipInitialSpace(true).nullValue("<NULL>")
                .colType(0, new IntegerFieldDescription(""))
                .colType(1, TextFieldDescription.INSTANCE).build();
        renderer.render(data);
        Assert.assertEquals("domain,key,value\r\n" +
                "file,encoding,US-ASCII\r\n" +
                "file,line_terminator,\\n\r\n" +
                "csv,delimiter,;\r\n" +
                "csv,double_quote,false\r\n" +
                "csv,escape_char,\\\r\n" +
                "csv,quote_char,'\r\n" +
                "csv,skip_initial_space,true\r\n" +
                "data,null_value,<NULL>\r\n" +
                "data,col/0/type,integer\r\n", sb.toString());
    }

    @Test
    public void testAsciiVerbose() throws IOException, MetaCSVDataException {
        StringBuilder sb = new StringBuilder();
        MetaCSVRenderer renderer =
                MetaCSVRenderer.create(new CSVPrinter(sb, CSVFormat.DEFAULT), false);
        MetaCSVData data = new MetaCSVDataBuilder().encoding("ASCII").lineTerminator("\n")
                .delimiter(';').doubleQuote(false).escapeChar('\\').quoteChar('\'')
                .skipInitialSpace(true).nullValue("<NULL>")
                .colType(0, new IntegerFieldDescription(""))
                .colType(1, TextFieldDescription.INSTANCE).build();
        renderer.render(data);
        Assert.assertEquals("domain,key,value\r\n" +
                "file,encoding,US-ASCII\r\n" +
                "file,bom,false\r\n" +
                "file,line_terminator,\\n\r\n" +
                "csv,delimiter,;\r\n" +
                "csv,double_quote,false\r\n" +
                "csv,escape_char,\\\r\n" +
                "csv,quote_char,'\r\n" +
                "csv,skip_initial_space,true\r\n" +
                "data,null_value,<NULL>\r\n" +
                "data,col/0/type,integer\r\n" +
                "data,col/1/type,text\r\n", sb.toString());
    }

    @Test
    public void testMinimalBom() throws IOException, MetaCSVDataException {
        StringBuilder sb = new StringBuilder();
        MetaCSVRenderer renderer =
                MetaCSVRenderer.create(new CSVPrinter(sb, CSVFormat.DEFAULT), true);
        MetaCSVData data = new MetaCSVDataBuilder().encoding("UTF-8").bom(true).build();
        renderer.render(data);
        Assert.assertEquals("domain,key,value\r\n" +
                "file,bom,true\r\n", sb.toString());
    }

    @Test
    public void testNullValueNull() throws IOException, MetaCSVDataException {
        StringBuilder sb = new StringBuilder();
        MetaCSVRenderer renderer =
                MetaCSVRenderer.create(new CSVPrinter(sb, CSVFormat.DEFAULT), true);
        MetaCSVData data = new MetaCSVDataBuilder().nullValue(null).build();
        renderer.render(data);
        Assert.assertEquals("domain,key,value\r\n", sb.toString());
    }
}