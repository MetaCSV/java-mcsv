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

public interface ReadFieldProcessor<T> {
    /**
     * Prepare a value for a consumer. Does not throw any exception.
     * @param text the CSV value
     * @return the Object
     */
    Object toObject(String text);

    /**
     * Return a canonical version of the value
     * @param text the value as string
     * @return the canonical version of the value
     */
    String toCanonicalString(String text);
}
