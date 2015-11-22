package datastructs;

import com.sleepycat.db.*;

public class StringEntry extends DatabaseEntry {
        
    StringEntry() {
    }

    StringEntry(String value) {
        setString(value);
    }

    public void setString(String value) {
        byte[] data = value.getBytes();
        setData(data);
        setSize(data.length);
    }

    public String getString() {
        return new String(getData(), getOffset(), getSize());
    }
}