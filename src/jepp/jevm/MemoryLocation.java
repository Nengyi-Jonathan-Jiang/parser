package jepp.jevm;

public interface MemoryLocation {
    void setInt(int val);
    void setFloat(float val);
    int getInt();
    float getFloat();

    default float intToFloat(int i) {
        return Float.intBitsToFloat(i);
    }
    default int floatToInt(float val) {
        return Float.floatToRawIntBits(val);
    }


    default void copyFrom(MemoryLocation location) {
        setInt(location.getInt());
    }

    class Constant implements MemoryLocation {
        // We don't have to store a constant as a byte array because it doesnt matter
        int value;
        public Constant(int value) {
            this.value = value;
        }
        public Constant(float value){
            this.value = Float.floatToRawIntBits(value);
        }

        @Override
        public void setInt(int val) {
            throw new IllegalAccessError("JeVM Error: Cannot write to a constant");
        }

        @Override
        public void setFloat(float val) {
            throw new IllegalAccessError("JeVM Error: Cannot write to a constant");
        }

        @Override
        public int getInt() {
            return value;
        }

        @Override
        public float getFloat() {
            return Float.intBitsToFloat(value);
        }
    }
}