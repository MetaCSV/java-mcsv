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

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MetaCSVReaderTest {
    public static <T> List<T> toList(Iterable<T> iterable) {
        List<T> list = new ArrayList<T>();
        for (T e : iterable) {
            list.add(e);
        }
        return list;
    }

    public static <T> List<String> toRep(Iterable<T> iterable) {
        List<String> list = new ArrayList<String>();
        for (T e : iterable) {
            list.add(e.toString() + " (" + e.getClass() + ")");
        }
        return list;
    }

    @Test
    public void testMeta() throws IOException, MetaCSVParseException {
        InputStream is =
                getResourceAsStream("meta_csv.mcsv");
        InputStream metaIs =
                getResourceAsStream("meta_csv.mcsv");
        MetaCSVReader reader = MetaCSVReader.create(is, metaIs);
        for (MetaCSVRecord record : reader) {
            System.out.println(toList(record));
        }
    }

    @Test
    public void testLongFile() throws IOException, MetaCSVParseException, URISyntaxException {
        File f = getResourceAsFile("20201001-bal-216402149.csv");
        MetaCSVReader reader = MetaCSVReader.create(f);
        for (MetaCSVRecord record : reader) {
            System.out.println(record);
        }
    }

    @Test
    public void testExample() throws IOException, MetaCSVParseException, URISyntaxException {
        File f = getResourceAsFile("example.csv");
        MetaCSVReader reader = MetaCSVReader.create(f);
        System.out.println(reader.getTypes());
        // {1=date/YYYY-MM-dd, 2=integer}
        for (MetaCSVRecord record : reader) {
            System.out.println(record);
        }
        // MetaCSVRecord(CSVRecord [comment=null, mapping=null, recordNumber=1, values=[name, date, count]] ,[name, date, count])
        // MetaCSVRecord(CSVRecord [comment=null, mapping=null, recordNumber=2, values=[foo, 2020-11-21, 15]] ,[foo, Mon Dec 30 00:00:00 CET 2019, 15])
        // MetaCSVRecord(CSVRecord [comment=null, mapping=null, recordNumber=3, values=[foo, 2020-11-22, -8]] ,[foo, Mon Dec 30 00:00:00 CET 2019, -8])
    }

    private InputStream getResourceAsStream(String name) {
        return MetaCSVParserTest.class.getClassLoader().getResourceAsStream(name);
    }

    private File getResourceAsFile(String name) throws URISyntaxException {
        return new File(
                MetaCSVParserTest.class.getClassLoader().getResource(name).toURI().getPath());
    }

    @Test
    public void test() throws IOException, MetaCSVParseException {
        ByteArrayInputStream is = new ByteArrayInputStream(
                ("boolean,currency,date,datetime,float,integer,percentage,text\r\n" +
                        "T,$15,01/12/2020,NULL,\"10,000.5\",12 354,56.5%,Foo\r\n" +
                        "F,\"$-1,900.5\",NULL,2020-12-01 09:30:55,-520.8,-1 000,-12.8%,Bar\r\n")
                        .getBytes(Util.UTF_8_CHARSET));
        ByteArrayInputStream metaIs = new ByteArrayInputStream(("domain,key,value\r\n" +
                "data,null_value,NULL\r\n" +
                "data,col/0/type,boolean/T/F\r\n" +
                "data,col/1/type,\"currency/pre/$/float/,/.\"\r\n" +
                "data,col/2/type,date/dd\\/MM\\/yyyy\r\n" +
                "data,col/3/type,datetime/yyyy-MM-dd HH:mm:ss\r\n" +
                "data,col/4/type,\"float/,/.\"\r\n" +
                "data,col/5/type,\"integer/ \"\r\n" +
                "data,col/6/type,\"percentage/post/%/float/,/.\"\r\n")
                .getBytes(Util.UTF_8_CHARSET));
        MetaCSVReader reader = MetaCSVReader.create(is, metaIs);
        System.out.println(reader.getTypes());
        for (MetaCSVRecord record : reader) {
            System.out.println(toList(record));
        }
    }
}