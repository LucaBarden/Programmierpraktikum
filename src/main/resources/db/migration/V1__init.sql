CREATE TABLE IF NOT EXISTS
    klausur (
                name varchar(100) not null,
                lsf_id integer not null unique,
                praesenz boolean not null,
                datum date not null,
                beginn time not null,
                end time not null,
                primary key (lsf_id)
);

CREATE TABLE IF NOT EXISTS
    student (
                student_id integer primary key,
                resturlaub integer NOT NULL
);

CREATE TABLE IF NOT EXISTS
    urlaub (

               id integer primary key auto_increment,
               student_dto integer references student(student_id),
               tag DATE NOT NULL,
               beginn TIME not null,
               end TIME not null
);

CREATE TABLE IF NOT EXISTS
    klausur_student (
                        klausur integer not null,
                        student_dto integer not null ,
                        PRIMARY KEY (klausur,student_dto)
);

