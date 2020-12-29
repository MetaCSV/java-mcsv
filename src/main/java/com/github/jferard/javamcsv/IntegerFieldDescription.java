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

public class IntegerFieldDescription implements FieldDescription<Long> {
    public static IntegerFieldDescription INSTANCE = new IntegerFieldDescription(null);

    private final String thousandsSeparator;

    public IntegerFieldDescription(String thousandsSeparator) {
        this.thousandsSeparator = thousandsSeparator;
    }

    @Override
    public void render(Appendable out) throws IOException {
        if (this.thousandsSeparator == null || this.thousandsSeparator.isEmpty()) {
            out.append("integer");
        } else {
            out.append("integer/").append(this.thousandsSeparator);
        }
    }

    @Override
    public FieldProcessor<Long> toFieldProcessor(String nullValue) {
        return new IntegerFieldProcessor(this.thousandsSeparator, nullValue);
    }

    @Override
    public Class<Long> getType() {
        return Long.class;
    }

    @Override
    public String getTypeName() {
        return "integer";
    }

    @Override
    public String toString() {
        return String.format("IntegerFieldDescription(%s)",
                this.thousandsSeparator);
    }
}
