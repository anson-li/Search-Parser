package datastructs;

import com.sleepycat.db.*;

static class StringEntry extends DatabaseEntry {
        
        StringEntry() {
        }

        StringEntry(String value) {
            setString(value);
        }

        void setString(String value) {
            byte[] data = value.getBytes();
            setData(data);
            setSize(data.length);
        }

        String getString() {
            return new String(getData(), getOffset(), getSize());
        }
    }