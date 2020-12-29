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

package com.github.jferard.javamcsv.it;

import com.github.jferard.javamcsv.DecimalFieldDescription;
import com.github.jferard.javamcsv.FieldDescription;
import com.github.jferard.javamcsv.MetaCSVData;
import com.github.jferard.javamcsv.MetaCSVDataBuilder;
import com.github.jferard.javamcsv.MetaCSVDataException;
import com.github.jferard.javamcsv.MetaCSVParseException;
import com.github.jferard.javamcsv.MetaCSVReadException;
import com.github.jferard.javamcsv.MetaCSVReader;
import com.github.jferard.javamcsv.MetaCSVRecord;
import com.github.jferard.javamcsv.MetaCSVWriter;
import com.github.jferard.javamcsv.TestHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WriteThenReadIT {
    @Test
    public void test()
            throws IOException, MetaCSVDataException, MetaCSVParseException, MetaCSVReadException {
        File csv = File.createTempFile("metacsv-test", ".csv");
        csv.deleteOnExit();
        File meta = File.createTempFile("metacsv-test", ".mcsv");
        meta.deleteOnExit();
        MetaCSVData data =
                new MetaCSVDataBuilder().colType(1, DecimalFieldDescription.INSTANCE).build();
        MetaCSVWriter writer = MetaCSVWriter.create(csv, meta, data);
        writer.writeHeader(Arrays.asList("foo", "bar"));
        writer.writeRow(Arrays.<Object>asList("baz", BigDecimal.valueOf(Math.PI)));
        writer.close();

        Assert.assertEquals("foo,bar\r\n" +
                "baz,3.141592653589793\r\n", TestHelper.toString(csv));
        Assert.assertEquals("domain,key,value\r\n" +
                "data,col/1/type,decimal//.\r\n", TestHelper.toString(meta));

        MetaCSVReader reader = MetaCSVReader.create(csv, meta);
        FieldDescription<BigDecimal> instance = DecimalFieldDescription.INSTANCE;
        Appendable sb = new StringBuilder();
        instance.render(sb);
        Assert.assertEquals(sb.toString(), reader.getMetaData().getDescription(1));
        Iterator<MetaCSVRecord> it = reader.iterator();
        Assert.assertTrue(it.hasNext());
        TestHelper.assertMetaEquals(TestHelper.createMetaRecord("foo", "bar"), it.next());
        Assert.assertTrue(it.hasNext());
        TestHelper.assertMetaEquals(
                TestHelper.createMetaRecord("baz", new BigDecimal("3.141592653589793")),
                it.next());
        Assert.assertFalse(it.hasNext());

    }

}
