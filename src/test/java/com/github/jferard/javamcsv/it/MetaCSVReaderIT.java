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

import com.github.jferard.javamcsv.MetaCSVDataException;
import com.github.jferard.javamcsv.MetaCSVMetaData;
import com.github.jferard.javamcsv.MetaCSVParseException;
import com.github.jferard.javamcsv.MetaCSVReadException;
import com.github.jferard.javamcsv.MetaCSVReader;
import com.github.jferard.javamcsv.MetaCSVRecord;
import com.github.jferard.javamcsv.TestHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class MetaCSVReaderIT {
    public static <T> List<String> toRep(Iterable<T> iterable) {
        List<String> list = new ArrayList<String>();
        for (T e : iterable) {
            list.add(e.toString() + " (" + e.getClass() + ")");
        }
        return list;
    }

    @Test
    public void testMeta()
            throws IOException, MetaCSVParseException, MetaCSVReadException, MetaCSVDataException {
        InputStream is =
                TestHelper.getResourceAsStream("meta_csv.mcsv");
        InputStream metaIs =
                TestHelper.getResourceAsStream("meta_csv.mcsv");
        MetaCSVReader reader = MetaCSVReader.create(is, metaIs);
        MetaCSVMetaData metaData = reader.getMetaData();
        Assert.assertEquals(TestHelper.stringDescription(metaData.getDescription(0)), "text");
        Assert.assertEquals(TestHelper.stringDescription(metaData.getDescription(1)), "text");
        Assert.assertEquals(TestHelper.stringDescription(metaData.getDescription(2)), "object");
        Iterator<MetaCSVRecord> iterator = reader.iterator();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(
                Arrays.asList("domain", "key", "value"), TestHelper.toList(iterator.next()));
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(
                Arrays.asList("file", "encoding", "utf-8"), TestHelper.toList(iterator.next()));
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(
                Arrays.asList("file", "line_terminator", "\\r\\n"),
                TestHelper.toList(iterator.next()));
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(
                Arrays.asList("csv", "delimiter", ","), TestHelper.toList(iterator.next()));
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(
                Arrays.asList("csv", "double_quote", "true"), TestHelper.toList(iterator.next()));
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(
                Arrays.asList("csv", "quote_char", "\""), TestHelper.toList(iterator.next()));
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(
                Arrays.asList("data", "col/0/type", "text"), TestHelper.toList(iterator.next()));
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(
                Arrays.asList("data", "col/1/type", "text"), TestHelper.toList(iterator.next()));
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(
                Arrays.asList("data", "col/2/type", "object"), TestHelper.toList(iterator.next()));
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testLongFile()
            throws IOException, MetaCSVParseException, URISyntaxException, MetaCSVReadException,
            MetaCSVDataException {
        File f = TestHelper.getResourceAsFile("20201001-bal-216402149.csv");
        MetaCSVReader reader;
        reader = MetaCSVReader.create(f);
        MetaCSVMetaData metaData = reader.getMetaData();
        Assert.assertEquals(TestHelper.stringDescription(metaData.getDescription(3)), "integer");
        Assert.assertEquals(TestHelper.stringDescription(metaData.getDescription(7)), "float//.");
        Assert.assertEquals(TestHelper.stringDescription(metaData.getDescription(8)), "float//.");
        Assert.assertEquals(TestHelper.stringDescription(metaData.getDescription(9)), "float//.");
        Assert.assertEquals(TestHelper.stringDescription(metaData.getDescription(10)), "float//.");
        Assert.assertEquals(TestHelper.stringDescription(metaData.getDescription(12))   , "date/yyyy-MM-dd");
        Iterator<MetaCSVRecord> iterator = reader.iterator();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(
                Arrays.asList("cle_interop", "uid_adresse", "voie_nom", "numero", "suffixe",
                        "commune_nom", "position", "x", "y", "long", "lat", "source",
                        "date_der_maj", "refparc", "voie_nom_eu", "complement"),
                TestHelper.toList(iterator.next()));
        Assert.assertTrue(iterator.hasNext());
        Calendar c = GregorianCalendar.getInstance(Locale.US);
        c.setTimeInMillis(0);
        c.set(2020, Calendar.JUNE, 11, 0, 0, 0);
        Assert.assertEquals(
                Arrays.asList("64214_0010_00700", null, "Route du Pays de Soule", 700L, null,
                        "Espès-undurein", "entrée", 385432.96, 6250383.75,
                        -0.8748110149745267, 43.28315047649357, "Commune de Espès-undurein",
                        c.getTime(), "ZB0188", "Xiberoko errepidea", null),
                TestHelper.toList(iterator.next()));
        while (iterator.hasNext()) {
            iterator.next();
        }
    }

    @Test
    public void testExample()
            throws IOException, MetaCSVParseException, URISyntaxException, MetaCSVReadException,
            MetaCSVDataException {
        File f = TestHelper.getResourceAsFile("example.csv");
        MetaCSVReader reader;
        reader = MetaCSVReader.create(f);
        MetaCSVMetaData metaData = reader.getMetaData();
        Assert.assertEquals(TestHelper.stringDescription(metaData.getDescription(1)), "date/yyyy-MM-dd");
        Assert.assertEquals(TestHelper.stringDescription(metaData.getDescription(2)), "integer");
        Iterator<MetaCSVRecord> iterator = reader.iterator();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(
                Arrays.asList("name", "date", "count"), TestHelper.toList(iterator.next()));
        Assert.assertTrue(iterator.hasNext());
        Calendar c = GregorianCalendar.getInstance(Locale.US);
        c.setTimeInMillis(0);
        c.set(2020, Calendar.NOVEMBER, 21, 0, 0, 0);
        Assert.assertEquals(Arrays.asList("foo", c.getTime(), 15L),
                TestHelper.toList(iterator.next()));
        Assert.assertTrue(iterator.hasNext());
        c.set(2020, Calendar.NOVEMBER, 22, 0, 0, 0);
        Assert.assertEquals(Arrays.asList("bar", c.getTime(), -8L),
                TestHelper.toList(iterator.next()));
        Assert.assertFalse(iterator.hasNext());
    }

}