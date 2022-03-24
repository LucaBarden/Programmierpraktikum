package de.propra.chicken.application.service;

import de.propra.chicken.application.service.repo.KlausurRepository;
import de.propra.chicken.domain.model.*;
import de.propra.chicken.application.service.repo.StudentRepository;
import de.propra.chicken.domain.service.StudentService;
import de.propra.chicken.domain.service.KlausurService;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.security.oauth2.core.user.OAuth2User;
import java.io.IOException;
import java.time.Clock;
import java.util.Map;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@org.springframework.stereotype.Service
public class Service {
    private final StudentRepository studentRepo;
    private final KlausurRepository klausurRepo;
    private final StudentService studentService;
    private final KlausurService klausurService;
    private final Clock clock;
    private static final Logger logger = Logger.getLogger("chicken.Service.Logger");

    private static final Dotenv dotenv = Dotenv.load();

    private static final String BEGINN     = dotenv.get("STARTUHRZEIT_PRAKTIKUM");
    private static final String ENDE       = dotenv.get("ENDUHRZEIT_PRAKTIKUM");
    private static final String STARTDATUM = dotenv.get("STARTDATUM_PRAKTIKUM");
    private static final String ENDDATUM   = dotenv.get("ENDDATUM_PRAKTIKUM");

    static {
        try {
            FileHandler fileHandler;
            fileHandler = new FileHandler("output.log", true);
            logger.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Service(StudentRepository studentRepo, KlausurRepository klausurRepo, StudentService studentService, KlausurService klausurService, Clock clock) {
        this.studentRepo = studentRepo;
        this.klausurRepo = klausurRepo;
        this.studentService = studentService;
        this.klausurService = klausurService;
        this.clock = clock;
    }

    private void klausurAnmelden(Klausur klausur, long githubID, OAuth2User principal) throws Exception {

        Student student = studentRepo.findByID(githubID);
        Set<Urlaub> gueltigeUrlaubeFuerTag = studentService.validiereKlausurAnmeldung(klausur, student, BEGINN, ENDE);
        student.aendereUrlaube(gueltigeUrlaubeFuerTag, klausur.getDatum());
        KlausurRef klausurRef = new KlausurRef(klausur.getLsfid());
        student.addKlausur(klausurRef);
        studentRepo.speicherStudent(student);
        logger.info(principal.getAttribute("login") + " hat sich zur Klausur " + klausur.getName()+"(" + klausur.getLsfid()+")" + " angemeldet");

    }

    public Set<Klausur> ladeAlleKlausuren() {
        Set<Klausur> alleKlausuren = klausurRepo.ladeAlleKlausuren();
        return klausurService.klausurIstNochImAnmeldezeitraum(alleKlausuren);
    }

    public Map<Klausur, Boolean> ladeAngemeldeteKlausuren(long githubID) {
        Set<KlausurRef> klausurenRef = studentRepo.getAngemeldeteKlausurenIds(githubID);
        Set<Klausur> klausuren = klausurRepo.getKlausurenByRefs(klausurenRef);

        return klausurService.stornierbareKlausuren(klausuren);
    }

    public Map<Urlaub, Boolean> ladeAngemeldeteUrlaube(long githubID) throws Exception {
        Student student = studentRepo.findByID(githubID);
        return studentService.stornierbareUrlaube(student.getUrlaube());
    }

    public void speicherStudent(Student student) {
        boolean alreadyExists = studentRepo.existsById(student.getGithubID());
        if(!alreadyExists){
            studentRepo.speicherStudent(student);
            logger.info("User " + student.getGithubID() + " angelegt");
        }
    }

    public void speicherUrlaub(Urlaub urlaub, long githubID, OAuth2User principal) throws Exception {

        Student student = studentRepo.findByID(githubID);
        Set<KlausurData> angemeldeteKlausuren = klausurRepo.findAngemeldeteKlausuren(githubID);
        Set<Urlaub> gueltigerNeuerUrlaub = studentService.validiereUrlaub(student, urlaub, angemeldeteKlausuren, BEGINN, ENDE, STARTDATUM, ENDDATUM);
        student.addUrlaube(gueltigerNeuerUrlaub);
        gueltigerNeuerUrlaub = student.ueberschneidendenUrlaubMergen();
        student.setUrlaube(gueltigerNeuerUrlaub);
        studentRepo.speicherStudent(student);
        logger.info(principal.getAttribute("login") + "(" + principal.getAttribute("id") + ") hat Urlaub eingetragen " + urlaub);

    }

    public void saveKlausur(Klausur klausur, OAuth2User principal) throws Exception {
        speicherKlausur(klausur);
        logger.info(principal.getAttribute("login") + "(" + principal.getAttribute("id") + ") " + "hat die Klausur " + klausur.getName()+"(" + klausur.getLsfid()+")" + " erstellt");
    }

    public void speicherKlausur(Klausur klausur) throws Exception {
            validiereKlausur(klausur);
            klausurRepo.speicherKlausur(klausur);
    }

    protected void validiereKlausur(Klausur klausur) throws Exception {
        klausurService.validiereLsfIdInternet(klausur);
        klausurService.validiereKlausur(klausur, BEGINN, ENDE, STARTDATUM, ENDDATUM);
    }

    public void klausurAnmeldung(long id, OAuth2User principal) throws Exception {
        Klausur klausur = klausurRepo.findeKlausurByID(id);
        klausurAnmelden(klausur, Long.parseLong(principal.getAttribute("id").toString()), principal);
    }

    public Student findStudentByGithubID(long id) throws Exception {
        return studentRepo.findByID(id);
    }

    public void storniereKlausurAnmeldung(long lsfID, OAuth2User principal) throws Exception {
        Klausur klausur = klausurRepo.findeKlausurByID(lsfID);
        Student student = studentRepo.findByID(Long.parseLong(principal.getAttribute("id").toString()));

        student.entferneUrlaubeAnEinemTag(klausur.getDatum());
        student.entferneKlausur(lsfID);
        studentRepo.speicherStudent(student);
        logger.info(principal.getAttribute("login").toString() + " hat die " + klausur + " und damit auch möglichen verbundenen Urlaub an dem Tag storniert");
    }

    public void urlaubStornieren(OAuth2User principal, String tag, String beginn, String end) throws Exception {
        Student student = studentRepo.findByID(Long.parseLong(principal.getAttribute("id").toString()));
        student.entferneUrlaub(tag, beginn, end);
        studentRepo.speicherStudent(student);
        logger.info(principal.getAttribute("login") + " hat seinen Urlaub am " + tag + " von " + beginn + " bis " + end + " Uhr storniert");
    }
}
