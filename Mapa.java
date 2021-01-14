import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Mapa {
    private int[][] mapa;
    private int riadky;
    private int stlpce;
    private int obvod;
    private List<Point> kamene;
    private HashMap<Integer, Point> startovaciePozicie;

    public Mapa(int riadky, int stlpce, List<Point> kamene) {
        this.mapa = new int[riadky][stlpce];
        this.kamene = kamene;
        this.riadky = riadky;
        this.stlpce = stlpce;
        this.obvod = 2 * riadky + 2 * stlpce;

        for (Point kamen : kamene) {         // inicializuj mapu - nastav kamene
            mapa[kamen.x][kamen.y] = -1;
        }

        this.startovaciePozicie = vytvorStartovaciePozicie();

    }

    public String ukazMapu() {          // pomocou toString funkcie vypise samotnu zahradku
        String print = new String();
        for (int i = 0; i < riadky; i++) {
            for (int j = 0; j < stlpce; j++) {
                print += "\t" + mapa[i][j];
            }
            print +="\n";
        }
        print += "\n";
        return print;
    }


    public boolean pohrabZahradku(Jedinec jedinec) {        // funkcia sluziaca na de-facto vypocet fitnes. Podla genov jedinca sa snazi pohrabat zahradku.
        Point start;
        int smer;
        int riadok;
        int stlpec;
        int cisloTahu;
        boolean pokazeneHrabanie = false;                   // flag, ktory signalizuje, ci sa pocas hrabania mnich niekedy zasekol uprostred zahradky

        cisloTahu = 1;
        for (int gen : jedinec.getGeny()) {

            // nastavenie startovacej pozicie a smeru hrabania
            start = zistiStartovaciuPoziciu(gen);
            smer = zistiSmerHrabania(gen);
            riadok = start.x;
            stlpec = start.y;


            hrabanie:
            while (true) {                                  // mnich hrabe, az kym sa nema bud kam pohnut (ostal uviaznuty v strede zahradky) alebo az kym z nej opat nevyjde
                switch (smer) {
                    case 0:                                 // hrabanie dole
                        if (pohrabanyStart(riadok, stlpec)){    // ak bolo startovacie policko uz pohrabane, ani nezacinaj hrabat
                            break hrabanie;
                        }
                        while (validnyHrab(riadok, stlpec)) {   //ak si stupil na validne policko (nepohrabane a vramci zahradky), tak ho pohrab -> zapis donho cislo tahu a posun sa na dalsie
                            mapa[riadok][stlpec] = cisloTahu;
                            riadok++;
                            if (riadok == riadky) {             //ak sa ti podarilo spravit kompletny hrab tak konci hrab a chod na dalsi gen
                                break hrabanie;
                            }
                        }
                        riadok--;                           // ak si zistil, ze si dosiel na policko ktore nemozes pohrabat (kamen, uz pohrabane), vrat sa o jedno policko dozadu


                        if (Math.random() <= 0.5) {         // nahodne sa rozhodni, ci sa budes pokusat ist najprv dolava a potom doprava alebo naopak
                            if (validnyHrab(riadok, stlpec - 1)) {       // ak mozes ist dolava, nastav smer hrabania na vlavo a pohni sa tam
                                smer = 1;
                                stlpec--;
                            } else if (validnyHrab(riadok, stlpec + 1)) {   // ak si nemohol ist dolava, tak ask mozes ist doprava, tak nastav smer na vpravo a pohni sa tam
                                smer = 3;
                                stlpec++;
                            } else {                                            // ak si nemohol ist ani tam ani tam, ostal si uviaznuty v strede zahradky. Nastav preto pokazene hrabanie a chod na dalsi gen
                                pokazeneHrabanie = true;
                                break hrabanie;
                            }
                        } else {                            // to co vyssie, len v opacnom poradi
                            if (validnyHrab(riadok, stlpec + 1)) {
                                smer = 3;
                                stlpec++;
                            }
                            else if (validnyHrab(riadok, stlpec - 1)) {
                                smer = 1;
                                stlpec--;
                            }else {
                                pokazeneHrabanie = true;
                                break hrabanie;
                            }
                        }


                        if (vychodZoZahrady(riadok, stlpec)){   // ak si sa pohnutim do strany dostal von zo zahradky, rovno ukonci hrab a chod na dalsi gen
                            break hrabanie;
                        }
                        break;


                    case 1:                 // hrabanie dolava
                        if (pohrabanyStart(riadok, stlpec)){
                            break hrabanie;
                        }
                        while (validnyHrab(riadok, stlpec)) {
                            mapa[riadok][stlpec] = cisloTahu;
                            stlpec--;
                            if (stlpec < 0) {
                                break hrabanie;
                            }
                        }
                        stlpec++;

                        if (Math.random() <= 0.5) {
                            if (validnyHrab(riadok - 1, stlpec)) {
                                smer = 2;
                                riadok--;
                            } else if (validnyHrab(riadok + 1, stlpec)) {
                                smer = 0;
                                riadok++;
                            } else {
                                pokazeneHrabanie = true;
                                break hrabanie;
                            }
                        } else {
                            if (validnyHrab(riadok + 1, stlpec)) {
                                smer = 0;
                                riadok++;
                            } else if (validnyHrab(riadok - 1, stlpec)) {
                                smer = 2;
                                riadok--;
                            } else {
                                pokazeneHrabanie = true;
                                break hrabanie;
                            }
                        }

                        if (vychodZoZahrady(riadok, stlpec)) {
                            break hrabanie;
                        }
                        break;



                    case 2:             // hrabanie hore
                        if (pohrabanyStart(riadok, stlpec)){
                            break hrabanie;
                        }
                        while (validnyHrab(riadok, stlpec)) {
                            mapa[riadok][stlpec] = cisloTahu;
                            riadok--;
                            if (riadok < 0) {
                                break hrabanie;
                            }
                        }
                        riadok++;

                        if (Math.random() <= 0.5) {
                            if (validnyHrab(riadok, stlpec - 1)) {
                                smer = 1;
                                stlpec--;
                            } else if (validnyHrab(riadok, stlpec + 1)) {
                                smer = 3;
                                stlpec++;
                            } else {
                                pokazeneHrabanie = true;
                                break hrabanie;
                            }
                        } else {
                            if (validnyHrab(riadok, stlpec + 1)) {
                                smer = 3;
                                stlpec++;
                            }
                            else if (validnyHrab(riadok, stlpec - 1)) {
                                smer = 1;
                                stlpec--;
                            }else {
                                pokazeneHrabanie = true;
                                break hrabanie;
                            }
                        }

                        if (vychodZoZahrady(riadok, stlpec)){
                            break hrabanie;
                        }
                        break;


                    case 3:                 // hrabanie doprava
                        if (pohrabanyStart(riadok, stlpec)){
                            break hrabanie;
                        }
                        while (validnyHrab(riadok, stlpec)) {
                            mapa[riadok][stlpec] = cisloTahu;
                            stlpec++;
                            if (stlpec == stlpce) {
                                break hrabanie;
                            }
                        }
                        stlpec--;

                        if (Math.random() <= 0.5) {
                            if (validnyHrab(riadok - 1, stlpec)) {
                                smer = 2;
                                riadok--;
                            } else if (validnyHrab(riadok + 1, stlpec)) {
                                smer = 0;
                                riadok++;
                            } else {
                                pokazeneHrabanie = true;
                                break hrabanie;
                            }
                        } else {
                            if (validnyHrab(riadok + 1, stlpec)) {
                                smer = 0;
                                riadok++;
                            } else if (validnyHrab(riadok - 1, stlpec)) {
                                smer = 2;
                                riadok--;
                            } else {
                                pokazeneHrabanie = true;
                                break hrabanie;
                            }
                        }
                        if (vychodZoZahrady(riadok, stlpec)){
                            break hrabanie;
                        }
                        break;
                }
            }

            cisloTahu++;

        }
        return pokazeneHrabanie;
    }

    public boolean validnyHrab(int riadok, int stlpec) {        // zisti, ci hrab, ktory chcem spravit je platny
        if (vychodZoZahrady(riadok, stlpec)){
            return true;
        } else if (mapa[riadok][stlpec] != 0) {                 // skontroluje, ci policko este nebolo pohrabane
            return false;
        }
        return true;
    }

    public boolean vychodZoZahrady(int riadok, int stlpec){     // skontroluje, ci som nahodou hrabom uz nevysiel zo zahradky
        if (riadok == riadky || riadok == -1) {
            return true;
        }
        if (stlpec == stlpce || stlpec == -1) {
            return true;
        }
        return false;
    }

    public boolean pohrabanyStart(int riadok, int stlpec){      // skontroluje, ci uz nahodou nebolo pohrabane startovacie policko
        if (mapa[riadok][stlpec] != 0){
            return true;
        } else return false;
    }

    public int zistiSmerHrabania(int gen) {                     // z genu zisti smer hrabania. Keby bol return type String, bolo by to prehladnejsie, no int je efektivnejsi
        int smer = stlpce;
        if (smer > gen) return 0;   //dole
        smer += riadky;
        if (smer > gen) return 1;   //dolava
        smer += stlpce;
        if (smer > gen) return 2;   //hore
        return 3;                   //doprava
    }

    public HashMap<Integer, Point> vytvorStartovaciePozicie() { // vytvori mozne startovacie pozicie po okraji zahradky
        HashMap<Integer, Point> map = new HashMap();
        int pozicia = 0;
        for (int i = 0; i < stlpce; i++) {
            map.put(pozicia, new Point(0, i));
            pozicia++;
        }

        for (int i = 0; i < riadky; i++) {
            map.put(pozicia, new Point(i, stlpce - 1));
            pozicia++;
        }

        for (int i = stlpce - 1; i >= 0; i--) {
            map.put(pozicia, new Point(riadky - 1, i));
            pozicia++;
        }

        for (int i = riadky - 1; i >= 0; i--) {
            map.put(pozicia, new Point(i, 0));
            pozicia++;
        }

        return map;
    }

    public Point zistiStartovaciuPoziciu(int gen) {
        return startovaciePozicie.get(gen);
    }

    public int zistiNepohrabanePolicka() {                      // spocita policka, ktore sa mnichovy nepodarilo pohrabat
        int nepohrabane = 0;
        for (int riadok = 0; riadok < riadky; riadok++) {
            for (int stlpec = 0; stlpec < stlpce; stlpec++) {
                if (mapa[riadok][stlpec] == 0) {
                    nepohrabane++;
                }
            }
        }
        return nepohrabane;
    }




    public int[][] getMapa() {
        return mapa;
    }

    public void setMapa(int[][] mapa) {
        this.mapa = mapa;
    }

    public int getRiadky() {
        return riadky;
    }

    public int getStlpce() {
        return stlpce;
    }


    @Override
    public String toString() {
        return ukazMapu();
    }
}
