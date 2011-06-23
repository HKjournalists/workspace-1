CREATE TABLE user (
  id bigint(18) NOT NULL AUTO_INCREMENT,
  username varchar(512) NOT NULL,
  password varchar(512) NOT NULL,
  isAdmin bigint(18) NOT NULL,
  create_time timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
)