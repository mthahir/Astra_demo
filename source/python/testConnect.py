from cassandra.cluster import Cluster
from cassandra.auth import PlainTextAuthProvider

with open('../demo.env') as f:
  d = dict(x.rstrip().split(':', 1) for x in f)
  secure_connect_bundle = d.get('secure_connect_bundle')
  userId = d.get('userId')
  passWord = d.get('passWord')
cloud_config= {
        'secure_connect_bundle': secure_connect_bundle
}
auth_provider = PlainTextAuthProvider(userId, passWord)
cluster = Cluster(cloud=cloud_config, auth_provider=auth_provider)
session = cluster.connect()

row = session.execute("select release_version from system.local").one()
if row:
    print(row[0])
else:
    print("An error occurred.")
