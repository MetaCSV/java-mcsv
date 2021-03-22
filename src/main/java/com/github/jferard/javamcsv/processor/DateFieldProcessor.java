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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateFieldProcessor implements FieldProcessor<Date> {
    private final SimpleDateFormat simpleDateFormat;
    private final String locale;
    private final String nullValue;

    public DateFieldProcessor(SimpleDateFormat simpleDateFormat, String locale, String nullValue) {
        this.simpleDateFormat = simpleDateFormat;
        this.locale = locale;
        this.nullValue = nullValue;
    }

    /**
     *
     * @param text the CSV value
     * @return the UTC-Date
     * @throws MetaCSVReadException
     */
    @Override
    public Date toObject(String text) throws MetaCSVReadException {
        if (text == null || text.equals(this.nullValue)) {
            return null;
        }
        try {
            return simpleDateFormat.parse(text);
        } catch (ParseException e) {
            throw new MetaCSVReadException(e);
        }
    }

    @Override
    public String toString(Date date) {
        if (date == null) {
            return this.nullValue;
        }
        return simpleDateFormat.format(date);
    }

    @Override
    public Date cast(Object o) {
        if (o == null || o instanceof Date) {
            return (Date) o;
        }
        if (o instanceof Calendar) {
            return ((Calendar) o).getTime();
        } else if (o instanceof Number) {
            return new Date(((Number) o).longValue());
        } else {
            return (Date) o;
        }
    }
}
