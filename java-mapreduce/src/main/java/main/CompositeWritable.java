package main;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableUtils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class CompositeWritable implements Writable {

    private String checkId;
    private int visit;

    public CompositeWritable(String checkId, int visit) {
        this.checkId = checkId;
        this.visit = visit;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        WritableUtils.writeString(out, checkId);
        out.writeInt(visit);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        checkId = WritableUtils.readString(in);
        visit = in.readInt();
    }

    @Override
    public String toString() {
        return this.checkId + "," + visit;
    }
}
