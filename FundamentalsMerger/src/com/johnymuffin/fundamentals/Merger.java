package com.johnymuffin.fundamentals;

import java.io.File;

public class Merger {

    public static void main(String[] args) {
        System.out.println("Fundamentals Merger Version 1.0.0"); //Hardcoded, yes

        File server1 = new File("Server 1" + File.separator);
        File server2 = new File("Server 2" + File.separator);

        if(!server1.exists() || !server2.exists()) {
            System.out.println("Generating folders for Fundamentals player files. Please place the files from both servers in to different folders and rerun this program.");
            server1.mkdirs();
            server2.mkdirs();
            return;
        }
        System.out.println("Starting the merge process.");


    }
}
