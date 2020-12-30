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

import java.io.IOException;

public class CurrencyIntegerFieldDescription implements FieldDescription<Long> {
    private final boolean pre;
    private final String symbol;
    private final FieldDescription<Long> numberDescription;
    private final String nullValue;

    public CurrencyIntegerFieldDescription(boolean pre, String symbol,
                                           FieldDescription<Long> numberDescription) {
        this.pre = pre;
        this.symbol = symbol;
        this.numberDescription = numberDescription;
        this.nullValue = "";
    }

    @Override
    public void render(Appendable out) throws IOException {
        Util.render(out, "currency", this.pre ? "pre" : "post", symbol);
        out.append('/');
        this.numberDescription.render(out);
    }

    @Override
    public FieldProcessor<Long> toFieldProcessor(String nullValue) {
        return new CurrencyIntegerFieldProcessor(this.pre, this.symbol,
                this.numberDescription.toFieldProcessor(nullValue), nullValue);
    }

    @Override
    public Class<Long> getJavaType() {
        return Long.class;
    }

    @Override
    public DataType getDataType() {
        return DataType.CURRENCY_INTEGER;
    }

    @Override
    public String toString() {
        return String.format("CurrencyFieldDescription(%b, %s, %s)",
                this.pre, this.symbol, this.numberDescription.toString());
    }
}
