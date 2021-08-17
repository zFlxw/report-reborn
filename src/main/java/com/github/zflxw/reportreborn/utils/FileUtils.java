package com.github.zflxw.reportreborn.utils;

import com.github.zflxw.reportreborn.ReportReborn;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;

public class FileUtils {
    /**
     * write a given content into a file
     * @param dest the file to write in
     * @param content the content to write in the file
     * @return the provided content
     */
    public String write(File dest, String content) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(dest));
            writer.write(content);

            writer.flush();
            writer.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return content;
    }

    /**
     * read the content of a file with line separation
     * @param file the file to read the content from
     * @return the content of the file
     */
    public String read(File file) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader bufferedReader = Files.newBufferedReader(file.toPath());

            bufferedReader.lines().forEach(line -> stringBuilder.append(line).append("\n"));
            bufferedReader.close();

            return stringBuilder.toString();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    /**
     * read the content of a resource file from classpath.
     * @param name the name of the resource
     * @return the content of the file
     */
    public String readResource(String name) {
        try {
            InputStream inputStream = ReportReborn.class.getClassLoader().getResourceAsStream(name);
            StringWriter stringWriter = new StringWriter();

            IOUtils.copy(inputStream, stringWriter, "UTF-8");
            return stringWriter.toString();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }
}
