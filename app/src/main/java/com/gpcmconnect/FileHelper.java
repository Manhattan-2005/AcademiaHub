package com.gpcmconnect;

import android.os.Environment;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileHelper {

    private static final String FILE_NAME = "your_file_name.txt";

    public static File getOrCreateFile() {
        File directory = new File(Environment.getExternalStorageDirectory(), "your_directory_name");

        // Check if the directory exists; if not, create it
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, FILE_NAME);

        // Check if the file exists; if not, create it
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }

    public static void writeToFile(File file, String data) {
        try {
            FileWriter writer = new FileWriter(file, true); // Use true to append data
            writer.append(data);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
