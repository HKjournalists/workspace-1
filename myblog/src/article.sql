CREATE TABLE article (
  id bigint(18) NOT NULL AUTO_INCREMENT,
  title varchar(512) NOT NULL,
  content varchar(512) NOT NULL,
  author varchar(512) NOT NULL,
  subject varchar(512) NOT NULL,
  create_time timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
)