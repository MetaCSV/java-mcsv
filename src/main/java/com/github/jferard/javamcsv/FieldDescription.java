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

package com.github.jferard.javamcsv;import java.io.IOException;

abstract public class FieldDescription<T> {
    public abstract void render(Appendable out) throws IOException;

    public FieldProcessor<?> toFieldProcessor(String nullValue, OnError onError) {
        final FieldProcessor<T> rawProcessor = this.toFieldProcessor(nullValue);
        switch (onError) {
            case WRAP:
                final String description = getDescription();
                return new FieldProcessor<Object>() {
                    @Override
                    public Object toObject(String text) {
                        try {
                            return rawProcessor.toObject(text);
                        } catch (MetaCSVReadException e) {
                            return new ReadError(text, description);
                        }
                    }

                    @Override
                    public String toString(Object value) {
                        return rawProcessor.toString((T) value);
                    }
                };
            case NULL:
                return new FieldProcessor<T>() {
                    @Override
                    public T toObject(String text) {
                        try {
                            return rawProcessor.toObject(text);
                        } catch (MetaCSVReadException e) {
                            return null;
                        }
                    }

                    @Override
                    public String toString(T value) {
                        return rawProcessor.toString(value);
                    }
                };
            case TEXT:
                return new FieldProcessor<Object>() {
                    @Override
                    public Object toObject(String text) {
                        try {
                            return rawProcessor.toObject(text);
                        } catch (MetaCSVReadException e) {
                            return text;
                        }
                    }

                    @Override
                    public String toString(Object value) {
                        return rawProcessor.toString((T) value);
                    }
                };
            default:
                return rawProcessor;
        }
    }

    private String getDescription() {
        try {
            StringBuilder sb = new StringBuilder();
            this.render(sb);
            return sb.toString();
        } catch (IOException e) {
            // should not happen
            return "???";
        }
    }

    protected abstract FieldProcessor<T> toFieldProcessor(String nullValue);

    public abstract Class<T> getJavaType();

    public abstract DataType getDataType();
}
