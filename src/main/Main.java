package main;

import java.io.File;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Stack;

import project.FileHandler;
import project.MerkleTree;
import project.Torrent;
import project.URLHandler;

public class Main {

    public static void main(String[] args) {


        MerkleTree m0 = new MerkleTree("secondaryPart/data/8local.txt");
        String hash = m0.getRoot().getLeft().getRight().getData();
        System.out.println(hash);

        boolean valid = m0.checkAuthenticity("secondaryPart/data/8meta.txt");
        System.out.println(valid);

        // The following just is an example for you to see the usage.
        // Although there is none in reality, assume that there are two corrupt chunks in this example.
        ArrayList<Stack<String>> corrupts = m0.findCorruptChunks("secondaryPart/data/8meta.txt");
        System.out.println(corrupts.size());
//        System.out.println("Corrupt hash of first corrupt chunk is: " + corrupts.get(0).pop());
//        System.out.println("Corrupt hash of second corrupt chunk is: " + corrupts.get(1).pop());

//		download("secondaryPart/data/download_from_trusted.txt");

    }

    public static void download(String torrentsPath) {
        //extract torrent data
        Queue<String> torrentsData = FileHandler.readAndParseLines(torrentsPath);

        ArrayList<String[]> torrents = divideTorrents(torrentsData);

        //work on torrents individually
        for(String[] torrent: torrents){
            if(!handleTorrent(torrent)) {
                System.out.println("An error occurred while handling torrent: " + torrent[0]);
            }
        }
    }

    public static ArrayList<String[]> divideTorrents(Queue<String> torrentsFile) {

        ArrayList<String[]> torrents = new ArrayList<>();

        String[] torrent;

        while(!torrentsFile.isEmpty()) {

            torrent = new String[3];

            for(int i = 0; i < 3 && !torrentsFile.peek().equals("") ; i++) {
                torrent[i] = torrentsFile.poll();
            }

            torrentsFile.poll();

            torrents.add(torrent);
        }

        return torrents;
    }

    public static boolean handleTorrent(String[] torrentData) {

        String path = "secondaryPart/data/";
        //BUG:
        String torrentNumber = torrentData[1].substring(torrentData[1].length() - 5, torrentData[1].length() - 4);

        Queue<String> filesUrl = FileHandler.readAndParseLines(URLHandler.downloadFile(torrentData[1], path));
        String trustedSourceMeta = URLHandler.downloadFile(torrentData[0], path);
        String alternateSource = URLHandler.downloadFile(torrentData[2], path);

        Torrent torrent = new Torrent(trustedSourceMeta, filesUrl, path + "split/" + torrentNumber + "/");

        if(!torrent.processDownload(alternateSource)) {
            return false;
        }

        return true;
    }
}
