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

import com.github.jferard.javamcsv.MetaCSVDataException;
import com.github.jferard.javamcsv.MetaCSVParseException;
import com.github.jferard.javamcsv.MetaCSVReadException;
import com.github.jferard.javamcsv.MetaCSVReader;
import com.github.jferard.javamcsv.TestHelper;
import com.github.jferard.javamcsv.tool.Tool;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class MetaCSVReaderResultSetIT {
    @Test
    public void testLongFile()
            throws IOException, MetaCSVParseException, URISyntaxException, MetaCSVReadException,
            MetaCSVDataException, SQLException {
        File f = TestHelper.getResourceAsFile("20201001-bal-216402149.csv");
        MetaCSVReader reader;
        reader = MetaCSVReader.create(f);
        ResultSet rs = Tool.readerToResultSet(reader);
        ResultSetMetaData metaData = rs.getMetaData();
        Assert.assertEquals("TEXT", metaData.getColumnTypeName(1));
        Assert.assertEquals("INTEGER", metaData.getColumnTypeName(4));
        Assert.assertEquals("FLOAT", metaData.getColumnTypeName(8));
        Assert.assertEquals("FLOAT", metaData.getColumnTypeName(9));
        Assert.assertEquals("FLOAT", metaData.getColumnTypeName(10));
        Assert.assertEquals("FLOAT", metaData.getColumnTypeName(11));
        Assert.assertEquals("DATE", metaData.getColumnTypeName(13));

        Assert.assertEquals(Types.VARCHAR, metaData.getColumnType(1));
        Assert.assertEquals(Types.INTEGER, metaData.getColumnType(4));
        Assert.assertEquals(Types.DOUBLE, metaData.getColumnType(8));
        Assert.assertEquals(Types.DOUBLE, metaData.getColumnType(9));
        Assert.assertEquals(Types.DOUBLE, metaData.getColumnType(10));
        Assert.assertEquals(Types.DOUBLE, metaData.getColumnType(11));
        Assert.assertEquals(Types.DATE, metaData.getColumnType(13));

        List<String> header =
                Arrays.asList("cle_interop", "uid_adresse", "voie_nom", "numero", "suffixe",
                        "commune_nom",
                        "position", "x", "y", "long", "lat", "source", "date_der_maj", "refparc",
                        "voie_nom_eu", "complement");
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            Assert.assertEquals(header.get(i-1), metaData.getColumnName(i));
        }
        Assert.assertTrue(rs.next());
        Calendar c = GregorianCalendar.getInstance(Locale.US);
        c.setTimeInMillis(0);
        c.set(2020, Calendar.JUNE, 11, 0, 0, 0);
        Assert.assertEquals("64214_0010_00700", rs.getString(1));
        Assert.assertEquals("", rs.getString(2));
        Assert.assertEquals("Route du Pays de Soule", rs.getString(3));
        Assert.assertEquals(700, rs.getInt(4));
        Assert.assertEquals("", rs.getString(5));
        Assert.assertEquals("Espès-undurein", rs.getString(6));
        Assert.assertEquals("entrée", rs.getString(7));
        Assert.assertEquals(385432.96, rs.getDouble(8), 0.001);
        Assert.assertEquals(6250383.75, rs.getDouble(9), 0.001);
        Assert.assertEquals(-0.8748110149745267, rs.getDouble(10), 0.001);
        Assert.assertEquals(43.28315047649357, rs.getDouble(11), 0.001);
        Assert.assertEquals("Commune de Espès-undurein", rs.getString(12));
        Assert.assertEquals(c.getTime(), rs.getDate(13));
        Assert.assertEquals("ZB0188", rs.getString(14));
        Assert.assertEquals("Xiberoko errepidea", rs.getString(15));
        Assert.assertEquals("", rs.getString(16));
    }
}
