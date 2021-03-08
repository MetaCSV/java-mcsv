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

package com.github.jferard.javamcsv;

import java.util.Iterator;

public class MetaCSVRow {
    public static MetaCSVRow fromIterable(Iterable<String> iterable) throws MetaCSVParseException {
        Iterator<String> it = iterable.iterator();
        if (!it.hasNext()) {
            throw new MetaCSVParseException("Bad header: empty");
        }
        String domain = it.next();
        if (!it.hasNext()) {
            throw new MetaCSVParseException(String.format("Bad header: one element (%s)", domain));
        }
        String key = it.next();
        if (!it.hasNext()) {
            throw new MetaCSVParseException(
                    String.format("Bad header: two elements (%s, %s)", domain, key));
        }
        String value = it.next();
        if (it.hasNext()) {
            throw new MetaCSVParseException(
                    String.format("Bad header: more elements: %s, %s, %s, %s, ...", domain, key,
                            value, it.next()));
        }
        return new MetaCSVRow(domain, key, value);
    }

    private final String domain;
    private final String key;
    private final String value;

    public MetaCSVRow(String domain, String key, String value) {
        this.domain = domain;
        this.key = key;
        this.value = value;
    }

    public String getDomain() {
        return domain;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
