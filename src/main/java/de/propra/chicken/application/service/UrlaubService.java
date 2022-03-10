package de.propra.chicken.application.service;

import de.propra.chicken.application.service.repository.UrlaubRepository;
import de.propra.chicken.domain.model.Urlaub;
import de.propra.chicken.domain.service.UrlaubDomainService;
import org.springframework.beans.factory.annotation.Autowired;

public class UrlaubService {

    @Autowired
    UrlaubRepository urlaubRepository;

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
}

