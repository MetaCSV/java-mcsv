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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatetimeFieldDescription implements FieldDescription<Date> {
    public static FieldDescription<Date> create(String dateFormat) {
        return new DatetimeFieldDescription(new SimpleDateFormat(dateFormat), null);
    }

    public static FieldDescription<Date> create(String dateFormat, String locale) {
        return new DatetimeFieldDescription(new SimpleDateFormat(dateFormat, Locale.getDefault()), locale);
    }

    private SimpleDateFormat simpleDateFormat;
    private String locale;
    private String nullValue;

    public DatetimeFieldDescription(SimpleDateFormat simpleDateFormat, String locale) {
        this.simpleDateFormat = simpleDateFormat;
        this.locale = locale;
        this.nullValue = "";
    }


    @Override
    public void render(Appendable out) throws IOException {
        out.append("datetime/");
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
    public Class<Date> getType() {
        return Date.class;
    }

    @Override
    public String getTypeName() {
        return "datetime";
    }

    @Override
    public String toString() {
        return String.format("DatetimeDescription(%s, %s)",
                this.simpleDateFormat.toPattern(), this.locale);
    }
}
