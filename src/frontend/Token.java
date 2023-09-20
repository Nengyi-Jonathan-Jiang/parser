package frontend;

public class Token {
    public final Symbol type;
    public final String value;
    public final int index;

    public Token(Symbol type, String value, int index){
        this.type = type;
        this.value = value;
        this.index = index;
    }
    public String toString(){
        return type.name.equals(value) ? value : value.isEmpty() ? type.toString() : type + "<" + value + ">";
    }
    public boolean isEOF() {
        return type == type.getTable().__END__;
    }
}