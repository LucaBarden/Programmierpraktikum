package de.propra.chicken.application.service;

import de.propra.chicken.application.service.repo.KlausurRepository;
import de.propra.chicken.domain.model.*;
import de.propra.chicken.application.service.repo.StudentRepository;
import de.propra.chicken.domain.service.StudentService;
import de.propra.chicken.domain.service.KlausurService;
import org.jsoup.Jsoup;
import org.springframework.security.oauth2.core.user.OAuth2User;
import java.io.IOException;
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
    private static final Logger logger = Logger.getLogger("de.propra.chicken.application.service.Service");


    private static String BEGINN     = System.getenv("STARTUHRZEIT_PRAKTIKUM");
    private static String ENDE       = System.getenv("ENDUHRZEIT_PRAKTIKUM");
    private static String STARTDATUM = System.getenv("STARTDATUM_PRAKTIKUM");
    private static String ENDDATUM   = System.getenv("ENDDATUM_PRAKTIKUM");



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

    public Service(StudentRepository studentRepo, KlausurRepository klausurRepo, StudentService studentService, KlausurService klausurService) {
        this.studentRepo = studentRepo;
        this.klausurRepo = klausurRepo;
        this.studentService = studentService;
        this.klausurService = klausurService;


    }

    private void klausurAnmelden(Klausur klausur, long githubID, OAuth2User principal) throws Exception {
        //student.setKlausuren(studentRepo.getKlausurenVonStudent(student));
        Student student = studentRepo.findByID(githubID);
        try {
            Set<Urlaub> gueltigeUrlaubeFuerTag = studentService.validiereKlausurAnmeldung(klausur, student, BEGINN, ENDE);
            student.zieheUrlaubsdauerAb(gueltigeUrlaubeFuerTag);
            student.aendereUrlaube(gueltigeUrlaubeFuerTag, klausur.getDatum());
            KlausurRef klausurRef = new KlausurRef(klausur.getLsfid());
            student.addKlausur(klausurRef);
            studentRepo.speicherStudent(student);
            logger.info(principal.getAttribute("login") + " hat sich zur Klausur " + klausur.getName()+"(" + klausur.getLsfid()+")" + " angemeldet");
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void saveKlausur(Klausur klausur, OAuth2User principal) throws Exception {
        speicherKlausur(klausur);
        logger.info(principal.getAttribute("login") + "(" + principal.getAttribute("id") + ") " + "hat die Klausur " + klausur.getName()+"(" + klausur.getLsfid()+")" + " erstellt");
    }

    private void validiereKlausur(Klausur klausur) throws Exception {
        validiereLsfIdInternet(klausur);
        klausurService.validiereKlausur(klausur);
    }

    private void validiereLsfIdInternet(Klausur klausur) throws Exception {
        String webContent = Jsoup.connect(String.format("https://lsf.hhu.de/qisserver/rds?state=verpublish&status=init&vmfile=no&publishid=%s&moduleCall=webInfo" +
                "&publishConfFile=webInfo&publishSubDir=veranstaltung", klausur.getLsfid())).get().text();
        if (!(webContent.contains("VeranstaltungsID"))) {
            throw new IllegalArgumentException("Invalide LSF ID");
        }
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


    public void speicherStudent(Student student) {
        boolean b = studentRepo.existsById(student.getGithubID());
        if(!b){
            studentRepo.speicherStudent(student);
            logger.info("User " + String.valueOf(student.getGithubID()) + " angelegt");
        }
    }

    public void speicherUrlaub(Urlaub urlaub, long githubID, OAuth2User principal) throws Exception {
        try {
            Student student = studentRepo.findByID(githubID);
            Set<KlausurData> angemeldeteKlausuren = studentRepo.findAngemeldeteKlausuren(githubID);
            Set<Urlaub> gueltigerNeuerUrlaub = studentService.validiereUrlaub(student, urlaub, angemeldeteKlausuren, BEGINN, ENDE);
            student.addUrlaube(gueltigerNeuerUrlaub);
            student.zieheUrlaubsdauerAb(gueltigerNeuerUrlaub);
            gueltigerNeuerUrlaub = student.ueberschneidendenUrlaubMergen();
            student.setUrlaube(gueltigerNeuerUrlaub);
            studentRepo.speicherStudent(student);
            logger.info(principal.getAttribute("login") + "(" + principal.getAttribute("id") + ") hat Urlaub eingetragen");
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void speicherKlausur(Klausur klausur) throws Exception {
        try {
            validiereKlausur(klausur);
            klausurRepo.speicherKlausur(klausur);
        } catch (Exception ex) {
            throw ex;
        }

    }
    public void klausurAnmeldung(long id, OAuth2User principal) throws Exception {
        Klausur klausur = klausurRepo.findeKlausurByID(id);
        klausurAnmelden(klausur, Long.parseLong(principal.getAttribute("id").toString()), principal);
    }
    public Student findStudentByGithubID(long id) throws Exception {
        return studentRepo.findByID(id);
    }

    public void storniereKlausurAnmeldung(long id, OAuth2User principal) {
        Klausur klausur = klausurRepo.findeKlausurByID(id);
        logger.info(principal.getAttribute("login").toString() + " hat die Klausur " + klausur + " und damit auch möglichen verbundenen Urlaub an dem Tag storniert");
    }
}
