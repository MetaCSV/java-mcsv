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

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class MetaCSVParserTest {
    @Test(expected = MetaCSVParseException.class)
    public void testEmptyStream() throws IOException, MetaCSVParseException, MetaCSVDataException {
        ByteArrayInputStream is = TestHelper.utf8InputStream("");
        MetaCSVParser.create(is).parse();
    }

    @Test
    public void testEmptyBody() throws IOException, MetaCSVParseException, MetaCSVDataException {
        ByteArrayInputStream is = TestHelper.utf8InputStream("domain,key,value\r\n");
        MetaCSVData data = MetaCSVParser.create(is).parse();
        Assert.assertEquals(TestHelper.UTF_8_CHARSET, data.getEncoding());
        Assert.assertFalse(data.isUtf8BOM());
        Assert.assertEquals("\r\n", data.getLineTerminator());
        Assert.assertEquals(',', data.getDelimiter());
        Assert.assertTrue(data.isDoubleQuote());
        Assert.assertEquals(0, data.getEscapeChar());
        Assert.assertEquals('"', data.getQuoteChar());
        Assert.assertFalse(data.isSkipInitialSpace());
    }

    @Test(expected = MetaCSVParseException.class)
    public void testBadHeader1() throws IOException, MetaCSVParseException, MetaCSVDataException {
        ByteArrayInputStream is = TestHelper.utf8InputStream("domai,key,value\r\n");
        MetaCSVParser.create(is).parse();
    }

    @Test(expected = MetaCSVParseException.class)
    public void testBadHeader2() throws IOException, MetaCSVParseException, MetaCSVDataException {
        ByteArrayInputStream is = TestHelper.utf8InputStream("domain,ke,value\r\n");
        MetaCSVParser.create(is).parse();
    }

    @Test(expected = MetaCSVParseException.class)
    public void testBadHeader3() throws IOException, MetaCSVParseException, MetaCSVDataException {
        ByteArrayInputStream is = TestHelper.utf8InputStream("domain,key,valu\r\n");
        MetaCSVParser.create(is).parse();
    }

    @Test(expected = MetaCSVParseException.class)
    public void testUnknownDomain() throws IOException, MetaCSVParseException,
            MetaCSVDataException {
        ByteArrayInputStream is = TestHelper.utf8InputStream(
                "domain,key,value\r\n" +
                        "foo,bar,baz\r\n");
        MetaCSVParser.create(is).parse();
    }

    @Test
    public void testFileDomain() throws IOException, MetaCSVParseException, MetaCSVDataException {
        ByteArrayInputStream is = TestHelper.utf8InputStream(
                "domain,key,value\r\n" +
                        "file,encoding,ascii\r\n" +
                        "file,bom,false\r\n" +
                        "file,line_terminator,\\n\r\n");
        MetaCSVData data = MetaCSVParser.create(is).parse();
        Assert.assertEquals(TestHelper.ASCII_CHARSET, data.getEncoding());
        Assert.assertFalse(data.isUtf8BOM());
        Assert.assertEquals("\n", data.getLineTerminator());
    }

    @Test(expected = MetaCSVParseException.class)
    public void testUnknownFileKey() throws IOException, MetaCSVParseException,
            MetaCSVDataException {
        ByteArrayInputStream is = TestHelper.utf8InputStream(
                "domain,key,value\r\n" +
                        "file,bar,baz\r\n");
        MetaCSVParser.create(is).parse();
    }

    @Test
    public void testCSVDomain() throws IOException, MetaCSVParseException, MetaCSVDataException {
        ByteArrayInputStream is = TestHelper.utf8InputStream(
                "domain,key,value\r\n" +
                        "csv,delimiter,\",\"\r\n" +
                        "csv,double_quote,true\r\n" +
                        "csv,escape_char,\\\r\n" +
                        "csv,quote_char,'\r\n" +
                        "csv,skip_initial_space,false\r\n");
        MetaCSVData data = MetaCSVParser.create(is).parse();
        Assert.assertEquals(',', data.getDelimiter());
        Assert.assertTrue(data.isDoubleQuote());
        Assert.assertEquals('\\', data.getEscapeChar());
        Assert.assertEquals('\'', data.getQuoteChar());
        Assert.assertFalse(data.isSkipInitialSpace());
    }

    @Test(expected = MetaCSVParseException.class)
    public void testUnknownCSVKey() throws IOException, MetaCSVParseException,
            MetaCSVDataException {
        ByteArrayInputStream is = TestHelper.utf8InputStream(
                "domain,key,value\r\n" +
                        "csv,bar,baz\r\n");
        MetaCSVParser.create(is).parse();
    }

    @Test(expected = MetaCSVParseException.class)
    public void testUnknownBoolean() throws IOException, MetaCSVParseException,
            MetaCSVDataException {
        ByteArrayInputStream is = TestHelper.utf8InputStream(
                "domain,key,value\r\n" +
                        "csv,double_quote,T\r\n");
        MetaCSVParser.create(is).parse();
    }

    @Test(expected = MetaCSVParseException.class)
    public void testUnknownChar() throws IOException, MetaCSVParseException, MetaCSVDataException {
        ByteArrayInputStream is = TestHelper.utf8InputStream(
                "domain,key,value\r\n" +
                        "csv,delimiter,foo\r\n");
        MetaCSVParser.create(is).parse();
    }

    @Test
    public void testDataDomain() throws IOException, MetaCSVParseException, MetaCSVDataException {
        ByteArrayInputStream is = TestHelper.utf8InputStream(
                "domain,key,value\r\n" +
                        "data,null_value,NULL\r\n" +
                        "data,col/0/type,boolean/T/F\r\n" +
                        "data,col/1/type,\"currency/pre/$/float/,/.\"\r\n" +
                        "data,col/2/type,date/dd\\/MM\\/yyyy\r\n" +
                        "data,col/3/type,datetime/yyyy-MM-dd HH:mm:ss\r\n" +
                        "data,col/4/type,\"float/,/.\"\r\n" +
                        "data,col/5/type,\"integer/ \"\r\n" +
                        "data,col/6/type,\"percentage/post/%/float/,/.\"\r\n" +
                        "data,col/7/type,text\r\n" +
                        "data,col/8/type,any/foo/bar/baz\r\n"
        );
        MetaCSVData data = MetaCSVParser.create(is).parse();
        Assert.assertEquals("BooleanFieldDescription(T, F)",
                data.getDescriptionByIndex().get(0).toString());
        Assert.assertEquals("CurrencyFieldDescription(true, $, FloatFieldDescription(,, .))",
                data.getDescriptionByIndex().get(1).toString());
        Assert.assertEquals("DateFieldDescription(dd/MM/yyyy, null)",
                data.getDescriptionByIndex().get(2).toString());
        Assert.assertEquals("DatetimeDescription(yyyy-MM-dd HH:mm:ss, null)",
                data.getDescriptionByIndex().get(3).toString());
        Assert.assertEquals("FloatFieldDescription(,, .)",
                data.getDescriptionByIndex().get(4).toString());
        Assert.assertEquals("IntegerFieldDescription( )",
                data.getDescriptionByIndex().get(5).toString());
        Assert.assertEquals("PercentageFieldDescription(false, %, FloatFieldDescription(,, .))",
                data.getDescriptionByIndex().get(6).toString());
        Assert.assertEquals("TextFieldDescription()",
                data.getDescriptionByIndex().get(7).toString());
        Assert.assertEquals("AnyFieldDescription([foo, bar, baz])",
                data.getDescriptionByIndex().get(8).toString());
    }

    @Test(expected = MetaCSVParseException.class)
    public void testBadDataNullKey() throws IOException, MetaCSVParseException,
            MetaCSVDataException {
        ByteArrayInputStream is = TestHelper.utf8InputStream(
                "domain,key,value\r\n" +
                        "data,null_value/foo,NULL\r\n");
        MetaCSVParser.create(is).parse();
    }

    @Test(expected = MetaCSVParseException.class)
    public void testBadDataColKey() throws IOException, MetaCSVParseException,
            MetaCSVDataException {
        ByteArrayInputStream is = TestHelper.utf8InputStream(
                "domain,key,value\r\n" +
                        "data,col/1/type/foo,bar\r\n");
        MetaCSVParser.create(is).parse();
    }

    @Test(expected = MetaCSVParseException.class)
    public void testUnkownDataKey() throws IOException, MetaCSVParseException,
            MetaCSVDataException {
        ByteArrayInputStream is = TestHelper.utf8InputStream(
                "domain,key,value\r\n" +
                        "data,foo,bar\r\n");
        MetaCSVParser.create(is).parse();
    }

    @Test(expected = MetaCSVParseException.class)
    public void testBadColNum() throws IOException, MetaCSVParseException, MetaCSVDataException {
        ByteArrayInputStream is = TestHelper.utf8InputStream(
                "domain,key,value\r\n" +
                        "data,col/foo/type,bar\r\n");
        MetaCSVParser.create(is).parse();
    }

    @Test(expected = MetaCSVParseException.class)
    public void testBadColSubKey() throws IOException, MetaCSVParseException, MetaCSVDataException {
        ByteArrayInputStream is = TestHelper.utf8InputStream(
                "domain,key,value\r\n" +
                        "data,col/3/foo,bar\r\n");
        MetaCSVParser.create(is).parse();
    }

    @Test(expected = MetaCSVParseException.class)
    public void testUnknownColType() throws IOException, MetaCSVParseException,
            MetaCSVDataException {
        ByteArrayInputStream is = TestHelper.utf8InputStream(
                "domain,key,value\r\n" +
                        "data,col/3/type,foo\r\n");
        MetaCSVParser.create(is).parse();
    }

    @Test
    public void testMissingFalse() throws IOException, MetaCSVParseException, MetaCSVDataException {
        ByteArrayInputStream is = TestHelper.utf8InputStream(
                "domain,key,value\r\n" +
                        "data,col/3/type,boolean/X\r\n");
        MetaCSVData data = MetaCSVParser.create(is).parse();
        Assert.assertEquals("BooleanFieldDescription(X, )",
                data.getDescriptionByIndex().get(3).toString());
    }

    @Test(expected = MetaCSVParseException.class)
    public void testMissingBooleanParameters() throws IOException, MetaCSVParseException, MetaCSVDataException {
        ByteArrayInputStream is = TestHelper.utf8InputStream(
                "domain,key,value\r\n" +
                        "data,col/3/type,boolean\r\n");
        MetaCSVParser.create(is).parse();
    }

    @Test(expected = MetaCSVParseException.class)
    public void testTooManyBooleanParameters() throws IOException, MetaCSVParseException, MetaCSVDataException {
        ByteArrayInputStream is = TestHelper.utf8InputStream(
                "domain,key,value\r\n" +
                        "data,col/3/type,boolean/true/false/maybe\r\n");
        MetaCSVParser.create(is).parse();
    }

    @Test
    public void testCurrencyInteger() throws IOException, MetaCSVParseException, MetaCSVDataException {
        ByteArrayInputStream is = TestHelper.utf8InputStream(
                "domain,key,value\r\n" +
                        "data,col/2/type,currency/pre/$/integer\r\n");
        MetaCSVData data = MetaCSVParser.create(is).parse();
        Assert.assertEquals("CurrencyFieldDescription(true, $, IntegerFieldDescription())",
                data.getDescriptionByIndex().get(2).toString());
    }

    @Test(expected = MetaCSVParseException.class)
    public void testCurrencyOther() throws IOException, MetaCSVParseException, MetaCSVDataException {
        ByteArrayInputStream is = TestHelper.utf8InputStream(
                "domain,key,value\r\n" +
                        "data,col/2/type,currency/pre/$/foo/a/b\r\n");
        MetaCSVParser.create(is).parse();
    }

    @Test
    public void testDateLocale() throws IOException, MetaCSVParseException, MetaCSVDataException {
        ByteArrayInputStream is = TestHelper.utf8InputStream(
                "domain,key,value\r\n" +
                        "data,col/1/type,date/YYYY/fr_FR\r\n");
        MetaCSVData data = MetaCSVParser.create(is).parse();
        Assert.assertEquals("DateFieldDescription(YYYY, fr_FR)",
                data.getDescriptionByIndex().get(1).toString());
    }

    @Test(expected = MetaCSVParseException.class)
    public void testDateNoParameter() throws IOException, MetaCSVParseException, MetaCSVDataException {
        ByteArrayInputStream is = TestHelper.utf8InputStream(
                "domain,key,value\r\n" +
                        "data,col/1/type,date\r\n");
        MetaCSVParser.create(is).parse();
    }

    @Test(expected = MetaCSVParseException.class)
    public void testDateTooManyParameters() throws IOException, MetaCSVParseException, MetaCSVDataException {
        ByteArrayInputStream is = TestHelper.utf8InputStream(
                "domain,key,value\r\n" +
                        "data,col/1/type,date/YYYY/fr_FR/foo\r\n");
        MetaCSVParser.create(is).parse();
    }
}