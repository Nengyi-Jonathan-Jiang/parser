package jepp.interpreter;

public class JeppInterpreterPanic extends JeppInterpreterException {
    public JeppInterpreterPanic() {
        super("Something went really wrong :(");
    }
}
