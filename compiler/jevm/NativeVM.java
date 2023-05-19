package compiler.jevm;

public class NativeVM {
    static {
        System.load("C:\\Users\\slhscs212\\IdeaProjects\\parser\\compiler\\jevm\\native\\cmake-build-debug\\libnative.dll");
    }

    public native void helloWorld();
    public native String helloWorldBy(String message);

    public static void main(String[] args) {
        NativeVM helloWorld = new NativeVM();
        helloWorld.helloWorld();
        System.out.println(helloWorld.helloWorldBy("Jeremy"));
    }
}
