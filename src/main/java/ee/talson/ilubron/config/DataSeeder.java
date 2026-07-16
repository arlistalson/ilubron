package ee.talson.ilubron.config;

import ee.talson.ilubron.model.SalonService;
import ee.talson.ilubron.model.ServiceCategory;
import ee.talson.ilubron.model.Worker;
import ee.talson.ilubron.repository.SalonServiceRepository;
import ee.talson.ilubron.repository.WorkerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static ee.talson.ilubron.model.ServiceCategory.*;
import static java.time.DayOfWeek.*;

@Configuration
public class DataSeeder {

    private static final Set<DayOfWeek> MON_FRI = Set.of(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY);
    private static final LocalTime OPEN = LocalTime.of(9, 0);
    private static final LocalTime CLOSE = LocalTime.of(20, 0);

    @Bean
    CommandLineRunner seed(WorkerRepository workers, SalonServiceRepository services) {
        return args -> {
            if (workers.count() > 0) {
                return;
            }

            workers.saveAll(List.of(
                    new Worker("Doris", "doristukk@gmail.com", Set.of(JUUKSUR), MON_FRI, OPEN, CLOSE),
                    new Worker("Ene", "eneorumaa2@gmail.com", Set.of(JUUKSUR), MON_FRI, OPEN, CLOSE),
                    new Worker("Terje", "terts75@gmail.com", Set.of(JUUKSUR), MON_FRI, OPEN, CLOSE),
                    new Worker("Keili", "juursalu.keili@gmail.com", Set.of(MASSAAZ), MON_FRI, OPEN, CLOSE),
                    // SmartBron: Anette teeb ripsmeid K ja R; pediküür samadel päevadel kuni täpsustub
                    new Worker("Anette", "anettekontson@gmail.com", Set.of(RIPSMED, PEDIKYYR),
                            Set.of(WEDNESDAY, FRIDAY), OPEN, CLOSE)
            ));

            services.saveAll(List.of(
                    // Juuksur – SmartBroni kestused ja hinnad
                    new SalonService("Naiste lõikus", JUUKSUR, "al. 35 €", 30),
                    new SalonService("Meeste lõikus", JUUKSUR, "25 €", 30),
                    new SalonService("Masina lõikus", JUUKSUR, "15 €", 30),
                    new SalonService("Tuka lõikus", JUUKSUR, "10 €", 15),
                    new SalonService("Pesu + föönisoeng", JUUKSUR, "al. 25 €", 30),
                    new SalonService("Ülespandud soeng", JUUKSUR, "35 €", 90),
                    new SalonService("Osaliselt ülespandud soeng", JUUKSUR, "35 €", 60),
                    new SalonService("Juurevärv (kuni 2 cm väljakasv) + lõikus + föönitamine", JUUKSUR, "al. 65 €", 120),
                    new SalonService("Värvimine + lõikus + föönisoeng (lühikesed juuksed, poisipea)", JUUKSUR, "al. 70 €", 120),
                    new SalonService("Värvimine + lõikus + föönisoeng (poolpikad juuksed)", JUUKSUR, "al. 80 €", 120),
                    new SalonService("Värvimine + lõikus + föönisoeng (pikad juuksed)", JUUKSUR, "al. 90 €", 120),
                    new SalonService("Värvimine + lõikus + föönisoeng (ülipikad juuksed)", JUUKSUR, "al. 100 €", 150),
                    new SalonService("Ainult salgutamine + lõikus + föönisoeng (poolpikad juuksed)", JUUKSUR, "al. 80 €", 120),
                    new SalonService("Ainult salgutamine + lõikus + föönisoeng (pikad juuksed)", JUUKSUR, "al. 90 €", 120),
                    new SalonService("Ainult salgutamine + lõikus + föönisoeng (ülipikad juuksed)", JUUKSUR, "al. 110 €", 180),
                    new SalonService("Põhi + salk + föön (lühikesed juuksed, poisipea)", JUUKSUR, "al. 80 €", 120),
                    new SalonService("Põhi + salk + föön (poolpikad juuksed)", JUUKSUR, "al. 90 €", 120),
                    new SalonService("Põhi + salk + föön (pikad juuksed)", JUUKSUR, "al. 100 €", 150),
                    new SalonService("Põhi + salk + föön (ülipikad juuksed)", JUUKSUR, "al. 120 €", 180),

                    new SalonService("Megamõnnatamine, 2 h", MASSAAZ, "80 €", 120),
                    new SalonService("Klassikaline massaaž, 1,5 h", MASSAAZ, "60 €", 90),
                    new SalonService("Klassikaline massaaž, 1 h", MASSAAZ, "45 €", 60),
                    new SalonService("Aroomimassaaž, 1,5 h", MASSAAZ, "55 €", 90),
                    new SalonService("Reflektoorne jalalabateraapia, 1,5 h", MASSAAZ, "40 €", 90),
                    new SalonService("Selg / turi / kael, 30 min", MASSAAZ, "30 €", 30),
                    new SalonService("Laste massaaž, 30 min", MASSAAZ, "25 €", 30),
                    new SalonService("Jalad, 30 min", MASSAAZ, "20 €", 30),

                    new SalonService("Klassikalised ripsmed – paigaldus", RIPSMED, "35 €", 110),
                    new SalonService("Klassikalised ripsmed – hooldus", RIPSMED, "30 €", 80),
                    new SalonService("Hübriidripsmed – paigaldus", RIPSMED, "45 €", 130),
                    new SalonService("Hübriidripsmed – hooldus", RIPSMED, "40 €", 90),

                    new SalonService("Spa-pediküür + geellakk", PEDIKYYR, "35 €", 90),
                    new SalonService("Spa-pediküür geellakita", PEDIKYYR, "30 €", 75)
            ));
        };
    }
}
