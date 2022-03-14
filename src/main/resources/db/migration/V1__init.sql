CREATE TABLE IF NOT EXISTS
    klausuren (
                  name varchar(100) not null,
                  lsfID int not null unique,
                  datum date not null,
                  beginn time not null,
                  schluss time not null,
                  isPraesenz boolean not null,
                  primary key (lsfID)
);

CREATE TABLE IF NOT EXISTS
    studenten (
                  githubID INT NOT NULL UNIQUE,
                  resturlaub INT NOT NULL,
                  PRIMARY KEY (githubID)
);


CREATE TABLE IF NOT EXISTS
    klausurStudent (
                       lsfID INT NOT NULL,
                       githubID INT NOT NULL,
                       PRIMARY KEY (lsfID,githubID)
);


CREATE TABLE IF NOT EXISTS
    urlaub (
               id INT NOT NULL auto_increment,
               datum DATE NOT NULL,
               beginn TIME not null,
               schluss TIME not null,
               githubID INT not null,
               constraint urlaub
                   PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS
    auditLog (
                 id INT unsigned NOT NULL AUTO_INCREMENT,
                 githubID INT NOT NULL,
                 timestamp DATETIME NOT NULL,
                 constraint auditLog
                     PRIMARY KEY (id)
);