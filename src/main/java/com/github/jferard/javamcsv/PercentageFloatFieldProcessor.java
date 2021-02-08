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

package com.github.jferard.javamcsv;

public class PercentageFloatFieldProcessor implements FieldProcessor<Double> {
    private final boolean pre;
    private final String symbol;
    private final FieldProcessor<Double> numberProcessor;
    private final String nullValue;

    public PercentageFloatFieldProcessor(boolean pre, String symbol,
                                         FieldProcessor<Double> numberProcessor, String nullValue) {
        this.pre = pre;
        this.symbol = symbol;
        this.numberProcessor = numberProcessor;
        this.nullValue = nullValue;
    }

    @Override
    public Double toObject(String text) throws MetaCSVReadException {
        if (text == null || text.equals(this.nullValue)) {
            return null;
        }
        text = text.trim();
        if (this.pre) {
            if (text.startsWith(this.symbol)) {
                text = text.substring(this.symbol.length()).trim();
            } else {
                throw new MetaCSVReadException("Value "+text+" should start with "+symbol);
            }
        } else {
            if (text.endsWith(this.symbol)) {
                text = text.substring(0, text.length() - this.symbol.length()).trim();
            } else {
                throw new MetaCSVReadException("Value "+text+" should end with "+symbol);
            }
        }
        return this.numberProcessor.toObject(text) / 100.0;
    }

    @Override
    public String toString(Double value) {
        if (value == null) {
            return this.nullValue;
        }
        String valueAsString =
                this.numberProcessor.toString(value * 100.0);
        if (this.pre) {
            return this.symbol + valueAsString;
        } else {
            return valueAsString + this.symbol;
        }
    }
}
