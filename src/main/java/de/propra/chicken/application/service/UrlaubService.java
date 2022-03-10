package de.propra.chicken.application.service;

import de.propra.chicken.application.service.repository.UrlaubRepository;
import de.propra.chicken.domain.model.Student;
import de.propra.chicken.domain.model.Urlaub;
import de.propra.chicken.domain.service.UrlaubDomainService;

import java.util.List;

public class UrlaubService {


    private UrlaubRepository urlaubRepository;

    public UrlaubService(UrlaubRepository urlaubRepository) {
        this.urlaubRepository = urlaubRepository;
    }

    public void save(Urlaub urlaub) {
        validiereUrlaub(urlaub);
        urlaubRepository.save(urlaub);
    }


    private void validiereUrlaub(Urlaub urlaub) {
        try {
            UrlaubDomainService.validiereUrlaub(urlaub);
        } catch (Exception e) {
            //TODO behandleException urlaub invalide
        }
    }

    public List<Urlaub> findUrlaube(Student student) {

        return urlaubRepository.findAllByID(student);
    }
}

