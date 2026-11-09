# Upgrade scripts

`initdb.d` builds a new database. An existing database gets the same schema change from the
script here that is named for the release that needs it, run as `SIM_OWNER` (or with the
`SIM_OWNER.` prefix kept) after the release's war is deployed. Each script is written to run once.
