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

import java.math.BigDecimal;

public class DecimalFieldProcessor implements FieldProcessor<BigDecimal> {
    private final String thousandsSeparator;
    private final String decimalSeparator;
    private final String nullValue;

    public DecimalFieldProcessor(String thousandsSeparator, String decimalSeparator,
                                 String nullValue) {
        this.thousandsSeparator = thousandsSeparator;
        this.decimalSeparator = decimalSeparator;
        this.nullValue = nullValue;
    }

    @Override
    public BigDecimal toObject(String text) throws MetaCSVReadException {
        if (text == null || text.equals(this.nullValue)) {
            return null;
        }
        try {
            return Util.parseBigDecimal(text, thousandsSeparator, decimalSeparator);
        } catch (NumberFormatException e) {
            throw new MetaCSVReadException(e);
        }
    }

    @Override
    public String toString(BigDecimal bd) {
        if (bd == null) {
            return this.nullValue;
        }
        return Util.formatBigDecimal(bd, this.thousandsSeparator, decimalSeparator);
    }

    @Override
    public BigDecimal cast(Object o) {
        if (o == null || o instanceof BigDecimal) {
            return (BigDecimal) o;
        }
        Number n = (Number) o;
        return new BigDecimal(n.toString());
    }
}
