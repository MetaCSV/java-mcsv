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

package com.github.jferard.javamcsv;import java.io.IOException;

public class PercentageFieldDescription implements FieldDescription<Double> {
    private final boolean pre;
    private final String symbol;
    private final FieldDescription<Double> floatDescription;

    public PercentageFieldDescription(boolean pre, String symbol,
                                      FieldDescription<Double> floatDescription) {
        this.pre = pre;
        this.symbol = symbol;
        this.floatDescription = floatDescription;
    }

    @Override
    public void render(Appendable out) throws IOException {
        Util.render(out, "percentage", this.pre ? "pre" : "post", symbol);
        out.append('/');
        this.floatDescription.render(out);
    }

    @Override
    public FieldProcessor<Double> toFieldProcessor(String nullValue) {
        return new PercentageFieldProcessor(this.pre, this.symbol,
                this.floatDescription.toFieldProcessor(nullValue), nullValue);
    }
}
