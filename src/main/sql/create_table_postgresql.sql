CREATE TABLE without_index (
  id INTEGER PRIMARY KEY,
  flag smallint NOT NULL,
  data1 char(64),
  data2 char(64),
  data3 char(64),
  data4 char(64),
  created timestamp,
  updated timestamp
);

ALTER TABLE without_index OWNER TO user1;


CREATE TABLE with_index_flag (
  id INTEGER PRIMARY KEY,
  flag smallint NOT NULL,
  data1 char(64),
  data2 char(64),
  data3 char(64),
  data4 char(64),
  created timestamp,
  updated timestamp
);

ALTER TABLE with_index_flag OWNER TO user1;

CREATE INDEX with_index_flag_flag_index ON with_index_flag(flag);


CREATE TABLE with_index_flag_id (
  id INTEGER PRIMARY KEY,
  flag smallint NOT NULL,
  data1 char(64),
  data2 char(64),
  data3 char(64),
  data4 char(64),
  created timestamp,
  updated timestamp
);

ALTER TABLE with_index_flag_id OWNER TO user1;

CREATE INDEX with_index_flag_id_flag_id_index ON with_index_flag_id(flag, id);
