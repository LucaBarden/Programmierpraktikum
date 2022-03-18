CREATE TABLE IF NOT EXISTS
    klausur (
                  name varchar(100) not null,
                  lsf_id int not null unique,
                  praesenz boolean not null,
                  datum date not null,
                  beginn time not null,
                  end time not null,
                  primary key (lsf_id)
);

CREATE TABLE IF NOT EXISTS
    student (
                  github_id INT NOT NULL UNIQUE,
                  resturlaub INT NOT NULL,
                  PRIMARY KEY (github_id)
);


CREATE TABLE IF NOT EXISTS
    klausur_student (
                       klausur INT NOT NULL,
                       student INT NOT NULL,
                       PRIMARY KEY (klausur,student)
);


CREATE TABLE IF NOT EXISTS
    urlaub (
               id INT auto_increment,
               datum DATE NOT NULL,
               beginn TIME not null,
               schluss TIME not null,
               github_id INT not null,
               constraint urlaub
                   PRIMARY KEY (id),
                   foreign key (github_id) references student(github_id)

);
