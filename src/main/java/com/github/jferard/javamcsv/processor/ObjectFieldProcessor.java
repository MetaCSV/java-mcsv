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

package com.github.jferard.javamcsv.processor;

import com.github.jferard.javamcsv.MetaCSVReadException;
import com.github.jferard.javamcsv.processor.FieldProcessor;

public class ObjectFieldProcessor implements FieldProcessor<Object> {
    private final String nullValue;

    public ObjectFieldProcessor(String nullValue) {
        this.nullValue = nullValue;
    }

    @Override
    public Object toObject(String text) {
        if (text == null || text.equals(this.nullValue)) {
            return null;
        } else {
            return text;
        }
    }

    @Override
    public String toString(Object value) {
        if (value == null) {
            return this.nullValue;
        } else {
            return value.toString();
        }
    }

    @Override
    public String toCanonicalString(String text) throws MetaCSVReadException {
        if (text == null || text.equals(this.nullValue)) {
            return "";
        } else {
            return text;
        }
    }

    @Override
    public Object cast(Object o) {
        return o;
    }
}
