package compiler.jevm;

public class NativeVM {
    static {
        System.loadLibrary("native_vm");
    }

    public native void execute(byte[] array);

    public static void main(String[] args) {
        NativeVM helloWorld = new NativeVM();
        helloWorld.execute(new byte[]{0, 1, 2, 3, 4});
    }
}
