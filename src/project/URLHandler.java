package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class URLHandler {
    //https://stackoverflow.com/questions/921262/how-to-download-and-save-a-file-from-internet-using-java
    public static String downloadFile(String url, String path) {
        String fileName = findInString(url, '/', 1);
        try {
            URL connection = new URL(url);
            ReadableByteChannel rbc = Channels.newChannel(connection.openStream());
            File newFolder = new File(path);
            newFolder.mkdirs();
            FileOutputStream fos = new FileOutputStream(new File(newFolder, fileName));
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return path + fileName;
    }

    public static String findInString(String url, char toBreak, int k) {
        return findInString(url, toBreak, k, false);
    }

    public static String findInString(String url, char toBreak, int k, boolean reverse) {
        if(url.charAt(url.length() - 1) == toBreak) {
            return "";
        }

        int i, j = 0;
        for(i = url.length() - 1; i > 0; i--) {
            if(url.charAt(i) == toBreak) j++;
            if(j == k) break;
        }

        return reverse ? url.substring(0, i) : url.substring(i + 1, url.length());
    }
}
