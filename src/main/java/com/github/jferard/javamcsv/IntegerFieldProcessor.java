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

package com.github.jferard.javamcsv;import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class IntegerFieldProcessor implements FieldProcessor<Integer> {
    private final String thousandsSeparator;
    private final String nullValue;

    public IntegerFieldProcessor(String thousandsSeparator, String nullValue) {
        this.thousandsSeparator = thousandsSeparator;
        this.nullValue = nullValue;
    }

    @Override
    public Integer toObject(String text) throws MetaCSVReadException {
        if (text == null || text.equals(this.nullValue)) {
            return null;
        }
        try {
            return Integer.parseInt(text.replace(thousandsSeparator, ""));
        } catch (NumberFormatException e) {
            throw new MetaCSVReadException(e);
        }
    }

    @Override
    public String toString(Integer value) throws MetaCSVWriteException {
        if (value == null) {
            return this.nullValue;
        }
        DecimalFormat formatter = new DecimalFormat();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        if (this.thousandsSeparator == null || this.thousandsSeparator.isEmpty()) {
            formatter.setDecimalSeparatorAlwaysShown(false);
        } else {
            symbols.setGroupingSeparator(this.thousandsSeparator.charAt(0));
        }
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(value.intValue());
    }
}
