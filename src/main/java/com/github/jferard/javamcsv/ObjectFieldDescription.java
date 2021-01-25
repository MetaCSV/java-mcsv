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
import java.util.Collections;
import java.util.List;

public class ObjectFieldDescription implements FieldDescription<Object> {
    public static final FieldDescription<?> INSTANCE = new ObjectFieldDescription(
            Collections.<String>emptyList());
    private final String nullValue;
    private List<String> parameters;

    public ObjectFieldDescription(List<String> parameters) {
        this.parameters = parameters;
        this.nullValue = "";
    }

    public List<String> getParameters() {
        return this.parameters;
    }

    @Override
    public void render(Appendable out) throws IOException {
        out.append("object");
        if (parameters.size() > 0) {
            out.append('/');
            Util.render(out, parameters.toArray(new String[]{}));
        }
    }

    @Override
    public FieldProcessor<Object> toFieldProcessor(String nullValue) {
        return new ObjectFieldProcessor(nullValue);
    }

    @Override
    public Class<Object> getJavaType() {
        return Object.class;
    }

    @Override
    public DataType getDataType() {
        return DataType.OBJECT;
    }

    @Override
    public String toString() {
        return String.format("ObjectFieldDescription(%s)", this.parameters);
    }
}
