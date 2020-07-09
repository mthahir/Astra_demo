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
  const keySpace = env['keyspace']

  const client = new Client({
    cloud: { secureConnectBundle: secureConnectBundle },
    credentials: { username: userId, password: passWord }
  });

  await client.connect();

  // Execute a query
  const drvrQry = 'SELECT fips_st_code, fips_state from galaxy.policy_losses';
  const policyQry = 'SELECT fips_state, SUM(policies) AS policies_count,' +
                    '       SUM(total_premium) AS tot_premium, '+
                    '        SUM(total_coverage) AS tot_coverage '+
                    '  FROM '+keySpace+'.'+'policy_info ' +
                    ' WHERE fips_state = ?';

  const updateQry = 'UPDATE '+keySpace+'.'+'policy_losses '+
                    '   SET policies_count = ?,'+
                    '       total_premium = ?,'+
                    '       total_coverage = ?,'+
                    '       updated_on = currentTimestamp()'+
                    '  WHERE fips_st_code = ?';

  try {
    const rs1 = await client.execute(drvrQry);

    var policies_count, tot_premium, tot_coverage;

    for (let plRow of rs1){
      const rs2 = await client.execute(policyQry, [plRow.fips_state]);
      policies_count = 0;
      tot_premium = 0;
      tot_coverage = 0;
      for (let pRow of rs2){
        policies_count = pRow.policies_count;
        tot_premium = pRow.tot_premium;
        tot_coverage = pRow.tot_coverage;
      }

    const params = [policies_count, tot_premium, tot_coverage, plRow.fips_st_code];
    await client.execute(updateQry, params, { prepare: true })
      .then(result => console.log('state: %s, %s', plRow.fips_st_code, plRow.fips_state));
    }
  }
  catch(error) {
    console.error(error);
  }
  await client.shutdown();
}

// Run the async function
const env = setEnvironmentVariables('../demo.env');
// Run the async function
run(env);
