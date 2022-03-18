CREATE TABLE IF NOT EXISTS
    klausur (
                  name varchar(100) not null,
                  lsfID int not null unique,
                  praesenz boolean not null,
                  datum date not null,
                  beginn time not null,
                  end time not null,
                  primary key (lsfID)
);

CREATE TABLE IF NOT EXISTS
    student (
                  githubID INT NOT NULL UNIQUE,
                  resturlaub INT NOT NULL,
                  PRIMARY KEY (githubID)
);


CREATE TABLE IF NOT EXISTS
    klausur_student (
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
