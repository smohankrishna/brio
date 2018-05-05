package net.hova_it.barared.brio.apis.hw.printer.document.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Documento que se puede imprimir en la impresora POS
 *
 * Created by Herman Peralta on 17/02/2016.
 */
public class PrintableDocument {
    public List<Section> sections;

    public PrintableDocument() {
        sections = new ArrayList<>();
    }

    public void addSection(Section section) {
        sections.add(section);
    }
}
