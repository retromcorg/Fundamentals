package com.johnymuffin.fundamentals;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class Merger {

    public static void main(String[] args) throws IOException, ParseException {
        System.out.println("Fundamentals Merger Version 1.0.0"); //Hardcoded, yes

        File server1 = new File("Server 1" + File.separator);
        File server2 = new File("Server 2" + File.separator);

        if (!server1.exists() || !server2.exists()) {
            System.out.println("Generating folders for Fundamentals player files. Please place the files from both servers in to different folders and rerun this program.");
            server1.mkdirs();
            server2.mkdirs();
            return;
        }
        System.out.println("Starting the merge process.");

        JSONParser parser = new JSONParser();
        ArrayList<UUID> knownUsers = new ArrayList<>();
        //Scan directory 1
        for (File file : server1.listFiles()) {
            String uuid = file.getName();
            uuid = uuid.replace(".json", "");
            if (!knownUsers.contains(uuid)) {
                knownUsers.add(UUID.fromString(uuid));
            }
        }
        //Scan directory 2
        for (File file : server2.listFiles()) {
            String uuid = file.getName();
            uuid = uuid.replace(".json", "");
            if (!knownUsers.contains(uuid)) {
                knownUsers.add(UUID.fromString(uuid));
            }
        }

        int copyCount = 0;
        int mergeCount = 0;
        for (UUID uuid : knownUsers) {
            File server1File = new File(server1 + File.separator + uuid + ".json");
            File server2File = new File(server2 + File.separator + uuid + ".json");


            if (!server1File.exists() || !server2File.exists()) {
                System.out.println(uuid.toString() + " only exists on one server. Skipping merge and copying.");
                //TODO: Copy to output directory
                copyCount++;
                continue;
            }
            mergeCount++;
            JSONObject server1Json = (JSONObject) parser.parse(new FileReader("Server 1" + File.separator + uuid.toString() + ".json"));
            JSONObject server2Json = (JSONObject) parser.parse(new FileReader("Server 2" + File.separator + uuid.toString() + ".json"));

            if (server1Json.containsKey("username")) {
                System.out.println("Merging user " + server1Json.get("username") + " (" + uuid + ")");
            }

            JSONObject mergedData = mergeJSONObjects(server1Json, server2Json, true);
            mergedData.remove("homes");
            mergedData.put("homes", mergePlayerHomes((JSONObject) server1Json.get("homes"), (JSONObject) server2Json.get("homes"), uuid));

            String mergedDataString = mergedData.toJSONString();

            //Write string to file
            FileWriter myWriter = new FileWriter("Output" + File.separator + uuid.toString() + ".json");
            myWriter.write(mergedDataString);
            myWriter.close();
        }

        System.out.println("Merged " + mergeCount + "/" + (mergeCount + copyCount) + " users.");
        System.out.println("Copied " + copyCount + "/" + (mergeCount + copyCount) + " users.");
//
//        for(File file : server1.listFiles()) {
//            try {
//                JSONObject json = (JSONObject) parser.parse(new FileReader(file));
//                String username = (String) json.get("username");
//                knownUsers.add(username);
//            } catch (Exception e) {
//                System.out.println("Error reading file: " + file.getName());
//            }
//        }


    }


    public static JSONObject mergePlayerHomes(JSONObject homeList1, JSONObject homeList2, UUID uuid) {
        if (homeList1 == null) {
            if (homeList2 == null) {
                return null;
            } else {
                return homeList2;
            }
        }else if (homeList2 == null) {
            if (homeList1 == null) {
                return null;
            } else {
                return homeList1;
            }
        }

        JSONObject mergedHomeList = new JSONObject();
        for (Object key : homeList1.keySet()) {
            mergedHomeList.put(key, homeList1.get(key));
        }
        for (Object key : homeList2.keySet()) {
            if (!mergedHomeList.containsKey(key)) {
                System.out.println("Merging home " + key + " for UUID " + uuid);
                mergedHomeList.put(key, homeList2.get(key));
            } else {
                int counter = 0;
                while (mergedHomeList.containsKey(key + "_" + counter)) {
                    counter++;
                }
                System.out.println("Duplicate home name " + key + " for UUID " + uuid + " found. Renaming to " + key + "_" + counter);
                mergedHomeList.put(key + "_" + counter, homeList2.get(key));
            }
        }
        return mergedHomeList;
    }

    //Merge JSON objects and child objects into one JSON object.
    public static JSONObject mergeJSONObjects(JSONObject obj1, JSONObject obj2, boolean mergeChild) {
        JSONObject mergedObject = new JSONObject();
        for (Object key : obj1.keySet()) {
            mergedObject.put(key, obj1.get(key));
        }
        for (Object key : obj2.keySet()) {
            if (!mergedObject.containsKey(key)) {
                mergedObject.put(key, obj2.get(key));
            } else {
                if (mergedObject.get(key) instanceof JSONObject && mergeChild) {
                    System.out.println("Merging JSON objects for key: " + key);
                    JSONObject childObject = (JSONObject) mergedObject.get(key);
                    JSONObject childObject2 = (JSONObject) obj2.get(key);
                    mergedObject.put(key, mergeJSONObjects(childObject, childObject2, false));
                } else {
                    mergedObject.put(key, obj2.get(key));
                }
            }

            mergedObject.put(key, obj2.get(key));
        }
        return mergedObject;
    }


}
