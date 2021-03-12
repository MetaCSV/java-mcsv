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

package com.github.jferard.javamcsv.it;

import com.github.jferard.javamcsv.MetaCSVData;
import com.github.jferard.javamcsv.MetaCSVDataException;
import com.github.jferard.javamcsv.MetaCSVParseException;
import com.github.jferard.javamcsv.MetaCSVParser;
import com.github.jferard.javamcsv.MetaCSVRenderer;
import com.github.jferard.javamcsv.TestHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MetaCSVParserIT {
    @Test
    public void testMetaMinimal() throws IOException, MetaCSVParseException, MetaCSVDataException {
        MetaCSVData metaCSVData = getMetaCSVData("meta_csv.mcsv");
        Assert.assertEquals("domain,key,value\r\n" +
                "data,col/2/type,object\r\n", this.render(metaCSVData, true));
    }

    @Test
    public void testMetaVerbose() throws IOException, MetaCSVParseException, MetaCSVDataException {
        MetaCSVData metaCSVData = getMetaCSVData("meta_csv.mcsv");
        Assert.assertEquals("domain,key,value\r\n" +
                "file,encoding,UTF-8\r\n" +
                "file,bom,false\r\n" +
                "file,line_terminator,\\r\\n\r\n" +
                "csv,delimiter,\",\"\r\n" +
                "csv,double_quote,true\r\n" +
                "csv,quote_char,\"\"\"\"\r\n" +
                "csv,skip_initial_space,false\r\n" +
                "data,null_value,\r\n" +
                "data,col/0/type,text\r\n" +
                "data,col/1/type,text\r\n" +
                "data,col/2/type,object\r\n", this.render(metaCSVData, false));
    }

    @Test
    public void testMetaExampleMinimal()
            throws IOException, MetaCSVParseException, MetaCSVDataException {
        MetaCSVData metaCSVData = getMetaCSVData("example.mcsv");
        String actual = render(metaCSVData, true);
        Assert.assertEquals("domain,key,value\r\n" +
                "data,col/1/type,date/yyyy-MM-dd\r\n" +
                "data,col/2/type,integer\r\n", actual);
    }

    @Test
    public void testMetaExampleVerbose()
            throws IOException, MetaCSVParseException, MetaCSVDataException {
        MetaCSVData metaCSVData = getMetaCSVData("example.mcsv");
        String actual = render(metaCSVData, false);
        Assert.assertEquals("domain,key,value\r\n" +
                "file,encoding,UTF-8\r\n" +
                "file,bom,false\r\n" +
                "file,line_terminator,\\r\\n\r\n" +
                "csv,delimiter,\",\"\r\n" +
                "csv,double_quote,true\r\n" +
                "csv,quote_char,\"\"\"\"\r\n" +
                "csv,skip_initial_space,false\r\n" +
                "data,null_value,\r\n" +
                "data,col/1/type,date/yyyy-MM-dd\r\n" +
                "data,col/2/type,integer\r\n", actual);
    }

    @Test
    public void testMetaLongFile() throws IOException, MetaCSVParseException, MetaCSVDataException {
        MetaCSVData metaCSVData = getMetaCSVData("20201001-bal-216402149.mcsv");
        String actual = render(metaCSVData, false);
        Assert.assertEquals("domain,key,value\r\n" +
                "file,encoding,UTF-8\r\n" +
                "file,bom,true\r\n" +
                "file,line_terminator,\\r\\n\r\n" +
                "csv,delimiter,;\r\n" +
                "csv,double_quote,false\r\n" +
                "csv,escape_char,\\\r\n" +
                "csv,quote_char,\"\"\"\"\r\n" +
                "csv,skip_initial_space,false\r\n" +
                "data,null_value,\r\n" +
                "data,col/3/type,integer\r\n" +
                "data,col/7/type,float//.\r\n" +
                "data,col/8/type,float//.\r\n" +
                "data,col/9/type,float//.\r\n" +
                "data,col/10/type,float//.\r\n" +
                "data,col/12/type,date/yyyy-MM-dd\r\n", actual);
    }

    private String render(MetaCSVData metaCSVData, boolean minimal) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        MetaCSVRenderer renderer = MetaCSVRenderer.create(bos, minimal);
        renderer.render(metaCSVData);
        return bos.toString(TestHelper.UTF_8_CHARSET_NAME);
    }

    private MetaCSVData getMetaCSVData(String metaCSVFilename)
            throws IOException, MetaCSVParseException, MetaCSVDataException {
        InputStream is =
                MetaCSVParserIT.class.getClassLoader().getResourceAsStream(metaCSVFilename);
        MetaCSVParser parser = MetaCSVParser.create(is);
        return parser.parse();
    }
}