package de.propra.chicken.application.service;

import ch.qos.logback.classic.BasicConfigurator;
import de.propra.chicken.application.service.repo.KlausurRepository;
import de.propra.chicken.domain.model.*;
import de.propra.chicken.application.service.repo.StudentRepository;
import de.propra.chicken.domain.service.StudentService;
import de.propra.chicken.domain.service.KlausurService;
import org.jsoup.Jsoup;
import org.springframework.security.oauth2.core.user.OAuth2User;
import java.io.IOException;
import java.util.HashSet;
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

    Logger logger = Logger.getLogger("de.propra.chicken.application.service.Service");
    FileHandler fileHandler;


    public Service(StudentRepository studentRepo, KlausurRepository klausurRepo, StudentService studentService, KlausurService klausurService) {
        this.studentRepo = studentRepo;
        this.klausurRepo = klausurRepo;
        this.studentService = studentService;
        this.klausurService = klausurService;

        try {
            fileHandler = new FileHandler("./output.log");
            logger.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void klausurAnmelden(Klausur klausur, long githubID, OAuth2User principal) throws Exception {
        //student.setKlausuren(studentRepo.getKlausurenVonStudent(student));
        Student student = studentRepo.findByID(githubID);
        try {
            Set<KlausurRef> angemeldeteKlausurenRefs = studentRepo.getAngemeldeteKlausurenIds(student.getGithubID());
            Set<KlausurData> angemeldeteKlausuren = klausurRepo.getKlausurenDataByRefs(angemeldeteKlausurenRefs);
            Set<Urlaub> zuErstattendeUrlaube = studentService.validiereKlausurAnmeldung(new KlausurRef(klausur.getLsfid()), new KlausurData(klausur.getDatum(), klausur.getBeginn(), klausur.getEnd(), klausur.isPraesenz()),
                    angemeldeteKlausuren, student, angemeldeteKlausurenRefs);
            //student = studentService.erstatteUrlaube(zuErstattendeUrlaube);
            KlausurRef klausurRef = new KlausurRef(klausur.getLsfid());
            student.addKlausur(klausurRef);
            studentRepo.speicherKlausurAnmeldung(student);
            logger.info(principal.getAttribute("login") + "hat sich zur Klausur " + klausur.getName()+"(" + klausur.getLsfid()+")" + " angemeldet");
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void saveKlausur(Klausur klausur, OAuth2User principal) throws Exception {
        speicherKlausur(klausur);
        logger.info(principal.getAttribute("login") + "(" + principal.getAttribute("id") + ") " + "hat die Klausur " + klausur.getName()+"(" + klausur.getLsfid()+")" + " erstellt");
    }

    public void speicherKlausur(Klausur klausur) throws Exception {
        try {
            validiereKlausur(klausur);
            klausurRepo.speicherKlausur(klausur);
        } catch (Exception ex) {
            throw ex;
        }

    }

    private void validiereKlausur(Klausur klausur) throws Exception {
        validiereLsfIdInternet(klausur);
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
        return klausurService.validiereAlleKlausuren(alleKlausuren);
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
            Set<Urlaub> gueltigerUrlaub = studentService.validiereUrlaub(student, urlaub, angemeldeteKlausuren);
            gueltigerUrlaub = ueberschneidendenUrlaubMergen(gueltigerUrlaub);

            student.addUrlaube(gueltigerUrlaub);
            studentRepo.speicherStudent(student);
            logger.info(principal.getAttribute("login") + "(" + principal.getAttribute("id") + ") hat Urlaub eingetragen");
        } catch (Exception ex) {
            throw ex;
        }
    }

    public Set<Urlaub> ueberschneidendenUrlaubMergen(Set<Urlaub> urlaube) {
        if(urlaube.size() == 0) return urlaube;

        Set<Urlaub> zuPruefendeUrlaube = new HashSet<>();
        zuPruefendeUrlaube.addAll(urlaube);
        Set<Urlaub> stashUrlaube = new HashSet<>();
        boolean aenderung = true;

        while(aenderung) {
            aenderung = false;
            for (Urlaub neu : zuPruefendeUrlaube) {
                if(aenderung) break;
                stashUrlaube.addAll(zuPruefendeUrlaube);
                for (Urlaub alt : zuPruefendeUrlaube) {
                    if (alt.getBeginn().equals(neu.getBeginn())) {
                        //if (alt.getEnd().equals(neu.getEnd())) {
                            //1: beide Urlaube sind gleich, einer wird übernommen, wegen Set gibt es sowieso keine Duplikationen
                        //} else
                        if (alt.getEnd().isAfter(neu.getEnd())) {
                            //2: der alte Urlaub wird übernommen
                            stashUrlaube.remove(neu);
                            aenderung = true;
                        } else if (alt.getEnd().isBefore(neu.getEnd())) {
                            //3: der neue Urlaub wird übernommen
                            stashUrlaube.remove(alt);
                            aenderung = true;
                        }
                    } else if (alt.getBeginn().isAfter(neu.getBeginn()) && (alt.getBeginn().isBefore(neu.getEnd()) || alt.getBeginn().equals(neu.getEnd()))) {
                        if (alt.getEnd().equals(neu.getEnd())) {
                            //4: der neue Urlaub wird übernommen
                            stashUrlaube.remove(alt);
                            aenderung = true;
                        } else if (alt.getEnd().isAfter(neu.getEnd())) {
                            //5: Start vom neuen und Ende vom alten Urlaub
                            stashUrlaube.remove(alt);
                            stashUrlaube.remove(neu);
                            stashUrlaube.add(new Urlaub(neu.getTag().toString(), neu.getBeginn().toString(), alt.getEnd().toString()));
                            aenderung = true;
                            break;
                        } else if (alt.getEnd().isBefore(neu.getEnd())) {
                            //6: der neue Urlaub wird übernommen
                            stashUrlaube.remove(alt);
                            aenderung = true;
                        }
                    } else if (alt.getBeginn().isBefore(neu.getBeginn()) && (neu.getBeginn().isBefore(alt.getEnd()) || neu.getBeginn().equals(alt.getEnd()))) {
                        if (alt.getEnd().equals(neu.getEnd())) {
                            //7: der alte Urlaub wird übernommen
                            stashUrlaube.remove(neu);
                            aenderung = true;
                        } else if (alt.getEnd().isAfter(neu.getEnd())) {
                            //8: der alte Urlaub wird übernommen
                            stashUrlaube.remove(neu);
                            aenderung = true;
                        } else if (alt.getEnd().isBefore(neu.getEnd())) {
                            //9: Start vom alten und Ende vom neuen Urlaub
                            stashUrlaube.remove(alt);
                            stashUrlaube.remove(neu);
                            stashUrlaube.add(new Urlaub(neu.getTag().toString(), alt.getBeginn().toString(), neu.getEnd().toString()));
                            aenderung = true;
                            break;
                        }
                    }
                    //else: die alten und der neue Urlaub überschneiden sich nicht: beide sind gültig, nichts tun
                }
            }
            zuPruefendeUrlaube = stashUrlaube;
            stashUrlaube = new HashSet<>();
        }
        return zuPruefendeUrlaube;
    }

    public void test(Student student) {
        studentRepo.speicherStudent(student);
    }
}
