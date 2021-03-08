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

    public static ResultSetMetaCSVWriter resultSetWriter(ResultSet resultSet) {
        return new ResultSetMetaCSVWriter(resultSet);
    }
}
