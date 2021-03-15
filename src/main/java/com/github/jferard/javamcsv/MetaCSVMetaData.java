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

package com.github.jferard.javamcsv;

import com.github.jferard.javamcsv.description.FieldDescription;
import com.github.jferard.javamcsv.description.TextFieldDescription;

import java.io.IOException;
import java.util.Map;

/** Utility class, similar to ResultSetMetaData */
public class MetaCSVMetaData {
    public static MetaCSVMetaData create(Map<Integer, FieldDescription<?>> descriptionByColIndex)
            throws IOException {
        return new MetaCSVMetaData(descriptionByColIndex);
    }

    private final Map<Integer, FieldDescription<?>> descriptionByColIndex;

    public MetaCSVMetaData(Map<Integer, FieldDescription<?>> descriptionByColIndex) {
        this.descriptionByColIndex = descriptionByColIndex;
    }

    public FieldDescription<?> getDescription(int c) {
        FieldDescription<?> ret = this.descriptionByColIndex.get(c);
        if (ret == null) {
            return TextFieldDescription.INSTANCE;
        }
        return ret;
    }

    public <T extends FieldDescription<?>> T getDescription(int c, Class<T> klass) {
        T ret = (T) this.descriptionByColIndex.get(c);
        if (ret == null) {
            return (T) TextFieldDescription.INSTANCE;
        }
        return ret;
    }

    public Class<?> getJavaType(int c) {
        FieldDescription<?> ret = this.descriptionByColIndex.get(c);
        if (ret == null) {
            return String.class;
        }
        return ret.getJavaType();
    }

    public DataType getDataType(int c) {
        FieldDescription<?> ret = this.descriptionByColIndex.get(c);
        if (ret == null) {
            return DataType.TEXT;
        }
        return ret.getDataType();
    }
}
