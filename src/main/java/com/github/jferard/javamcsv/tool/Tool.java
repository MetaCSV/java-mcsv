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

package com.github.jferard.javamcsv.tool;

import com.github.jferard.javamcsv.IntegerFieldDescription;
import com.github.jferard.javamcsv.MetaCSVData;
import com.github.jferard.javamcsv.MetaCSVDataBuilder;
import com.github.jferard.javamcsv.MetaCSVDataException;
import com.github.jferard.javamcsv.MetaCSVReader;
import com.github.jferard.javamcsv.MetaCSVRenderer;
import com.github.jferard.javamcsv.MetaCSVWriter;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Tool {
    public static ResultSet readerToResultSet(MetaCSVReader reader) {
        return new MetaCSVReaderResultSet(reader);
    }

    public static void writeResultSetMetaData(ResultSetMetaData resultSetMetaData, MetaCSVDataBuilder dataBuilder,
                                              MetaCSVRenderer renderer)
            throws SQLException, MetaCSVDataException, IOException {
        MetaCSVData data0 = dataBuilder.build();
        int count = resultSetMetaData.getColumnCount();
        for (int c=0; c<count; c++) {
            if (data0.getDescription(c) != null) {
                continue;
            }
            int columnType = resultSetMetaData.getColumnType(c + 1);
            switch (columnType) {
                default: dataBuilder.colType(c, IntegerFieldDescription.INSTANCE);
            }
        }
        renderer.render(dataBuilder.build());
    }

    public static void writeResultSet(ResultSet resultSet, MetaCSVWriter writer)
            throws SQLException, IOException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int count = metaData.getColumnCount();
        List<String> header = new ArrayList<String>(count);
        for (int i =0; i<count; i++) {
            header.add(metaData.getColumnName(i));
        }
        writer.writeHeader(header);
        writer.writeRow(null);
    }
}
