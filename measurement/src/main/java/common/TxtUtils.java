package common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class TxtUtils {
    protected BufferedWriter writer;

    public TxtUtils(String filepath) {
        File file = new File(filepath);

        try {
            file.createNewFile();
            this.writer = new BufferedWriter(new FileWriter(file));
        } catch (Exception var4) {
            System.err.println("failed to create " + filepath);
            var4.getMessage();
            var4.getStackTrace();
        }

    }

    public synchronized void writeNFlush(String str) {
        try {
            this.writer.write(str);
            this.writer.flush();
        } catch (Exception var3) {
            var3.getMessage();
            var3.getStackTrace();
        }

    }

    public synchronized void write(String str) {
        try {
            this.writer.write(str);
        } catch (Exception var3) {
            var3.getMessage();
            var3.getStackTrace();
        }

    }

    public synchronized void flushBuffer() {
        try {
            this.writer.flush();
        } catch (Exception var2) {
            var2.getMessage();
            var2.getStackTrace();
        }

    }

    public void closeWriter() {
        try {
            this.writer.close();
        } catch (Exception var2) {
            var2.getMessage();
            var2.getStackTrace();
        }

    }
}
