# 🔥 DataStax Astra Full Demo 🔥

This is a full blown demo of DataStax Astra and uses three drivers - Python, Javascript and Java, and Astra Data API with GraphQL. It also demonstrate how to connect Astra with Datastax client tools such as DSBULK, CQLSH etc. and also an integration with visualization tool, KNOWI

## Pre-requisites:
  1. Nodejs installed
  2. Python installed
  3. Mavern installed (or any IDE for Java)
  4. DataStax client tools, DSBULK and CQLSH (instructions given)
  5. An DataStax Astra instance created (instructions given)


# STEP -1 Create an Astra instance
  note: you can skip this step if you have already have an instance. pls make sure you copy the secure connect bundle to the /connect folder in your project

  Follow the instruction in the following link (Do only the first two steps - 1. Create an Astra Database; 2. Download the secure connect bundle)
  https://docs.datastax.com/en/astra/aws/doc/dscloud/astra/dscloudGettingStarted.html#dscloudTour
  copy the secure connect bundle to the /connect folder in your project

# STEP -2 Install client tools
  note: you can skip this step if you have the following DataStax tools installed. CQLSH must be the Astra version
  1. Download and install CQLSH tool:
    https://docs.datastax.com/en/astra/aws/doc/dscloud/astra/dscloudConnectcqlshConsole.html#dscloudConnectcqlshConsole
  2. Download and install DSBULK tool:
    https://docs.datastax.com/en/dsbulk/doc/dsbulk/install/dsbulkInstall.html

# STEP -3 Define Schemas
  1. cd to /source/cql in your project folder

    $ cd /source/cql

  2. Start CQLSH

    $ cqlsh -u username -p password -b /path/to/secure-connectdatabase_name.zip

  3. create tables and indexes by running the following commands on your CQLSH

      source 'schema.cql'

      source 'index.cql';

  4. List the tables and indexes you created

      source 'listTables.cql';

      source 'listIndexes.cql';

# Step -4 load Data
  load all the tables using the dsbulk command in source/bulkload/load_scripts
  there are five tables to be loaded.
  Pls make sure you change: userId, Password, secure bundle file loc, keyspacename and the location of your datafile  

# Step -5 check your dataload
  This script is using python driver.
  1. install Datastax cassandra driver:

    $ pip install cassandra-driver

  2. cd to /yourproject/source/pythons

    $ cd  /yourproject/source/python

  3. edit the environment variables in your /source/demo.env (userid, password, secure bundle loc, kespace name etc.)

  4. run the python script:

    $ Python3 listTables.py

  more on connecting to Astra using Python driver:
  https://docs.datastax.com/en/astra/aws/doc/dscloud/astra/dscloudConnectPythonDriver.html

# Step -6 merge tables
  In this step, we are aggregating data from storm data  and merging it with economic data (CRUD operation example)
  1. run the python script:

    $ Python3 mergeTables.py

# Step -7 merge tables
  In this step, we are using Node.js driver and updating an existing table after aggregating data from policy_info
  1. Install Datastax Node.js driver:

    $ npm install cassandra-driver

  2. edit the environment variables in your /source/demo.env (userid, password, secure bundle loc, kespace name etc.)
  2. cd to your js folder:

    $ cd /yourproject/source/js

  3. run the script

    $ node updatePolicyLosses.js

  more on connecting to Astra using Node.js driver:
  https://docs.datastax.com/en/astra/aws/doc/dscloud/astra/dscloudConnectNodeJsDriver.html

# Step -8 Run the risk analysis java application
  1. Download and install Maven.
    download: https://maven.apache.org/download.cgi
    install: https://maven.apache.org/install.html
  2. create a mavern app in your project folder

    $ cd /yourprojectfolder/

    $ mvn archetype:generate -DgroupId=com.datastax.app -DartifactId=myApp -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4 -DinteractiveMode=false

  3. replace the pom.xml file in your newly created myApp folder with the pom.xml in your /source/java folder
     note: This step adds the DataStax Java driver dependency to your pom.xml file

    $ cd /myApp

    $ cp ../source/java/pom.xml .

  4. copy the file riskFactor.java from /source/java folder to /myApp/src/main/java/com/datastax/app

    $ cp ../source/java/riskFactor.java ./src/main/java/com/datastax/app

  5. compile the application

    $ mvn clean install

  6. run the application

    $ mvn exec:java -Dexec.mainClass="com.datastax.app.riskFactor" -Dexec.args="/myproject directory/source/demo.env"

  note: the argument is the path to demo.env file which contains the database credentials. pls make sure the demo.env file has the correct credentials

# Step -9 DataStax Astra Data API (REST API)
  1. Get an authorization token (this step is required before running any operation on Data API. The token expires after 30 mts)
     Scripts are available in your projects /soure/RestAPI/scriptsCURl file. The first script is to get an authorization token.
     note: you need to change the url, userId and password in the script
  2. Test any of the scripts after replacing the authorization token you just received.
  3. Run the GraphQL query in the script after replacing the authorization token you just received
  note: GraphQL require a table named 'products' with a primary key "id". you can try your CQL skills at DataStudio or use Data API and create a table and load a few rows.
     more reading on Data API:
     https://astra.readme.io/docs/getting-started-with-datastax-astra

# Step -10 Knowi tool integration
  Knowi is an analytical platform which can connect to Astra. Sign-up and get a trial account at https://www.knowi.com/home
  There is an Astra Driver for Knowi once you log-on, click and configure it. Once you are done with configuring, you can use the sample query in the /image folder and do a join
