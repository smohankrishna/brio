package net.hova_it.barared.brio.apis.hw.printer.document.api;

/**
 * Bloque de texto
 *
 * Created by Herman Peralta on 16/02/2016.
 */
public class TextBlock extends Block {
    public final static float ATTR_TEXT_SIZE_BIG = 13.0f;
    public final static float ATTR_TEXT_SIZE_NORMAL = 11.0f;
    public final static float ATTR_TEXT_SIZE_SMALL = 10.0f;
    public final static float ATTR_TEXT_SIZE_MSMALL = 8.0f;

    public String text;
    public boolean attr_bold = false;
    public boolean attr_italic = false;

    public TextBlock(String text) {
        this.text = text;
        attr_size = ATTR_TEXT_SIZE_NORMAL;
    }

    public TextBlock(String text, float text_size) {
        this.text = text;
        attr_size = text_size;
    }

    public TextBlock(String text, float text_size, boolean bold, boolean italic) {
        this.text = text;

        attr_size = text_size;
        attr_bold = bold;
        attr_italic = italic;
    }
}
