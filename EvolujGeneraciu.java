import java.util.*;

public class EvolujGeneraciu {

    private double pMutacie;
    private double pKrizenia;
    private int velkostTurnaja;
    private int pocetElitnych;
    private int pocetJedincov;
    private int obvod;
    private Mapa mapa;
    private int[][] mapaZaloha;
    private static final int BK = 5;



    public EvolujGeneraciu(int pocetJedincov, int pocetElitnych, int obvod,
                           double pMutacie, double pKrizenia, int velkostTurnaja, Mapa mapa){
        this.pocetJedincov = pocetJedincov;
        this.pocetElitnych = pocetElitnych;
        this.obvod = obvod;
        this.pMutacie = pMutacie;
        this.pKrizenia = pKrizenia;
        this.velkostTurnaja = velkostTurnaja;
        this.mapa = mapa;

        // sluzi na zalohovanie mapy -> ta sa totiz pri hrabani meni a pred dalsim hrabanim ju je potrebne zresetovat
        this.mapaZaloha = Arrays.stream(mapa.getMapa()).map(int[]::clone).toArray(int[][]::new); // zdroj: https://stackoverflow.com/questions/1686425/copy-a-2d-array-in-java
    }


    public int vypocetFitnes(Jedinec jedinec){
        int fitnes = mapa.getRiadky() * mapa.getStlpce();
        boolean pokazeneHrabanie = mapa.pohrabZahradku(jedinec);    // pohrab zahradku
        fitnes -= mapa.zistiNepohrabanePolicka();                   // zisti kolko policok sa ti nepodarilo pohrabat a na zaklade toho vypocitaj fitnes
        if (pokazeneHrabanie){                                      // ak hrabanie nebolo uplne dokonale, zniz fitnes o konstantu
            fitnes -= BK;
        }
        if (fitnes == mapa.getRiadky() * mapa.getStlpce()){         // ak sa fitnes rovna maximalnej fitnes - teda ak sme nasli spravne riesenie - tak uloz obraz pohrabanej zahradky do jedinca, aby si ju mohol potom zobrazit
            jedinec.setMapa(Arrays.stream(mapa.getMapa()).map(int[]::clone).toArray(int[][]::new));
        }
        int[][] kopiaMapy = Arrays.stream(mapaZaloha).map(int[]::clone).toArray(int[][]::new);
        mapa.setMapa(kopiaMapy);                                    // vrat zahradku do povodneho stavu
        return fitnes;
    }

    public void nastavFitnesGeneracii(Generacia generacia){         // nastavi fitnes pre kazdeho jedinca z populacie
        for (Jedinec jedinec: generacia.getJedinci()){
            jedinec.setFitnes(vypocetFitnes(jedinec));
        }
    }


    public Generacia vytvorNovuGeneraciu(Generacia generacia, String sposobKrizenia){       // hlavny algoritmus programu
        Generacia novaGeneracia = new Generacia();

        if (pocetElitnych > 0){                                                             // ak sa maju pouzit nejaki elitni jedinci, rovno ich vyber a pridaj do novej generacie
            novaGeneracia.getJedinci().addAll(generacia.vyberXNajlepsich(pocetElitnych));
        }

        while (novaGeneracia.getJedinci().size() != generacia.getJedinci().size()){         //pokial nebude mat nova generacia rovnaky pocet jedincov povodna generacia

            Jedinec otec;
            Jedinec mama;

            if (sposobKrizenia.equals("turnaj")) {                                          // vyber rodicov bud pomocou turnaja alebo ruleta, ako to zada pouzivatel
                otec = turnaj(generacia, velkostTurnaja);
                mama = turnaj(generacia, velkostTurnaja);
            } else {
                otec = ruleta(generacia);
                mama = ruleta(generacia);
            }

            List<Jedinec> deti = krizenie(otec, mama);                                      // skriz rodicov a vytvor deti

            if (budemeMutovat()){                                                           // ak na to vyjde pravdepodobnost, tak zmutuj deti
                deti.set(0, zmutujJedinca(deti.get(0)));
                deti.set(1, zmutujJedinca(deti.get(1)));
            }

            novaGeneracia.getJedinci().addAll(deti);                                        // pridaj deti do novej generacie
        }

        nastavFitnesGeneracii(novaGeneracia);                                               //nastav fitnes novej generacii

        return novaGeneracia;
    }

    public Jedinec turnaj(Generacia generacia, int velkostTurnaja){
        List<Jedinec> kandidati = new ArrayList<>();
        int cisloJedinca;

        for (int i = 0; i < velkostTurnaja; i++){                                           // kym nevyberies ziadany pocet kandidatov do turnaja
            cisloJedinca = (int)(Math.random() * generacia.getJedinci().size());
            kandidati.add(generacia.getJedinci().get(cisloJedinca));                        // vyber si nahodneho jedinca z generacie
        }
        Collections.sort(kandidati);                                                        // usporiadaj kandidatov podla fitns
        return kandidati.get(0);                                                            // a vrat najlepsieho
    }


    public Jedinec ruleta(Generacia generacia){
        Jedinec vyherca = new Jedinec();
        int totalFitnes = 0;
        for (Jedinec jedinec: generacia.getJedinci()){
            totalFitnes += jedinec.getFitnes();                                             // vypocitaj celkovy fitnes generacie
        }

        int osudie = (int)(Math.random() * totalFitnes);                                    // vyber nahodne cislo z rozsahu 0 az celkovy fitnes
        int fitnes = 0;
        for (Jedinec jedinec: generacia.getJedinci()){                                      // postupne pripocitavaj fitnes jednotlivych jedincov
            fitnes += jedinec.getFitnes();
            if (fitnes >= osudie){                                                          // jedinec, ktoreho prispevkom fitnes prekroci ono nahodne cislo, sa stava rodicom
                vyherca = jedinec;
                return vyherca;
            }
        }
        return vyherca;
    }

    public List<Jedinec> krizenie(Jedinec otec, Jedinec mama){
        List<Jedinec> deti = new ArrayList<>();
        Jedinec prve = new Jedinec();
        Jedinec druhe = new Jedinec();
        int cisloGenu = 0;

        if (Math.random()<=pKrizenia) {
            while (cisloGenu < otec.getGeny().size() / 3){                                  // prvu tretinu genov noveho jedinca sprav nahodnych
                prve.getGeny().add((int)(Math.random() * obvod));
                druhe.getGeny().add((int)(Math.random() * obvod));
                cisloGenu++;
            }
            while (cisloGenu < otec.getGeny().size()*2 / 3) {                               // druhu tretinu od otca
                prve.getGeny().add(otec.getGeny().get(cisloGenu));                          // prve dieta berie geny od zaciatku
                druhe.getGeny().add(otec.getGeny().get(otec.getGeny().size() - 1 - cisloGenu)); // druhe dieta od konca
                cisloGenu++;
            }
            while (cisloGenu < mama.getGeny().size()) {                                     // poslednu tretinu od matky
                prve.getGeny().add(mama.getGeny().get(cisloGenu));
                druhe.getGeny().add(mama.getGeny().get(mama.getGeny().size() - 1 - cisloGenu));
                cisloGenu++;
            }

        } else {                                                                             // ak sa nebudu krizit, tak vrat aj otca aj mamu
            prve = otec;
            druhe = mama;
        }

        deti.add(prve);
        deti.add(druhe);

        return deti;
    }


    public boolean budemeMutovat(){                                                         // zisti, ci vysla pravdepodobnost na mutaciu
        if (Math.random() < pMutacie){
            return true;
        }
        else return false;
    }

    public Jedinec zmutujJedinca(Jedinec jedinec){                                          // mutovanie jedinca -> zapricini, ze jedinec bude mat iba unikatne geny
        Jedinec mutant = new Jedinec(new ArrayList<>(jedinec.getGeny()));
        List<Integer> povodneGeny = new ArrayList<>(mutant.getGeny());
        Set<Integer> pocet = new HashSet<>(mutant.getGeny());

        List<Integer> duplicitneGeny = new ArrayList<>();
        while (pocet.size() < jedinec.getGeny().size()) {                                   //pokial nebudu vsetky geny opat unikatne

            // najdi duplicitne geny
            Collections.sort(povodneGeny);
            for (int i = 1; i < povodneGeny.size(); i++) {
                if (povodneGeny.get(i - 1).equals(povodneGeny.get(i))) {
                    duplicitneGeny.add(povodneGeny.get(i));
                }
            }

            // nahrad ich novymi nahodnymi genmi
            for(int gen: duplicitneGeny){
                int novyGen = (int)(Math.random() * obvod);
                int index = mutant.getGeny().indexOf(gen);
                mutant.getGeny().set(index, novyGen);
            }

            // nanovo nastav pomocne zoznamy a sety
            pocet.clear();
            pocet.addAll(mutant.getGeny());
            povodneGeny.clear();
            povodneGeny.addAll(mutant.getGeny());
            duplicitneGeny.clear();

        }

      return mutant;

    }

    public int zistiPriemernyFitnes(Generacia generacia){
        int priemer = 0;
        for (Jedinec jedinec: generacia.getJedinci()){
            priemer += jedinec.getFitnes();
        }
        return priemer/generacia.getJedinci().size();
    }
}
