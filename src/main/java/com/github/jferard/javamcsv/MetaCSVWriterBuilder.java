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

import com.github.jferard.javamcsv.processor.WriteProcessorProvider;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class MetaCSVWriterBuilder {
    private File csvFile;
    private File metaCSVFile;
    private MetaCSVData data;
    private OutputStream out;
    private OutputStream metaOut;
    private MetaCSVRenderer metaRenderer;
    private Appendable writer;
    private OnError onError;

    MetaCSVWriterBuilder() {
        onError = OnError.EXCEPTION;
    }

    public MetaCSVWriterBuilder csvFile(File csvFile) {
        this.csvFile = csvFile;
        return this;
    }


    public MetaCSVWriterBuilder metaData(MetaCSVData data) {
        this.data = data;
        return this;
    }

    public MetaCSVWriterBuilder metaCSVFile(File metaCSVFile) {
        this.metaCSVFile = metaCSVFile;
        return this;
    }

    public MetaCSVWriterBuilder out(OutputStream out) {
        this.out = out;
        return this;
    }

    public MetaCSVWriterBuilder metaOut(OutputStream metaOut) {
        this.metaOut = metaOut;
        return this;
    }

    public MetaCSVWriterBuilder metaRenderer(MetaCSVRenderer renderer) {
        this.metaRenderer = renderer;
        return this;
    }

    public MetaCSVWriterBuilder onError(OnError onError) {
        this.onError = onError;
        return this;
    }

    public MetaCSVWriter build() throws IOException {
        this.writeData();
        if (this.writer == null) {
            if (this.out == null) {
                this.out = new FileOutputStream(this.csvFile);
            }
            this.writer = new OutputStreamWriter(out, data.getEncoding());
        }
        CSVFormat format = CSVFormatHelper.getCSVFormat(data);
        CSVPrinter printer = new CSVPrinter(writer, format);
        WriteProcessorProvider writeProvider = data.toWriteProcessorProvider(onError);
        return new MetaCSVWriter(printer, writeProvider);
    }

    private void writeData() throws IOException {
        if (this.metaRenderer == null) {
            if (this.metaOut == null) {
                if (this.metaCSVFile == null) {
                    if (this.csvFile == null) {
                        return;
                    }
                    this.metaCSVFile = Util.withExtension(csvFile, ".mcsv");
                }
                this.metaOut = new FileOutputStream(metaCSVFile);
                this.metaRenderer = MetaCSVRenderer.create(metaOut);
                try {
                    this.metaRenderer.render(this.data);
                } finally {
                    this.metaOut.close();
                }
            } else {
                this.metaRenderer = MetaCSVRenderer.create(metaOut);
                this.metaRenderer.render(this.data);
            }
        }
    }
}
