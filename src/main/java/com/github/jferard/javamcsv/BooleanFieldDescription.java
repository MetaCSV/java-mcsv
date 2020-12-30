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

public class BooleanFieldDescription implements FieldDescription<Boolean> {
    private final String trueWord;
    private final String falseWord;

    public BooleanFieldDescription(String trueWord, String falseWord) {
        this.trueWord = trueWord;
        this.falseWord = falseWord;
    }

    @Override
    public void render(Appendable out) throws IOException {
        out.append("boolean/").append(this.trueWord);
        if (this.falseWord != null) {
            out.append('/').append(this.falseWord);
        }
    }

    @Override
    public FieldProcessor<Boolean> toFieldProcessor(String nullValue) {
        return new BooleanFieldProcessor(this.trueWord, this.falseWord, nullValue);
    }

    @Override
    public Class<Boolean> getJavaType() {
        return Boolean.class;
    }

    @Override
    public DataType getDataType() {
        return DataType.BOOLEAN;
    }

    @Override
    public String toString() {
        return String.format("BooleanFieldDescription(%s, %s)", this.trueWord, this.falseWord);
    }
}
