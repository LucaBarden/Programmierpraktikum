CREATE TABLE `klausuren` (
    `name` TEXT NOT NULL,
    `lsfID` INT(16) unsigned NOT NULL UNIQUE,
    `datum` DATE NOT NULL,
    `beginn` TIME NOT NULL,
    `schluss` TIME NOT NULL,
    `isPraesenz` BOOLEAN NOT NULL,
    PRIMARY KEY (`lsfID`)
    );

CREATE TABLE `studenten` (
    `githubID` INT(16) unsigned NOT NULL UNIQUE,
    `resturlaub` INT(4) NOT NULL,
    PRIMARY KEY (`githubID`)
    );


CREATE TABLE `KlausurStudent` (
    `lsfID` INT(16) unsigned NOT NULL,
    `githubID` INT(16) unsigned NOT NULL,
    PRIMARY KEY (`lsfID`,`githubID`)
    );


CREATE TABLE `urlaub` (
                          `datum` DATE NOT NULL,
                          `beginn` TIME,
                          `schluss` TIME,
                          `githubID` INT(16) unsigned,
    `id` INT unsigned NOT NULL AUTO_INCREMENT
                            PRIMARY KEY (`id`)
    );

CREATE TABLE `auditLog` (
                            `id` INT unsigned NOT NULL AUTO_INCREMENT,
                            `githubID` INT NOT NULL,
                            `timestamp` DATETIME NOT NULL,
                            PRIMARY KEY (`id`)
    );