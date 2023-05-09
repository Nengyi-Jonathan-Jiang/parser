package compiler;

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
        return this.type.string.equals(this.value) ? this.value : this.type + "<" + this.value + ">";
    }
}