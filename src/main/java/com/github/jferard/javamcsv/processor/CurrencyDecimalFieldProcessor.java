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

public class CurrencyDecimalFieldProcessor implements FieldProcessor<BigDecimal> {
    private final boolean pre;
    private final String symbol;
    private final FieldProcessor<BigDecimal> numberProcessor;
    private final String nullValue;

    public CurrencyDecimalFieldProcessor(boolean pre, String symbol,
                                         FieldProcessor<BigDecimal> numberProcessor,
                                         String nullValue) {
        this.pre = pre;
        this.symbol = symbol;
        this.numberProcessor = numberProcessor;
        this.nullValue = nullValue;
    }

    @Override
    public BigDecimal toObject(String text) throws MetaCSVReadException {
        if (text == null || text.equals(this.nullValue)) {
            return null;
        }
        text = Util.cleanCurrencyText(text, this.pre, this.symbol);
        return this.numberProcessor.toObject(text);
    }

    @Override
    public String toString(BigDecimal value) {
        if (value == null) {
            return this.nullValue;
        }
        String valueAsString = this.numberProcessor.toString(value);
        if (this.pre) {
            return this.symbol + valueAsString;
        } else {
            return valueAsString + " " + this.symbol;
        }
    }

    @Override
    public String toCanonicalString(String text) throws MetaCSVReadException {
        if (text == null || text.equals(this.nullValue)) {
            return "";
        }
        text = Util.cleanCurrencyText(text, this.pre, this.symbol);
        return this.numberProcessor.toCanonicalString(text);
    }

    @Override
    public BigDecimal cast(Object o) {
        return this.numberProcessor.cast(o);
    }
}
