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

package com.github.jferard.javamcsv;import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MetaCSVRenderer {
    public static MetaCSVRenderer create(OutputStream os) throws IOException {
        return MetaCSVRenderer.create(os, true);
    }

    public static MetaCSVRenderer create(OutputStream os, boolean minimal) throws IOException {
        CSVPrinter printer =
                new CSVPrinter(new OutputStreamWriter(os, Util.UTF_8_CHARSET), CSVFormat.DEFAULT);
        return new MetaCSVRenderer(printer, minimal);
    }

    private final CSVPrinter printer;
    private final boolean minimal;

    public MetaCSVRenderer(CSVPrinter printer, boolean minimal) {
        this.printer = printer;
        this.minimal = minimal;
    }

    public void render(MetaCSVData data) throws IOException {
        this.printer.printRecord("domain", "key", "value");
        if (minimal) {
            this.renderMinimal(data);
        } else {
            this.renderVerbose(data);
        }
        this.printer.flush();
    }

    private void renderMinimal(MetaCSVData data) throws IOException {
        Charset encoding = data.getEncoding();
        if (encoding != Util.UTF_8_CHARSET) {
            this.printer.printRecord("file", "encoding", encoding.toString());
        }
        if (data.isUtf8BOM()) {
            this.printer.printRecord("file", "bom", true);
        }
        String lineTerminator = data.getLineTerminator();
        if (!lineTerminator.equals("\r\n")) {
            this.printer.printRecord("file", "line_terminator", Util.escapeLineTerminator(lineTerminator));
        }
        String nullValue = data.getNullValue();
        if (!(nullValue == null || nullValue.isEmpty())) {
            this.printer.printRecord("data", "null_value", nullValue);
        }
        Map<Integer, FieldDescription<?>> descriptionByIndex = data.getDescriptionByIndex();
        List<Integer> indices = new ArrayList<Integer>(descriptionByIndex.keySet());
        Collections.sort(indices);
        for (int i : indices) {
            FieldDescription<?> description = descriptionByIndex.get(i);
            if (!(description instanceof TextFieldDescription)) {
                StringBuilder out = new StringBuilder();
                description.render(out);
                this.printer.printRecord("data", "col/" + i + "/type", out.toString());
            }
        }
    }

    private void renderVerbose(MetaCSVData data) throws IOException {
        this.printer.printRecord("file", "encoding", data.getEncoding().toString());
        this.printer.printRecord("file", "bom", data.isUtf8BOM());
        this.printer.printRecord("file", "line_terminator", Util.escapeLineTerminator(data.getLineTerminator()));
        this.printer.printRecord("data", "null_value", data.getNullValue());
        Map<Integer, FieldDescription<?>> descriptionByIndex = data.getDescriptionByIndex();
        List<Integer> indices = new ArrayList<Integer>(descriptionByIndex.keySet());
        Collections.sort(indices);
        for (int i : indices) {
            FieldDescription<?> description = descriptionByIndex.get(i);
            StringBuilder out = new StringBuilder();
            description.render(out);
            this.printer.printRecord("data", "col/" + i + "/type", out.toString());
        }
    }
}
