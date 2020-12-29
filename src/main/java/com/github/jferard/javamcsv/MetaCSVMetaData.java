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
import java.util.HashMap;
import java.util.Map;

public class MetaCSVMetaData {
    public static MetaCSVMetaData create(Map<Integer, FieldDescription<?>> descriptionByColIndex)
            throws IOException {
        Map<Integer, Class<?>> types = new HashMap<Integer, Class<?>>();
        Map<Integer, String> typeNames = new HashMap<Integer, String>();
        Map<Integer, String> descriptions = new HashMap<Integer, String>();
        for (Map.Entry<Integer, FieldDescription<?>> entry : descriptionByColIndex
                .entrySet()) {
            Integer i = entry.getKey();
            FieldDescription<?> description = entry.getValue();
            types.put(i, description.getType());
            typeNames.put(i, description.getTypeName());
            StringBuilder sb = new StringBuilder();
            description.render(sb);
            descriptions.put(i, sb.toString());
        }
        return new MetaCSVMetaData(descriptions, types, typeNames);
    }

    private final Map<Integer, String> descriptions;
    private final Map<Integer, Class<?>> types;
    private final Map<Integer, String> typeNames;

    public MetaCSVMetaData(Map<Integer, String> descriptions, Map<Integer, Class<?>> types, Map<Integer, String> typeNames) {
        this.descriptions = descriptions;
        this.types = types;
        this.typeNames = typeNames;
    }

    public String getDescription(int c) {
        String ret = this.descriptions.get(c);
        if (ret == null) {
            return "text";
        }
        return ret;
    }

    public Class<?> getType(int c) {
        Class<?> ret = this.types.get(c);
        if (ret == null) {
            return String.class;
        }
        return ret;
    }

    public String getTypeName(int c) {
        String ret = this.typeNames.get(c);
        if (ret == null) {
            return "text";
        }
        return ret;
    }
}
