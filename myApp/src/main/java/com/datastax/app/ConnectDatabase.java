package com.datastax.app;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import java.nio.file.Paths;
import java.util.*;
import java.io.*;

public class ConnectDatabase {

  public static List<String[]> getEnvData (String filePath)
  {
    List<String[]> totLines = new ArrayList<String[]>();

    try
    {
        File file = new File(filePath);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null)
        {
            String array1[]= line.split(":");
            totLines.add(array1);
        }
        br.close();
    }
    catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
    //String result = null;

    return totLines;
  }

   public static void main(String[] args) {
       // Create the CqlSession object:
       String path = args[0];
       List<String[]> envData = getEnvData(path);

/*    get connection details from demo.env file
*/
       String userId, passWord, secureConnectBundle, keySpace;
       userId = passWord = secureConnectBundle = keySpace = "";

       for(String[] element : envData) {
         switch (element[0]){
           case "userId" :
             userId = element[1];
             break;
           case "passWord" :
             passWord = element[1];
             break;
           case "secure_connect_bundle" :
             secureConnectBundle = element[1];
             break;
           case "keyspace" :
             keySpace = element[1];
             break;
         }
       }

/*       System.out.println("userId = "+userId+" passWord = "+passWord+" connect = "+ secureConnectBundle+" keyspace = "+keySpace);
*/
       try (CqlSession session = CqlSession.builder()
           .withCloudSecureConnectBundle(Paths.get(secureConnectBundle))
           .withAuthCredentials(userId,passWord)
           .withKeyspace(keySpace)
           .build()) {
           // Select the release_version from the system.local table:
           ResultSet rs = session.execute("select release_version from system.local");
           Row row = rs.one();
           //Print the results of the CQL query to the console:
           if (row != null) {
               System.out.println(row.getString("release_version"));
           } else {
               System.out.println("An error occurred.");
           }
       }

       System.exit(0);
   }
}
