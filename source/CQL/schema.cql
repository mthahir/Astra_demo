USE your_keyspace_name;

CREATE TABLE IF NOT EXISTS storm_data (
    fips_state text,
    fips_st_code int,
    event_year int,
    event_month text,
    event_type text,
    injuries_direct int,
    injuries_indirect int,
    death_direct int,
    death_indirect int,
    event_source text,
    episode_narrative text,
    event_id uuid,
    added_on timestamp,
  	PRIMARY KEY ((fips_st_code),event_year, event_type, event_id))
;
CREATE TABLE IF NOT EXISTS acs_data (
    fips_st_code int,
    fips_state text,
    pop_est int,
    families int,
    child_family int,
    children int,
    child_family_avg double,
    child_avg double ,
    housingunits int,
    medianincome int,
    PRIMARY KEY (fips_st_code)
)
;
CREATE TABLE IF NOT EXISTS incidents (
    fips_st_code int,
    fips_state text,
    incident_year int,
    incidents  int,
    deaths     int,
    injuries   int,
    acs_data_housingunits int,
    acs_data_median_income int,
    acs_data_pop_est int,
    acs_data_families int,
    acs_avg_child_per_family double,
    acs_data_children int,
    PRIMARY KEY (fips_st_code, incident_year)
    )
    WITH CLUSTERING ORDER BY (incident_year ASC)
;
CREATE TABLE IF NOT EXISTS policy_info (
  fips_state text,
  fips_county text,
  community text,
  policies int,
  total_coverage double,
  total_premium double,
  added_on timestamp,
  data_source text,
  PRIMARY KEY ((fips_state), fips_county, community))
;

CREATE TABLE IF NOT EXISTS policy_losses(
  fips_st_code int,
  fips_state text,
  policy_loss_count int,
  closed_with_payment_losses int,
  open_losses int,
  closed_without_payment_losses int,
  payments  double,
  policies_count int,
  total_premium double,
  total_coverage double,
  added_on timestamp,
  updated_on timeStamp,
  PRIMARY KEY (fips_st_code))
;

CREATE TABLE IF NOT EXISTS census_data(
    fips_st_code int,
    fips_state text,
    popEst int,
    pop2010 int,
    pop2011 int,
    pp2012 int,
    pop2013 int,
    pop204 int,
    pop2015 int,
    pop2016 int,
    pop2017 int,
    pop2018 int,
    pop2019 int,
    PRIMARY KEY(fips_st_code)
    )
;
