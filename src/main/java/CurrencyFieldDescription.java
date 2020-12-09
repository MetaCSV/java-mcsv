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

import java.io.IOException;

public class CurrencyFieldDescription implements FieldDescription<Number> {
    private final boolean pre;
    private final String symbol;
    private final FieldDescription<? extends Number> numberDescription;
    private final String nullValue;

    public CurrencyFieldDescription(boolean pre, String symbol,
                                    FieldDescription<? extends Number> numberDescription) {
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
    public FieldProcessor<Number> toFieldProcessor(String nullValue) {
        return new CurrencyFieldProcessor(this.pre, this.symbol,
                this.numberDescription.toFieldProcessor(nullValue), nullValue);
    }
}
