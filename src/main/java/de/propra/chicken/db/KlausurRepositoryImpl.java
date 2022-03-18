package de.propra.chicken.db;

import de.propra.chicken.application.service.repo.KlausurRepository;
import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.KlausurData;
import de.propra.chicken.domain.model.KlausurRef;
import de.propra.chicken.db.dto.KlausurDTO;
import org.springframework.stereotype.Repository;

import java.util.Collection;
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
    public void speicherKlausur(Klausur klausur) {
        KlausurDTO dto = transferKlausurToDTO(klausur);
        crudKlausur.save(dto);
    }


    @Override
    public Set<Klausur> ladeAlleKlausuren() {
        Set<KlausurDTO> all = crudKlausur.findAll();
        Set<Klausur> klausuren = new HashSet<>();
        for(KlausurDTO curr : all) {
            klausuren.add(transferDTOToKlausur(curr));
        }
        return klausuren;
    }

    @Override
    public Set<Klausur> getKlausurenByRefs(Set<KlausurRef> klausurenRef) {
        Set<Long> IDs = klausurenRef.stream().map(klausurRef -> klausurRef.getLsfID()).collect(Collectors.toSet());
        Set<KlausurDTO> DTOs = new HashSet<>();

        //DTOs.addAll((Collection<? extends KlausurDTO>) crudKlausur.findAllById(IDs));

        return DTOs.stream().map(k -> transferDTOToKlausur(k)).collect(Collectors.toSet());
    }

    @Override
    public Set<KlausurData> getKlausurenDataByRefs(Set<KlausurRef> angemeldeteKlausurenRefs) {
        Set<Long> IDs = angemeldeteKlausurenRefs.stream().map(klausurRef -> klausurRef.getLsfID()).collect(Collectors.toSet());
        Set<KlausurDTO> DTOs = new HashSet<>();

        DTOs.addAll((Collection<? extends KlausurDTO>) crudKlausur.findAllById(IDs));

        return DTOs.stream().map(k -> transferDTOToKlausur(k).getKlausurData()).collect(Collectors.toSet());
    }

    private Klausur transferDTOToKlausur(KlausurDTO dto) {
        return new Klausur( dto.getName(), dto.getLsfID(), dto.isPraesenz(), dto.getDatum().toString(), dto.getBeginn().toString(), dto.getEnd().toString());

    }

    private KlausurDTO transferKlausurToDTO(Klausur s){
        return new KlausurDTO(s.getLsfid(), s.getName(), s.isPraesenz(), s.getDatum(), s.getBeginn(),s.getEnd());
    }

}
