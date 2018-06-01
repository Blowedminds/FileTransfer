package project;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class Torrent {

    String trustedSource;
    Queue<String> filesSource;
    String downloadPath;

    public Torrent(String trustedSource, Queue<String> filesSource, String downloadPath) {

        this.trustedSource = trustedSource;
        this.filesSource = filesSource;
        this.downloadPath = downloadPath;
    }

    public boolean processDownload(String altSource) {

        Queue<String> downloadedFiles = this.downloadFromSource(this.filesSource);

        if(downloadedFiles == null) {
            System.out.println("Download failed");
            return false;
        }

        MerkleTree merkleTree = new MerkleTree(downloadedFiles);

        boolean checkAuthenticity = merkleTree.checkAuthenticity(this.trustedSource);

        if(checkAuthenticity) {
            return true;
        }

        ArrayList<Stack<String>> corruptChunks = merkleTree.findCorruptChunks(this.trustedSource);

        Queue<String> replaceChunksPaths = new LinkedList<>();

        for(Stack<String> stack: corruptChunks) {
            replaceChunksPaths.add(merkleTree.findCorruptPaths(stack));
        }

        Queue<String> altDownloaded = this.downloadFromSource(this.findFilesInAltSource(altSource, replaceChunksPaths));

        return altDownloaded != null;
    }

    private Queue<String> findFilesInAltSource(String altSource, Queue<String> replaceChunksPaths) {

        Queue<String> altSourceUrls = FileHandler.readAndParseLines(altSource);
        Queue<String> toDownload = new LinkedList<>();
        while(!replaceChunksPaths.isEmpty()) {

            String path = replaceChunksPaths.poll();

            String urlPath;

            while(!altSourceUrls.isEmpty()) {
                urlPath = altSourceUrls.poll();

                if(URLHandler.findInString(urlPath, '/', 2).equals(URLHandler.findInString(path, '/', 2))) {
                    toDownload.add(urlPath);
                    break;
                }
            }
        }

        return toDownload;
    }

    private Queue<String> downloadFromSource(Queue<String> files) {

        Queue<String> filesPaths = new LinkedList<>();

        while(!files.isEmpty()) {

            String path = URLHandler.downloadFile(files.poll(), this.downloadPath);

            if(path == null)
                return null;
            else
                filesPaths.add(path);
        }

        return filesPaths;
    }
}
