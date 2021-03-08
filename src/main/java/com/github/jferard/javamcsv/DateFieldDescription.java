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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFieldDescription implements FieldDescription<Date> {
    public static final FieldDescription<?> INSTANCE = DateFieldDescription.create("yyyy-MM-dd");

    public static FieldDescription<Date> create(String dateFormat) {
        return new DateFieldDescription(new SimpleDateFormat(dateFormat), null);
    }

    public static FieldDescription<Date> create(String dateFormat, String locale) {
        Locale instance = Util.getLocale(locale);
        return new DateFieldDescription(new SimpleDateFormat(dateFormat, instance), locale);
    }

    private SimpleDateFormat simpleDateFormat;
    private String locale;
    private String nullValue;

    public DateFieldDescription(SimpleDateFormat simpleDateFormat, String locale) {
        this.simpleDateFormat = simpleDateFormat;
        this.locale = locale;
        this.nullValue = "";
    }


    @Override
    public void render(Appendable out) throws IOException {
        out.append("date/");
        Util.render(out, this.simpleDateFormat.toPattern());
        if (this.locale != null) {
            out.append('/').append(locale);
        }
    }

    @Override
    public FieldProcessor<Date> toFieldProcessor(String nullValue) {
        return new DateFieldProcessor(this.simpleDateFormat, this.locale, nullValue);
    }

    @Override
    public Class<Date> getJavaType() {
        return Date.class;
    }

    @Override
    public DataType getDataType() {
        return DataType.DATE;
    }

    @Override
    public String toString() {
        return String.format("DateFieldDescription(%s, %s)",
                this.simpleDateFormat.toPattern(), this.locale);
    }
}
