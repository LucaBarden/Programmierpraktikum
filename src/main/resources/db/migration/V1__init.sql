CREATE TABLE IF NOT EXISTS
    klausur (
                id integer auto_increment,
                name varchar(100) not null,
                lsf_id integer not null unique,
                praesenz boolean not null,
                datum date not null,
                beginn time not null,
                end time not null,
                primary key (id)
);



CREATE TABLE IF NOT EXISTS
    student (
                github_id integer NOT NULL,
                resturlaub integer NOT NULL,
                primary key (github_id)

);
CREATE TABLE IF NOT EXISTS
    urlaub (

               id integer auto_increment,
               student integer,
               tag DATE NOT NULL,
               beginn TIME not null,
               end TIME not null,
               primary key(id),
               constraint urlaub_fk foreign key (student) references student(github_id)

);


CREATE TABLE IF NOT EXISTS
    klausur_student (
                        klausur INT NOT NULL,
                        student INT NOT NULL,
                        PRIMARY KEY (klausur,student)
);
Show Engine innodb status

