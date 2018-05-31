package project;

import util.HashGeneration;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.Queue;

public class FileHandler {

    public static LinkedList<String> readAndParseLines(String path) {
        return readAndParseLines(path, -1);
    }

    public static LinkedList<String> readAndParseLines(String path, int upto) {

        BufferedReader reader = readFile(path);

        LinkedList<String> paths = new LinkedList<>();

        if (reader == null) {
            return paths;
        }

        try {
            int i = 0 ;
            for (String line; (line = reader.readLine()) != null; ) {
                if(upto != -1 && i >= upto) {
                    break;
                }
                paths.add(line);
                i++;
            }
        } catch (IOException e) {
            System.out.println("While opening file an error occurred");
        }

        return paths;
    }

    public static LinkedList<Node> hashFiles(Queue<String> chunksPaths) {

        LinkedList<Node> hashedChunks = new LinkedList<>();

        while (!chunksPaths.isEmpty()) {

            String path = chunksPaths.poll();

            Node node = new Node(hashFileInThePath(path));

            node.setChildren(new Node(path), null);

            hashedChunks.add(node);
        }

        return hashedChunks;
    }

    private static String hashFileInThePath(String path) {

        try {
            return HashGeneration.generateSHA256(new File(path));
        } catch (IOException e) {
            System.out.print("File cannot be read");
        } catch (NoSuchAlgorithmException e) {
            System.out.print("No such algorithm");
        }

        return "";
    }

    private static BufferedReader readFile(String path) {
        try {
            return new BufferedReader(new FileReader(path));
        } catch (FileNotFoundException e) {
            System.out.print("File not found");
        }

        return null;
    }
}
