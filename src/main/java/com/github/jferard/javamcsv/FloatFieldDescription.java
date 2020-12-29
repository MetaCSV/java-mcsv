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
import java.math.BigDecimal;

public class FloatFieldDescription implements FieldDescription<Double> {
    public static final FieldDescription<Double> INSTANCE = new FloatFieldDescription("", ".");

    private String thousandsSeparator;
    private String decimalSeparator;
    private String nullValue;

    public FloatFieldDescription(String thousandsSeparator, String decimalSeparator) {
        this.thousandsSeparator = thousandsSeparator;
        this.decimalSeparator = decimalSeparator;
        this.nullValue = "";
    }

    @Override
    public void render(Appendable out) throws IOException {
        if (this.thousandsSeparator.isEmpty()) {
            out.append("float//");
        } else {
            out.append("float/").append(this.thousandsSeparator).append('/');
        }
        out.append(this.decimalSeparator);
    }

    @Override
    public FieldProcessor<Double> toFieldProcessor(String nullValue) {
        return new FloatFieldProcessor(this.thousandsSeparator, this.decimalSeparator, nullValue);
    }

    @Override
    public Class<Double> getType() {
        return Double.class;
    }

    @Override
    public String getTypeName() {
        return "float";
    }

    @Override
    public String toString() {
        return String.format("FloatFieldDescription(%s, %s)",
                this.thousandsSeparator, this.decimalSeparator);
    }
}
