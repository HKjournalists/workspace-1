CREATE TABLE comment (
  id bigint(18) NOT NULL AUTO_INCREMENT,
  username varchar(512) NOT NULL,
  email varchar(512) NOT NULL,
  content varchar(512) NOT NULL,
  articleid bigint(18) NOT NULL,
  replyTime timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
)