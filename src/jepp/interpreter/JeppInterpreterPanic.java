package jepp.interpreter;

public class JeppInterpreterPanic extends JeppInterpreterException {
    public JeppInterpreterPanic(String reason) {
        super("Something went really wrong : " + reason);
    }

    public JeppInterpreterPanic() {
        super("Something went really wrong :(");
    }
}
