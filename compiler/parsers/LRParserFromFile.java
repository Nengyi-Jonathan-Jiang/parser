package compiler.parsers;

import compiler.items.Item;
import compiler.items.ItemSet;

public class LRParserFromFile extends LRParser{

    public LRParserFromFile(String filename){
        super(filename);
    }

    @Override
    public ItemSet closure(Item item){
        return null;
    }
}
