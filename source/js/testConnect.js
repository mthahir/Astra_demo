'use strict'
const { Client } = require('cassandra-driver');
var fs = require('fs');

function setEnvironmentVariables(file) {
  let obj = {};
  var lines = fs.readFileSync(file).toString().split("\n");
  for(var i in lines) {
    const word = lines[i].split(':');
    obj[word[0]] = word[1];
  }
  return obj;
}

async function run(env) {

  const userId = env['userId'];
  const passWord = env['passWord'];
  const secureConnectBundle = env['secure_connect_bundle'];
//  console.log(`variables:${env} ${userId}  ${passWord} ${secureConnectBundle}`);


  const client = new Client({
    cloud: { secureConnectBundle: secureConnectBundle},
    credentials: { username: userId, password: passWord}
  });

  await client.connect();

  // Execute a query
  const rs = await client.execute('SELECT * FROM system.local');
  console.log(`Hello from cluster: ${rs.first()['cluster_name']}`);

  await client.shutdown();

}
const env = setEnvironmentVariables('../demo.env');
// Run the async function
run(env);
