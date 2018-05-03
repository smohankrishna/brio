package net.hova_it.barared.brio.apis.hw.printer.document.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Sección, una lista de bloques
 *
 * Created by Herman Peralta on 17/02/2016.
 */
public class Section {
    public List<Block> blocks;

    public Section() {
        blocks = new ArrayList<>();
    }

    public void addBlock(Block block) {
        this.blocks.add(block);
    }
}
