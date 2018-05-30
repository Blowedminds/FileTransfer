package project;

import util.HashGeneration;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.Queue;

public class FileHandler {

    public static Queue<String> readAndParseLines(String path) {

        BufferedReader reader = readFile(path);

        Queue<String> paths = new LinkedList<>();

        if (reader == null) {
            return paths;
        }

        try {
            for (String line; (line = reader.readLine()) != null; ) {
                paths.add(line);
            }
        } catch (IOException e) {
            System.out.println("While reading opening file an error occured");
        }

        return paths;
    }

    public static Queue<Node> hashFiles(Queue<String> chunksPaths) {

        Queue<Node> hashedChunks = new LinkedList<>();

        while (!chunksPaths.isEmpty()) {

            String path = chunksPaths.poll();

            Node node = new Node(hashFileInThePath(path));

            node.setChildren(new Node(path), new Node(path));

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
