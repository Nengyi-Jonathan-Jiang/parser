package frontend;

import java.util.Objects;

public final class Location {
    public final int index;
    public final int column;
    public final int line;

    public Location(int index, int line, int column) {
        this.index = index;
        this.column = column;
        this.line = line;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        return obj instanceof Location that && this.index == that.index && this.column == that.column && this.line == that.line;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, column, line);
    }

    @Override
    public String toString() {
        return line + ":" + column;
    }
}