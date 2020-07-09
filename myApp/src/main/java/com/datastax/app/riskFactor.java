package com.datastax.app;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.*;
//import com.datastax.oss.driver.api.core.cql.PreparedStatement;
//import com.datastax.oss.driver.api.core.cql.ResultSet;

//import com.datastax.oss.driver.api.core.cql.Row;
import java.nio.file.Paths;
import java.util.*;
import java.io.*;

public class riskFactor {

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

     /*    get connection details from demo.env file
     */
     String path = args[0];
     List<String[]> envData = getEnvData(path);

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
       // Create the CqlSession object:
       try (CqlSession session = CqlSession.builder()
           .withCloudSecureConnectBundle(Paths.get(secureConnectBundle))
           .withAuthCredentials(userId,passWord)
           .withKeyspace(keySpace)
           .build()) {
           String acsQry = "select child_avg, medianincome, housingunits from acs_data where fips_st_code =?";
           String policyQry ="select sum(policy_loss_count) as pLossCount, sum(policies_count) as pCount, sum(payments) as pPayments from policy_losses";
           ResultSet rs;
           PreparedStatement prepared = session.prepare(acsQry);
           // Select the release_version from the system.local table:
           rs = session.execute(prepared.bind(0));
           Double national_child_avg =0.0;
           int national_medianincome =0;
           int national_housingunits =0;
           int national_policy_loss_count =0;
           int national_policy_count =0;
           Double national_total_payments = 0.0;

           // get national average;
           for (Row nationalAcsData: rs) {
             national_child_avg = nationalAcsData.getDouble("child_avg");
             national_medianincome = nationalAcsData.getInt("medianincome");
             national_housingunits = nationalAcsData.getInt("housingunits");
           }

           Row row = session.execute(policyQry).one();
           national_policy_loss_count = row.getInt("pLossCount");
           national_policy_count = row.getInt("pCount");
           national_total_payments = row.getDouble("pPayments");

           System.out.println("national Summary: "+national_child_avg+" "+national_medianincome+" "+national_housingunits+" "+
                              national_policy_loss_count+" "+national_policy_count+" "+national_total_payments
                             );

           String policyQry2 = "select * from policy_losses";
           String incidentQry = "select * from incidents where fips_state = ?";

           rs = session.execute(policyQry2);

           for (Row policyLosses: rs) {
             int policy_loss_count = policyLosses.getInt("policy_loss_count");
             Double payments =policyLosses.getDouble("payments");
             int policy_count = policyLosses.getInt("policies_count");
             String fips_state = policyLosses.getString("fips_state");

             double ratio_policy_loss_count = ((double)policy_loss_count/(double)national_policy_loss_count)*100;
             double ratio_policy_count = ((double)policy_count/(double)national_policy_count)*100;
             double ratio_payments = ((double)payments/(double)national_total_payments)*100;

             prepared = session.prepare(incidentQry);
             // Select the release_version from the system.local table:
             rs = session.execute(prepared.bind(policyLosses.getString("fips_state")));
             for (Row incidents: rs) {
               int fips_st_code = incidents.getInt("fips_st_code");
               int incident_year = incidents.getInt("incident_year");
               int deaths = incidents.getInt("deaths");
               int injuries = incidents.getInt("injuries");
               int acs_data_housingunits = incidents.getInt("acs_data_housingunits");
               int acs_data_median_income = incidents.getInt("acs_data_median_income");

               double ratio_housingunits = ((double)acs_data_housingunits/(double)national_housingunits)*100;
               int delta_median_income = acs_data_median_income - national_medianincome;
               System.out.println("State data: "+fips_st_code+" "+fips_state+" "
                                                +policy_loss_count+" "+payments+" "+policy_count+" "
                                                +incident_year+" "+deaths + " "+ injuries +" "
                                                +acs_data_housingunits+" "+acs_data_median_income+" "
                                                +ratio_policy_loss_count+" "+ratio_policy_count+" "+ratio_payments+" "+
                                                +ratio_housingunits +" "+delta_median_income
                                             );
             }
           }

       }
       System.exit(0);
   }
}
