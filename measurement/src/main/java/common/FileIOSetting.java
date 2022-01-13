package common;

import org.apache.commons.io.filefilter.WildcardFileFilter;

import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileIOSetting {
    public static String root;
    public static String taskOutName;
    public static String taskOutPath;
    public static File taskOutFile;

    public static String LOCAL_O_PREFIX;

    public static String outputDir(){
        return taskOutPath;
    }

    public static void pack2dnDir() {
        String zipFileName = taskOutFile.getAbsolutePath() + ".zip";
        zipIt(zipFileName, taskOutFile.getAbsolutePath());
        deleteAll(taskOutFile);
    }

    public static void mkResDir(String outputDirNoSlash) {
        LOCAL_O_PREFIX = outputDirNoSlash;
        root = LOCAL_O_PREFIX + "/";

        taskOutName = root + "task" + LocalDateTime.now().format(Constants.DATETIME_SERIES);
        taskOutPath = taskOutName + "/";

        taskOutFile = new File(taskOutName);
        taskOutFile.mkdirs();
    }

    public static void zipIt(String zipFile, String source_folder) {
        byte[] buffer = new byte[1024];
        String source = new File(source_folder).getName();
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            fos = new FileOutputStream(zipFile);
            zos = new ZipOutputStream(fos);

            FileInputStream in = null;

            List<File> fileList = (List<File>) FileUtils.listFiles(new File(source_folder), new WildcardFileFilter("*"), null);
            for (File file: fileList) {
                ZipEntry ze = new ZipEntry(source + File.separator + file.getName());
                zos.putNextEntry(ze);
                try {
                    in = new FileInputStream(file);
                    int len;
                    while ((len = in .read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                } finally {
                    in.close();
                }
            }

            zos.closeEntry();

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                zos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static void deleteAll(File file) {

        if (file.isFile() || file.list().length == 0) {
            file.delete();
        } else {
            for (File f : file.listFiles()) {
                deleteAll(f); // 递归删除每一个文件

            }
            file.delete(); // 删除文件夹
        }
    }
}
