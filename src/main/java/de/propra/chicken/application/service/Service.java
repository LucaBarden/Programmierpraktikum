package de.propra.chicken.application.service;

import de.propra.chicken.application.service.repo.KlausurRepository;
import de.propra.chicken.domain.model.*;
import de.propra.chicken.application.service.repo.StudentRepository;
import de.propra.chicken.domain.service.StudentService;
import de.propra.chicken.domain.service.KlausurService;
import de.propra.chicken.dto.StudentDTO;
import org.jsoup.Jsoup;

import java.util.Map;
import java.util.Set;

@org.springframework.stereotype.Service
public class Service {
    //TODO Log4J
    private final StudentRepository studentRepo;
    private final KlausurRepository klausurRepo;
    private final StudentService studentService;
    private final KlausurService klausurService;

    public Service(StudentRepository studentRepo, KlausurRepository klausurRepo, StudentService studentService, KlausurService klausurService) {
        this.studentRepo = studentRepo;
        this.klausurRepo = klausurRepo;
        this.studentService = studentService;
        this.klausurService = klausurService;
    }

    public void klausurAnmelden(Klausur klausur, long githubID) throws Exception {
        //student.setKlausuren(studentRepo.getKlausurenVonStudent(student));
        Student student = studentRepo.findByID(githubID);
        try {
            Set<KlausurRef> angemeldeteKlausurenRefs = studentRepo.getAngemeldeteKlausurenIds(student.getGithubID());
            Set<KlausurData> angemeldeteKlausuren = klausurRepo.getKlausurenDataByRefs(angemeldeteKlausurenRefs);
            Set<Urlaub> zuErstattendeUrlaube = studentService.validiereKlausurAnmeldung(new KlausurRef(klausur.getLsfid()), new KlausurData(klausur.getDatum(), klausur.getBeginn(), klausur.getEnd(), klausur.isPraesenz()),
                    angemeldeteKlausuren, student.getUrlaube(), angemeldeteKlausurenRefs);
            student = studentService.erstatteUrlaube(zuErstattendeUrlaube);
            KlausurRef klausurRef = new KlausurRef(klausur.getLsfid());
            student.addKlausur(klausurRef);
            studentRepo.speicherKlausurAnmeldung(student);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void saveKlausur(Klausur klausur) throws Exception {
        klausurRepo.speicherKlausur(klausur);
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
        Student studentTmp = studentRepo.findByID(student.getGithubID());
        if (studentTmp == null) {
            studentRepo.speicherStudent(student);
        }
    }

    public void speicherUrlaub(Urlaub urlaub, long githubID) throws Exception {
        try {
            //rausfinden ob wir Studenten aus der DB bekommen dürfen oder DTOS zu Studenten machen müssen
            Student student = studentRepo.findByID(githubID);
            Set<KlausurData> angemeldeteKlausuren = studentRepo.findAngemeldeteKlausuren(githubID);
            Set<Urlaub> gueltigerUrlaub = studentService.validiereUrlaub(student, urlaub, angemeldeteKlausuren);

            student.addUrlaube(gueltigerUrlaub);
            studentRepo.speicherStudent(student);
        } catch (Exception ex) {
            throw ex;
        }
    }


}
