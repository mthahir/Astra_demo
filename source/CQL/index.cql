use your_keyspace_name;
CREATE INDEX IF NOT EXISTS ON galaxy.incidents (fips_state);
CREATE INDEX IF NOT EXISTS ON galaxy.policy_losses (fips_state);
