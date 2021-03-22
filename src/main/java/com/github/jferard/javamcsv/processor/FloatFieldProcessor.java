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
import com.github.jferard.javamcsv.Util;
import com.github.jferard.javamcsv.processor.FieldProcessor;

public class FloatFieldProcessor implements FieldProcessor<Double> {
    private final String thousandsSeparator;
    private final String decimalSeparator;
    private final String nullValue;

    public FloatFieldProcessor(String thousandsSeparator, String decimalSeparator,
                               String nullValue) {
        this.thousandsSeparator = thousandsSeparator;
        this.decimalSeparator = decimalSeparator;
        this.nullValue = nullValue;
    }

    @Override
    public Double toObject(String text) throws MetaCSVReadException {
        if (text == null || text.equals(this.nullValue)) {
            return null;
        }
        try {
            return Util.parseDouble(text, thousandsSeparator, decimalSeparator);
        } catch (NumberFormatException e) {
            throw new MetaCSVReadException(e);
        }
    }

    @Override
    public String toString(Double d) {
        if (d == null) {
            return this.nullValue;
        }
        return Util.formatDouble(d, this.thousandsSeparator, decimalSeparator);
    }

    @Override
    public Double cast(Object o) {
        if (o == null || o instanceof Double) {
            return (Double) o;
        }
        return ((Number) o).doubleValue();
    }
}
