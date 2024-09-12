package frontend.parser.parser_generator.item;

import util.comparableSet.ComparableTreeSet;
import util.SerializableToString;

public class ItemSet extends ComparableTreeSet<Item> implements SerializableToString {

    public ItemSet() {
    }

    public ItemSet(Item item) {
        add(item);
    }

    @Override
    public void serializeToStringBuilder(StringBuilder stringBuilder) {
        stringBuilder.append("{");
        for (Item it : this) {
            stringBuilder.append("\n\t");
            stringBuilder.append(it.toString());
        }
        stringBuilder.append("\n}");
    }

    @Override
    public String toString() {
        return serializeToString();
    }

    public ItemSet copy() {
        return (ItemSet) clone();
    }
}