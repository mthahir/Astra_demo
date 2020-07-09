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

acsCQL = "SELECT fips_st_code, fips_state, housingunits, medianincome, \
                 pop_est, families, child_avg, children \
            FROM "+keySpace+"."+"acs_data \
           WHERE fips_st_code > 0 \
           ALLOW FILTERING \
         "
strmCQL = "SELECT fips_st_code, event_year, \
                  COUNT(*) as incidents, \
                  SUM(death_direct) + SUM(death_indirect) as deaths, \
                  SUM(injuries_direct) + SUM(injuries_indirect) as injuries \
             FROM "+keySpace+"."+"storm_data \
            WHERE fips_st_code = ? \
            GROUP BY fips_st_code, event_year \
            ORDER by event_year"
incidentCQL = "INSERT INTO "+keySpace+"."+"incidents \
                    (fips_st_code, fips_state, incident_year, incidents, deaths, injuries, \
                     acs_data_housingunits, acs_data_median_income, acs_data_pop_est, \
                     acs_data_families,acs_avg_child_per_family, acs_data_children) \
                 VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)"

rows_acs = session.execute(acsCQL)
for acs in rows_acs:
    acsPreparedCQL = session.prepare(strmCQL)
    rows_usgs = session.execute(acsPreparedCQL, [acs.fips_st_code])
    for usgs in rows_usgs:
        session.execute(incidentCQL,(acs.fips_st_code, acs.fips_state, usgs.event_year, usgs.incidents, usgs.deaths, usgs.injuries,acs.housingunits,acs.medianincome, acs.pop_est, acs.families, acs.child_avg, acs.children))
        print (acs.fips_st_code, acs.fips_state, usgs.event_year)
