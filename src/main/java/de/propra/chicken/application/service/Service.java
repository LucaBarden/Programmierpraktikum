package de.propra.chicken.application.service;

import de.propra.chicken.application.service.repo.IRepository;
import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.Student;
import de.propra.chicken.domain.model.Urlaub;
import de.propra.chicken.domain.service.Validierung;
import org.jsoup.Jsoup;
import java.util.Map;
import java.util.Set;

public class Service {

    private final IRepository repo;
    private Student student;

    public Service(IRepository repo) {
        this.repo = repo;
    }


    public void klausurAnmelden(Klausur klausur) throws Exception {
        student.setKlausuren(repo.getKlausurenVonStudent(student));
        student.setUrlaube(repo.getUrlaubeVonStudent(student));
        try {
            student.validiereKlausurAnmeldung(klausur);
            //TODO Je nach validierung Klausur zu Student hinzufügen oder nicht
            student = repo.speicherKlausurAnmeldung(student);
        }
        catch(Exception ex) {
            throw ex;
        }
    }

    public void speicherKlausur(Klausur klausur) throws Exception {
        try {
            validiereKlausur(klausur);
            repo.speicherKlausur(klausur);
        } catch (Exception ex) {
            throw ex;
        }

    }

    private void validiereKlausur(Klausur klausur) throws Exception {
        //TODO Auf Namen der Klausur prüfen
        if(!repo.validiereLsfIdCache(klausur)) {
            try {
                validiereLsfIdInternet(klausur);
            } catch(Exception ex) {
                throw ex;
            }
        }
    }

    private void validiereLsfIdInternet(Klausur klausur) throws Exception {
        String webContent = Jsoup.connect(String.format("https://lsf.hhu.de/qisserver/rds?state=verpublish" +
                "&status=init&vmfile=no&publishid=%d&moduleCall=webInfo&publishConfFile=webInfo&publishSubDir=veranstaltung", klausur.getLsfid())).get().text();
        if(!(webContent.contains("VeranstaltungsID"))){
            throw new IllegalArgumentException("Invalide LSF ID");
        }
    }

    public Set<Klausur> ladeAlleKlausuren() {
        Set<Klausur> alleKlausuren = repo.ladeAlleKlausuren();
        return Validierung.validiereAlleKlausuren(alleKlausuren);
    }

    public Map<Klausur, Boolean> ladeAngemeldeteKlausuren() {
        Set<Klausur> klausuren = repo.findAngemeldeteKlausuren(student);
        return Validierung.stornierbareKlausuren(klausuren);
    }


    public void speicherStudent() {
        //TODO validiere Student
        student = repo.speicherStudent(student);
    }

    public void speicherUrlaub(Urlaub urlaub) {
        student.validiereUrlaub(urlaub);
        //TODO Je nach Validierung Urlaub zu student hinzufügen oder nicht
        student = repo.speicherStudent(student);
    }

    //TODO Set Student bei Anmeldung
    public void setStudent(long githubID) {
        //TODO Student aus der DB laden
        this.student = new Student(githubID);
    }
}