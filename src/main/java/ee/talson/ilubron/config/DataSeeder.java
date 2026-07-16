package ee.talson.ilubron.config;

import ee.talson.ilubron.model.SalonService;
import ee.talson.ilubron.model.Worker;
import ee.talson.ilubron.repository.SalonServiceRepository;
import ee.talson.ilubron.repository.WorkerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static java.time.DayOfWeek.*;

/**
 * Seeds an empty database with one salon's catalog. Which salon comes from the
 * property ilubron.seed (default "ilutegu"; "onne" for the Õnne Ilusalong demo).
 */
@Configuration
public class DataSeeder {

    private static final Set<DayOfWeek> MON_FRI = Set.of(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY);

    @Value("${ilubron.seed:ilutegu}")
    private String seed;

    @Bean
    CommandLineRunner seedData(WorkerRepository workers, SalonServiceRepository services) {
        return args -> {
            if (workers.count() > 0) {
                return;
            }
            if ("onne".equalsIgnoreCase(seed)) {
                seedOnne(workers, services);
            } else {
                seedIlutegu(workers, services);
            }
        };
    }

    private void seedIlutegu(WorkerRepository workers, SalonServiceRepository services) {
        LocalTime open = LocalTime.of(9, 0), close = LocalTime.of(20, 0);
        List<Worker> staff = List.of(
                new Worker("Doris", "doristukk@gmail.com", Set.of("Juuksur"), MON_FRI, open, close),
                new Worker("Ene", "eneorumaa2@gmail.com", Set.of("Juuksur"), MON_FRI, open, close),
                new Worker("Terje", "terts75@gmail.com", Set.of("Juuksur"), MON_FRI, open, close),
                new Worker("Keili", "juursalu.keili@gmail.com", Set.of("Massaaž"), MON_FRI, open, close),
                // SmartBron: Anette teeb ripsmeid K ja R; pediküür samadel päevadel kuni täpsustub
                new Worker("Anette", "anettekontson@gmail.com", Set.of("Ripsmed", "Pediküür"),
                        Set.of(WEDNESDAY, FRIDAY), open, close)
        );
        // Arenduse PIN-id; toodangus määra päris PIN-id admin-otspunktiga PATCH /workers/{id}/pin
        staff.forEach(w -> w.setPin("0000"));
        workers.saveAll(staff);

        services.saveAll(List.of(
                new SalonService("Naiste lõikus", "Juuksur", "al. 35 €", 30),
                new SalonService("Meeste lõikus", "Juuksur", "25 €", 30),
                new SalonService("Masina lõikus", "Juuksur", "15 €", 30),
                new SalonService("Tuka lõikus", "Juuksur", "10 €", 15),
                new SalonService("Pesu + föönisoeng", "Juuksur", "al. 25 €", 30),
                new SalonService("Ülespandud soeng", "Juuksur", "35 €", 90),
                new SalonService("Osaliselt ülespandud soeng", "Juuksur", "35 €", 60),
                new SalonService("Juurevärv (kuni 2 cm väljakasv) + lõikus + föönitamine", "Juuksur", "al. 65 €", 120),
                new SalonService("Värvimine + lõikus + föönisoeng (lühikesed juuksed, poisipea)", "Juuksur", "al. 70 €", 120),
                new SalonService("Värvimine + lõikus + föönisoeng (poolpikad juuksed)", "Juuksur", "al. 80 €", 120),
                new SalonService("Värvimine + lõikus + föönisoeng (pikad juuksed)", "Juuksur", "al. 90 €", 120),
                new SalonService("Värvimine + lõikus + föönisoeng (ülipikad juuksed)", "Juuksur", "al. 100 €", 150),
                new SalonService("Ainult salgutamine + lõikus + föönisoeng (poolpikad juuksed)", "Juuksur", "al. 80 €", 120),
                new SalonService("Ainult salgutamine + lõikus + föönisoeng (pikad juuksed)", "Juuksur", "al. 90 €", 120),
                new SalonService("Ainult salgutamine + lõikus + föönisoeng (ülipikad juuksed)", "Juuksur", "al. 110 €", 180),
                new SalonService("Põhi + salk + föön (lühikesed juuksed, poisipea)", "Juuksur", "al. 80 €", 120),
                new SalonService("Põhi + salk + föön (poolpikad juuksed)", "Juuksur", "al. 90 €", 120),
                new SalonService("Põhi + salk + föön (pikad juuksed)", "Juuksur", "al. 100 €", 150),
                new SalonService("Põhi + salk + föön (ülipikad juuksed)", "Juuksur", "al. 120 €", 180),

                new SalonService("Megamõnnatamine, 2 h", "Massaaž", "80 €", 120),
                new SalonService("Klassikaline massaaž, 1,5 h", "Massaaž", "60 €", 90),
                new SalonService("Klassikaline massaaž, 1 h", "Massaaž", "45 €", 60),
                new SalonService("Aroomimassaaž, 1,5 h", "Massaaž", "55 €", 90),
                new SalonService("Reflektoorne jalalabateraapia, 1,5 h", "Massaaž", "40 €", 90),
                new SalonService("Selg / turi / kael, 30 min", "Massaaž", "30 €", 30),
                new SalonService("Laste massaaž, 30 min", "Massaaž", "25 €", 30),
                new SalonService("Jalad, 30 min", "Massaaž", "20 €", 30),

                new SalonService("Klassikalised ripsmed – paigaldus", "Ripsmed", "35 €", 110),
                new SalonService("Klassikalised ripsmed – hooldus", "Ripsmed", "30 €", 80),
                new SalonService("Hübriidripsmed – paigaldus", "Ripsmed", "45 €", 130),
                new SalonService("Hübriidripsmed – hooldus", "Ripsmed", "40 €", 90),

                new SalonService("Spa-pediküür + geellakk", "Pediküür", "35 €", 90),
                new SalonService("Spa-pediküür geellakita", "Pediküür", "30 €", 75)
        ));
    }

    private void seedOnne(WorkerRepository workers, SalonServiceRepository services) {
        LocalTime open = LocalTime.of(10, 0), close = LocalTime.of(20, 0);
        List<Worker> staff = List.of(
                new Worker("Anni", null, Set.of("Juuksur"), MON_FRI, open, close),
                new Worker("Ave", null, Set.of("Juuksur"), MON_FRI, open, close),
                new Worker("Maiu", null, Set.of("Juuksur"), MON_FRI, open, close),
                new Worker("Lisell", null, Set.of("Juuksur"), MON_FRI, open, close),
                new Worker("Marie-Helene", null, Set.of("Juuksur"), MON_FRI, open, close),
                new Worker("Veronika", null, Set.of("Kosmeetik", "Maniküür", "Pediküür"), MON_FRI, open, close),
                new Worker("Krista", null, Set.of("Kosmeetik", "Pediküür"), MON_FRI, open, close),
                new Worker("Sandra", null, Set.of("Kosmeetik", "Maniküür", "Pediküür"), MON_FRI, open, close),
                new Worker("Virge", null, Set.of("Maniküür", "Pediküür"), MON_FRI, open, close),
                new Worker("Geidi", null, Set.of("Püsimeik"), MON_FRI, open, close)
        );
        staff.forEach(w -> w.setPin("0000"));
        workers.saveAll(staff);

        services.saveAll(List.of(
                new SalonService("Naiste juukselõikus", "Juuksur", "40 €", 60),
                new SalonService("Juuste otste piiramine", "Juuksur", "30 €", 30),
                new SalonService("Tuka lõikus", "Juuksur", "5 €", 15),
                new SalonService("Föönisoeng", "Juuksur", "35 €", 45),
                new SalonService("Pidulik lahtine soeng (lokid/lained)", "Juuksur", "40 €", 60),
                new SalonService("Ülespandud soeng", "Juuksur", "45 €", 75),
                new SalonService("Meeste juukselõikus", "Juuksur", "30 €", 30),
                new SalonService("Masinlõikus", "Juuksur", "20 €", 20),
                new SalonService("Laste lõikus (kuni 10 a)", "Juuksur", "20–25 €", 30),
                new SalonService("Juurevärv (kuni 2 cm)", "Juuksur", "55–60 €", 120),
                new SalonService("Täispikkuses värv, lühikesed juuksed", "Juuksur", "60–65 €", 120),
                new SalonService("Täispikkuses värv, poolpikad juuksed", "Juuksur", "65–70 €", 135),
                new SalonService("Täispikkuses värv, pikad juuksed", "Juuksur", "70–80 €", 150),
                new SalonService("Täispikkuses värv, ülipikad juuksed", "Juuksur", "80–100 €", 165),
                new SalonService("Salgutamine täispikkuses, lühikesed juuksed", "Juuksur", "75–80 €", 150),
                new SalonService("Salgutamine täispikkuses, pikad juuksed", "Juuksur", "85–90 €", 180),
                new SalonService("Joico K-PAK intensiivhooldus", "Juuksur", "45–55 €", 60),
                new SalonService("K18 hooldus", "Juuksur", "al. 20 €", 30),

                new SalonService("Ekspress näohooldus", "Kosmeetik", "35 €", 30),
                new SalonService("Ekspress näohooldus + käte parafiin", "Kosmeetik", "45 €", 45),
                new SalonService("Intensiivne puhastav hooldus näole", "Kosmeetik", "50 €", 60),
                new SalonService("Rahustav hooldus tundlikule nahale", "Kosmeetik", "55 €", 60),
                new SalonService("Näopuhastus massaažiga", "Kosmeetik", "60 €", 75),
                new SalonService("Ultraheli näopuhastus", "Kosmeetik", "60 €", 60),
                new SalonService("Näo keemiline koorimine", "Kosmeetik", "65 €", 60),
                new SalonService("Age Summum", "Kosmeetik", "75 €", 75),
                new SalonService("LED valgusteraapia näole", "Kosmeetik", "90 €", 60),
                new SalonService("Lõõgastav näohooldus meestele", "Kosmeetik", "55 €", 60),

                new SalonService("Klassikaline maniküür ilma lakita", "Maniküür", "25 €", 45),
                new SalonService("Klassikaline maniküür tavalakiga", "Maniküür", "30 €", 60),
                new SalonService("Geellakiga maniküür", "Maniküür", "40 €", 75),
                new SalonService("Geellakiga maniküür lihtsa disainiga", "Maniküür", "45 €", 90),
                new SalonService("Geellaki eemaldus maniküüriga", "Maniküür", "30 €", 60),

                new SalonService("Pediküür ilma lakita", "Pediküür", "45 €", 60),
                new SalonService("Klassikaline pediküür tavalakiga", "Pediküür", "47 €", 75),
                new SalonService("Geellakiga pediküür", "Pediküür", "50 €", 90),
                new SalonService("Pediküür prantsuse geellakiga", "Pediküür", "55 €", 90),
                new SalonService("SOODUSHIND! Geellakiga pediküür + kulmud", "Pediküür", "75 €", 105),

                new SalonService("Akvarell tehnikas huulte püsimeik", "Püsimeik", "140 €", 150),
                new SalonService("Puudertehnikas kulmu püsimeik", "Püsimeik", "140 €", 150),
                new SalonService("Ripsmetevaheline lainer", "Püsimeik", "100 €", 90),
                new SalonService("Klassikaline / hajutusega lainer", "Püsimeik", "140 €", 120),
                new SalonService("Püsimeigi hooldus 2–4 kuud", "Püsimeik", "65 €", 90),
                new SalonService("Püsimeigi hooldus 4–24 kuud", "Püsimeik", "100 €", 120)
        ));
    }
}
