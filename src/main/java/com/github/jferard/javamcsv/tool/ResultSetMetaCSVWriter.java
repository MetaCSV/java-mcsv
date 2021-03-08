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

package com.github.jferard.javamcsv.tool;

import com.github.jferard.javamcsv.DataType;
import com.github.jferard.javamcsv.FieldDescription;
import com.github.jferard.javamcsv.FieldProcessor;
import com.github.jferard.javamcsv.MetaCSVData;
import com.github.jferard.javamcsv.MetaCSVDataBuilder;
import com.github.jferard.javamcsv.MetaCSVDataException;
import com.github.jferard.javamcsv.MetaCSVRenderer;
import com.github.jferard.javamcsv.MetaCSVWriter;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResultSetMetaCSVWriter {
    private ResultSet resultSet;

    public ResultSetMetaCSVWriter(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    public void write(MetaCSVWriter writer, MetaCSVRenderer renderer)
            throws SQLException, IOException, MetaCSVDataException {
        write(writer, renderer, new MetaCSVDataBuilder());
    }

    private void write(MetaCSVWriter writer, MetaCSVRenderer renderer,
                       MetaCSVDataBuilder dataBuilder)
            throws SQLException, MetaCSVDataException, IOException {
        MetaCSVData metaCSVData = getMetaCSVData(dataBuilder);
        renderer.render(metaCSVData);
        writeCSV(writer, metaCSVData);
    }

    public void writeMetaCSV(MetaCSVRenderer renderer)
            throws SQLException, MetaCSVDataException, IOException {
        this.writeMetaCSV(new MetaCSVDataBuilder(), renderer);
    }

    public void writeMetaCSV(MetaCSVDataBuilder dataBuilder,
                                    MetaCSVRenderer renderer)
            throws SQLException, MetaCSVDataException, IOException {
        MetaCSVData metaCSVData = getMetaCSVData(dataBuilder);
        renderer.render(metaCSVData);
    }

    public MetaCSVData getMetaCSVData(MetaCSVDataBuilder dataBuilder)
            throws SQLException, MetaCSVDataException {
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        MetaCSVData data0 = dataBuilder.build();
        int count = resultSetMetaData.getColumnCount();
        for (int c = 0; c < count; c++) {
            if (data0.getDescription(c) != null) {
                continue;
            }
            int columnType = resultSetMetaData.getColumnType(c + 1);
            DataType dataType = SQLUtil.sqlTypeToDataType(columnType);
            FieldDescription<?> description = dataType.getDefaultDescription();
            dataBuilder.colType(c, description);
        }
        MetaCSVData metaCSVData = dataBuilder.build();
        return metaCSVData;
    }

    public void writeCSV(MetaCSVWriter writer)
            throws SQLException, IOException, MetaCSVDataException {
        writeCSV(writer, new MetaCSVDataBuilder());
    }

    private void writeCSV(MetaCSVWriter writer, MetaCSVDataBuilder dataBuilder)
            throws SQLException, MetaCSVDataException, IOException {
        MetaCSVData metaCSVData = getMetaCSVData(dataBuilder);
        writeCSV(writer, metaCSVData);
    }

    private void writeCSV(MetaCSVWriter writer, MetaCSVData metaCSVData)
            throws SQLException, IOException {
        List<String> header = getHeader();
        int count = header.size();
        writer.writeHeader(header);
        while (resultSet.next()) {
            List<Object> values = this.getRow(metaCSVData, count);
            writer.writeRow(values);
        }
        writer.close();
    }

    public List<String> getHeader() throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int count = metaData.getColumnCount();
        List<String> header = new ArrayList<String>(count);
        for (int i = 0; i < count; i++) {
            header.add(metaData.getColumnName(i + 1));
        }
        return header;
    }

    private List<Object> getRow(MetaCSVData metaCSVData, int count) throws SQLException {
        List<Object> values = new ArrayList<Object>(count);
        for (int i = 0; i < count; i++) {
            DataType dataType = metaCSVData.getDescription(i).getDataType();
            Object value;
            switch (dataType) {
                case BOOLEAN:
                    value = resultSet.getBoolean(i + 1);
                    break;
                case CURRENCY_DECIMAL: case DECIMAL: case PERCENTAGE_DECIMAL:
                    value = resultSet.getBigDecimal(i + 1);
                    break;
                case CURRENCY_INTEGER: case INTEGER:
                    value = resultSet.getLong(i + 1);
                    break;
                case DATE:
                    value = resultSet.getDate(i + 1);
                    break;
                case DATETIME:
                    value = resultSet.getTime(i + 1);
                    break;
                case FLOAT: case PERCENTAGE_FLOAT:
                    value = resultSet.getDouble(i + 1);
                    break;
                case TEXT:
                    value = resultSet.getString(i + 1);
                    break;
                default:
                    value = resultSet.getObject(i + 1);
                    break;
            }
            values.add(value);
        }
        return values;
    }
}
