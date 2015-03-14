CREATE TABLE without_index (
  id INTEGER PRIMARY KEY NOT NULL,
  flag smallint NOT NULL,
  data1 char(64),
  data2 char(64),
  data3 char(64),
  data4 char(64),
  created timestamp,
  updated timestamp
);


CREATE TABLE with_index_flag (
  id INTEGER PRIMARY KEY NOT NULL,
  flag smallint NOT NULL,
  data1 char(64),
  data2 char(64),
  data3 char(64),
  data4 char(64),
  created timestamp,
  updated timestamp
);

CREATE INDEX with_index_flag_flag_index ON with_index_flag(flag);


CREATE TABLE with_index_flag_id (
  id INTEGER PRIMARY KEY NOT NULL,
  flag smallint NOT NULL,
  data1 char(64),
  data2 char(64),
  data3 char(64),
  data4 char(64),
  created timestamp,
  updated timestamp
);

CREATE INDEX with_index_flag_id_flag_id_index ON with_index_flag_id(flag, id);
