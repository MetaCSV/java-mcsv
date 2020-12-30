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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class MetaCSVWriterTest {
    @Test
    public void test() throws IOException, MetaCSVDataException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream metaOut = new ByteArrayOutputStream();
        MetaCSVData data =
                new MetaCSVDataBuilder().nullValue("NULL").
                        colType(0, new BooleanFieldDescription("T", "F")).
                        colType(1, new CurrencyDecimalFieldDescription(true, "$",
                                new DecimalFieldDescription(",", "."))).
                        colType(2,
                                new DateFieldDescription(new SimpleDateFormat("dd/MM/yyyy"), null)).
                        colType(3,
                                new DatetimeFieldDescription(new SimpleDateFormat(
                                        "yyyy-MM-dd HH:mm:ss"), null)).
                        colType(4,
                                new FloatFieldDescription(",", ".")).
                        colType(5,
                                new IntegerFieldDescription(" ")).
                        colType(6,
                                new PercentageFloatFieldDescription(false, "%",
                                        new FloatFieldDescription(",", "."))).
                        build();
        MetaCSVWriter writer = MetaCSVWriter.create(out, metaOut, data);
        Calendar c = GregorianCalendar.getInstance(Locale.US);
        c.set(2020, Calendar.DECEMBER, 1, 9, 30, 55);

        writer.writeHeader(Arrays.<String>asList("boolean", "currency", "date", "datetime", "float",
                "integer", "percentage", "text"));
        writer.writeRow(
                Arrays.<Object>asList(true, new BigDecimal("15.0"), c.getTime(), null, 10000.5,
                        12354L, 0.565, "Foo"));
        writer.writeRow(
                Arrays.<Object>asList(false, new BigDecimal("-1900.5"), null, c.getTime(), -520.8,
                        -1000L, -0.128, "Bar"));
        writer.close();

        Assert.assertEquals("boolean,currency,date,datetime,float,integer,percentage,text\r\n" +
                        "T,$15,01/12/2020,NULL,\"10,000.5\",12 354,56.49999999999999%,Foo\r\n" +
                        "F,\"$-1,900.5\",NULL,2020-12-01 09:30:55,-520.8,-1 000,-12.8%,Bar\r\n",
                new String(out.toByteArray()));
        Assert.assertEquals("domain,key,value\r\n" +
                        "data,null_value,NULL\r\n" +
                        "data,col/0/type,boolean/T/F\r\n" +
                        "data,col/1/type,\"currency/pre/$/decimal/,/.\"\r\n" +
                        "data,col/2/type,date/dd\\/MM\\/yyyy\r\n" +
                        "data,col/3/type,datetime/yyyy-MM-dd HH:mm:ss\r\n" +
                        "data,col/4/type,\"float/,/.\"\r\n" +
                        "data,col/5/type,\"integer/ \"\r\n" +
                        "data,col/6/type,\"percentage/post/%/float/,/.\"\r\n" +
                        ""
                , new String(metaOut.toByteArray()));
    }
}