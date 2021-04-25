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
import com.github.jferard.javamcsv.OnError;
import com.github.jferard.javamcsv.ReadError;
import com.github.jferard.javamcsv.description.FieldDescription;

import java.io.IOException;

public class FieldProcessorFactory {
    public <T> ReadFieldProcessor<T> toReadFieldProcessor(FieldDescription<T> description,
                                                          String nullValue, OnError onError) {
        final FieldProcessor<T> rawProcessor = description.toFieldProcessor(nullValue);
        switch (onError) {
            case WRAP:
                final String strDescription = getColTypeValue(description);
                return new ReadFieldProcessor<T>() {
                    @Override
                    public Object toObject(String text) {
                        try {
                            return rawProcessor.toObject(text);
                        } catch (MetaCSVReadException e) {
                            return new ReadError(text, strDescription);
                        }
                    }
                };
            case NULL:
                return new ReadFieldProcessor<T>() {
                    @Override
                    public T toObject(String text) {
                        try {
                            return rawProcessor.toObject(text);
                        } catch (MetaCSVReadException e) {
                            return null;
                        }
                    }
                };
            case TEXT:
                return new ReadFieldProcessor<T>() {
                    @Override
                    public Object toObject(String text) {
                        try {
                            return rawProcessor.toObject(text);
                        } catch (MetaCSVReadException e) {
                            return text;
                        }
                    }
                };
            default:
                return new ReadFieldProcessor<T>() {
                    @Override
                    public Object toObject(String text) {
                        try {
                            return rawProcessor.toObject(text);
                        } catch (MetaCSVReadException e) {
                            throw new RuntimeException(e);
                        }
                    }
                };
        }
    }

    <T> String getColTypeValue(FieldDescription<T> description) {
        try {
            StringBuilder sb = new StringBuilder();
            description.render(sb);
            return sb.toString();
        } catch (IOException e) {
            // should not happen
            return "???";
        }
    }

    public <T> WriteFieldProcessor toWriteFieldProcessor(FieldDescription<T> description,
                                                         final String nullValue, OnError onError,
                                                         boolean lenient) {
        final FieldProcessor<T> rawProcessor = description.toFieldProcessor(nullValue);
        if (lenient) {
            switch (onError) {
                case WRAP:
                    throw new RuntimeException("OnError.WRAP not allowed for write error.");
                case NULL:
                    return new WriteFieldProcessor() {
                        @Override
                        public String toString(Object o) {
                            try {
                                T text = rawProcessor.cast(o);
                                return rawProcessor.toString(text);
                            } catch (ClassCastException e) {
                                return nullValue;
                            }
                        }
                    };
                case TEXT:
                    return new WriteFieldProcessor() {
                        @Override
                        public String toString(Object o) {
                            try {
                                T value = rawProcessor.cast(o);
                                return rawProcessor.toString(value);
                            } catch (ClassCastException e) {
                                return o.toString();
                            }
                        }
                    };
                default:
                    return new WriteFieldProcessor() {
                        @Override
                        public String toString(Object o) {
                            T value = rawProcessor.cast(o);
                            return rawProcessor.toString(value);
                        }
                    };
            }
        } else {
            switch (onError) {
                case WRAP:
                    throw new RuntimeException("OnError.WRAP not allowed for write error.");
                case NULL:
                    return new WriteFieldProcessor() {
                        @Override
                        public String toString(Object o) {
                            try {
                                T value = (T) o;
                                return rawProcessor.toString(value);
                            } catch (ClassCastException e) {
                                return nullValue;
                            }
                        }
                    };
                case TEXT:
                    return new WriteFieldProcessor() {
                        @Override
                        public String toString(Object o) {
                            try {
                                T value = (T) o;
                                return rawProcessor.toString(value);
                            } catch (ClassCastException e) {
                                return o.toString();
                            }
                        }
                    };
                default:
                    return new WriteFieldProcessor() {
                        @Override
                        public String toString(Object o) {
                            T value = (T) o;
                            return rawProcessor.toString(value);
                        }
                    };
            }
        }
    }
}
