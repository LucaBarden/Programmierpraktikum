package de.propra.chicken.db;

import com.sun.jdi.request.DuplicateRequestException;
import de.propra.chicken.application.service.repo.KlausurRepository;
import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.KlausurData;
import de.propra.chicken.domain.model.KlausurRef;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Repository
public class KlausurRepositoryImpl implements KlausurRepository {


    private CRUDKlausur crudKlausur;

    public KlausurRepositoryImpl(CRUDKlausur crudKlausur) {
        this.crudKlausur = crudKlausur;
    }



    //Works
    @Override
    public Klausur speicherKlausur(Klausur klausur) throws Exception {
        KlausurDTO dto = transferKlausurToDTO(klausur);
        if(!crudKlausur.existsById(dto.getLsfID())) {
            return transferDTOToKlausur(crudKlausur.save(dto));
        }
        throw new Exception("Eine Klausur mit dieser ID existiert bereits");
        //dto.setIsNew(false);

    }


    //Works
    @Override
    public Set<Klausur> ladeAlleKlausuren() {
        Set<KlausurDTO> all = crudKlausur.findAll();
        Set<Klausur> klausuren = new HashSet<>();
        for(KlausurDTO curr : all) {
            klausuren.add(transferDTOToKlausur(curr));
        }
        return klausuren;
    }

    //Works
    @Override
    public Set<Klausur> getKlausurenByRefs(Set<KlausurRef> klausurenRef) {
        Set<KlausurDTO> DTOs = findeKlausurenByID(klausurenRef);
        return DTOs.stream().map(this::transferDTOToKlausur).collect(Collectors.toSet());
    }

    //Works
    @Override
    public Set<KlausurData> getKlausurenDataByRefs(Set<KlausurRef> angemeldeteKlausurenRefs) {
        Set<KlausurDTO> DTOs = findeKlausurenByID(angemeldeteKlausurenRefs);

        return DTOs.stream().map(k -> transferDTOToKlausur(k).getKlausurData()).collect(Collectors.toSet());
    }

    public Klausur findeKlausurByID(long id) throws Exception {
        return crudKlausur.findeKlausurByID(id).map(this::transferDTOToKlausur).orElseThrow(() -> new Exception("Diese Klausur Existiert nicht"));
    }

    @Override
    public Set<KlausurData> findAngemeldeteKlausuren(long githubID) {
        return crudKlausur.findAngemeldeteKlausuren(githubID);
    }

    private Set<KlausurDTO> findeKlausurenByID(Set<KlausurRef> angemeldeteKlausurenRefs) {
        Set<Long> IDs = angemeldeteKlausurenRefs.stream().map(KlausurRef::getLsfID).collect(Collectors.toSet());
        Set<KlausurDTO> DTOs = new HashSet<>();

        for (Long id : IDs) {
            crudKlausur.findeKlausurByID(id).ifPresent(DTOs::add);
        }
        return DTOs;
    }

    Klausur transferDTOToKlausur(KlausurDTO dto) {
        return new Klausur( dto.getName(), dto.getLsfID(), dto.isPraesenz(), dto.getDatum().toString(), dto.getBeginn().toString(), dto.getEnde().toString());

    }

    KlausurDTO transferKlausurToDTO(Klausur s){
        return new KlausurDTO(s.getLsfid(), s.getName(), s.isPraesenz(), s.getDatum(), s.getBeginn(),s.getEnde());
    }

}
