import time
from cassandra.cluster import Cluster
from cassandra.auth import PlainTextAuthProvider

with open('../demo.env') as f:
  d = dict(x.rstrip().split(':', 1) for x in f)
  secure_connect_bundle = d.get('secure_connect_bundle')
  userId = d.get('userId')
  passWord = d.get('passWord')
  keySpace = d.get('keyspace')


cloud_config= {
        'secure_connect_bundle': secure_connect_bundle
}
auth_provider = PlainTextAuthProvider(userId, passWord)
cluster = Cluster(cloud=cloud_config, auth_provider=auth_provider)
session = cluster.connect()

tblCQL = "SELECT keyspace_name, table_name, writetime(id) AS created_on \
            FROM system_schema.tables \
          WHERE keyspace_name = "+ "'"+keySpace+"'"

rows_tbl = session.execute(tblCQL)
for tbl in rows_tbl:
    cntCQL = 'SELECT count(*) AS rowCount \
               FROM ' +tbl.keyspace_name+'.'+ tbl.table_name
    rows_cnt = session.execute(cntCQL)
    for cnt in rows_cnt:
        print ('table:', tbl.table_name, '\tcreated on:',
        time.strftime('%Y-%m-%d %H:%M:%S',time.gmtime(tbl.created_on/1000000.0)), '\tcount:', cnt.rowcount)
