package net.hova_it.barared.brio.apis.hw.printer.document.api;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Bloque de imagen
 *
 * Created by Herman Peralta on 16/02/2016.
 */
public class ImageBlock extends Block {

    public Bitmap bitmap;

    public ImageBlock(Bitmap bitmap, int size) {
        this.bitmap = bitmap;
        this.attr_size = size;
    }

    public ImageBlock(Resources resources, int resid_drawable, int size) {
        this.bitmap = BitmapFactory.decodeResource(resources, resid_drawable);
        this.attr_size = size;
    }
}
