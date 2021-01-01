/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package datastore;

/**
 *
 * @author thngm
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

/**
 *
 * @author thngm
 */

//import java.util.*;
//import java.io.*;

public class DataStore {

  /**
     * @param args the command line arguments
     */
  static HashMap < String,String > map = new HashMap < String,String > ();
  static HashMap < String,Long > map1 = new HashMap < String,Long > ();
  static DataStore obj = new DataStore();
  public static void main(String[] args) {
    // TODO code application logic here
    String key,value;
    int selection;
    Long ttl;
    char choice;
    Scanner console = new Scanner(System. in );

    do {
      // Get the value from the user.
      System.out.println("1.CREATE ");
      System.out.println("2.READ ");
      System.out.println("3.DELETE ");
      System.out.println("4.EXIT");
      System.out.print("Enter choice: ");
      selection = console.nextInt();

      switch (selection) {
      case 1:
        System.out.println("enter key ");
        key = console.next();
        System.out.println("enter value ");
        value = console.next();
        System.out.println("enter TTL(Time To Live) ");
        ttl = console.nextLong();
        obj.create(key, value, ttl);
        break;

      case 2:
        System.out.print("enter key ");
        key = console.next();
        obj.read(key);
        break;

      case 3:
        System.out.print("enter key ");
        key = console.next();
        obj.delete(key);
        break;
      case 4:
        System.exit(0);
        break;

      default:
        System.out.print("Not a valid choice ");

      }

      System.out.print("Enter Y for yes or N for no: ");
      choice = console.next().charAt(0);
    }
    while (( choice == 'y') || (choice == 'Y'));
  }

  public void writeToFile(HashMap < String, String > map, HashMap < String, Long > map1) {
    //write to file : "newfile"
    try {
      File toWrite = new File("datafile.txt");
      FileOutputStream fos = new FileOutputStream(toWrite);
      PrintWriter pw = new PrintWriter(fos);
      for (Map.Entry < String, String > entry1: map.entrySet()) {
        String key = entry1.getKey();
        String value1 = entry1.getValue();
        Long value2 = map1.get(key);
        pw.println(key + ":" + value1 + ":" + value2);
      }
      pw.flush();
      pw.close();
      fos.close();
    }

    catch(Exception e) {
      System.out.println("Cannot Write To File!!");

    }
  }

  public void readFromFile(String key) {
    //read from file 
    try {

      File toRead = new File("datafile.txt");
      FileInputStream fis = new FileInputStream(toRead);

      Scanner sc = new Scanner(fis);
      String currentLine,
      Key,
      Value,
      TTL;
      //hashmap to store key-value pair
      HashMap < String,String > keyValueMap = new HashMap < String,String > ();
      //hashmap to store key-TimeToLive pair
      HashMap < String,Long > keyTTLmap = new HashMap < String,Long > ();

      while (sc.hasNextLine()) {
        currentLine = sc.nextLine();
        String[] line = new String[3];
        line = currentLine.split(":");
        keyValueMap.put(line[0], line[1]);
        keyTTLmap.put(line[0], Long.parseLong(line[2]));

      }

      if ((Long) System.currentTimeMillis() < keyTTLmap.get(key)) {
        System.out.println(key + " : " + keyValueMap.get(key));
      }
      else {
        System.out.println("error: key has expired");
        keyTTLmap.remove(key);
        keyTTLmap.remove(key);
      }

      fis.close();
    }

    catch(Exception e) {
      System.out.println("Cannot Read From File!!");
    }

  }

  public void create(String key, String value, Long ttl) {
    if (map.containsKey(key)) {
      System.out.println("error: this key already exists");
    }
    else {
      //constraints for file size less than 1GB and Jasonobject value less than 16KB
      if ((map.size() < (1024 * 1024 * 1024)) && (value.length() <= (16 * 1024 * 1024))) {
        if (ttl == 0) {
          map1.put(key, 1234L);

        }

        else {
          map1.put(key, (((Long) System.currentTimeMillis()) + ttl));

        }
        //constraints for input key_name capped at 32chars
        if (key.length() <= 32) {
          map.put(key, value);
          obj.writeToFile(map, map1);

        }
      }
      else {
        System.out.println("error: Memory limit exceeded!!");
      }
    }
  }
  void read(String key) {
    if (!map.containsKey(key)) {
      System.out.println("error: given key does not exist in database. Please enter a valid key");
    }
    else {
      obj.readFromFile(key);
    }
  }
  void delete(String key) {
    if (!map.containsKey(key)) {
      System.out.println("error: given key does not exist in database. Please enter a valid key");
    }
    else {
      map.remove(key);
      map1.remove(key);
      System.out.println("key is successfully deleted");
      System.out.println(map);
      obj.writeToFile(map, map1);
    }
  }
}