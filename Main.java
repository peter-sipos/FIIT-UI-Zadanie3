import javafx.scene.chart.XYChart;
import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {

        // nacitavanie udajov od pouzivatela
        Scanner stdin = new Scanner(System.in);

        System.out.println("Zadajte pocet riadkov a stlpcov zenovej zahradky");
        int riadky = stdin.nextInt();
        int stlpce = stdin.nextInt();
        int obvod = 2*riadky + 2*stlpce;

        System.out.println("Zadajte pocet kamenov");
        int pocetKamenov = stdin.nextInt();

        System.out.println("Zadajte suradnice kamenov v poradi riadok, stlpec, oddelene medzerou");
        List<Point> kamene = new ArrayList<>();
        for (int i = 0; i < pocetKamenov; i++){
            kamene.add(new Point(stdin.nextInt(), stdin.nextInt()));
        }

        System.out.println("Zadajte pocet jedincov v generacii (minimalne 20 a iba parny pocet)");
        int pocetJedincov = stdin.nextInt();

        System.out.println("Zadajte sposob krizenia - turnaj alebo ruleta");
        String sposobKrizenia = stdin.next();

        int velkostTurnaja;
        if (sposobKrizenia.equals("turnaj")) {
            System.out.println("Zadajte velkost turnaja");
            velkostTurnaja = stdin.nextInt();
        } else {
            velkostTurnaja = 0;
        }

        System.out.println("Zadajte pravdepodobnost mutacie");
        double pMutacie = stdin.nextDouble();

        System.out.println("Zadajte pravdepodobnost krizenia jedincov");
        double pKrizenia = stdin.nextDouble();

        System.out.println("Zadajte pocet elitnych jedincov - iba parny pocet");
        int pocetElitnych = stdin.nextInt();

        Mapa mapa = new Mapa(riadky, stlpce, kamene);
        final int maxFitnes = riadky*stlpce;
        int cisloGeneracie = 0;

        EvolujGeneraciu evolujGeneraciu =
                new EvolujGeneraciu(pocetJedincov, pocetElitnych, obvod, pMutacie, pKrizenia, velkostTurnaja, mapa);

        Generacia generacia = new Generacia(pocetJedincov, obvod);
        evolujGeneraciu.nastavFitnesGeneracii(generacia);
//        int tisicka = 1;

        List<Integer> najlepsieFitnes = new ArrayList<>();
        List<Integer> priemerneFitnes = new ArrayList<>();
        List<Integer> cislaGeneracii = new ArrayList<>();


        while (generacia.vyberNajlepsieho().getFitnes() < maxFitnes){
            generacia = evolujGeneraciu.vytvorNovuGeneraciu(generacia, sposobKrizenia);
            cisloGeneracie++;
            cislaGeneracii.add(cisloGeneracie);

            //nova krv                                                          //po finalnych upravach algoritmu nepotrebne - pocet generacii uz nepresiahol ani 20000
//            if (cisloGeneracie == tisicka*100000){
//                tisicka++;
//                for (int i = 0; i < generacia.getJedinci().size()/2; i++){
//                    generacia.getJedinci().set(i, new Jedinec(obvod));
//                }
//            }
            najlepsieFitnes.add(generacia.vyberNajlepsieho().getFitnes());
            priemerneFitnes.add(evolujGeneraciu.zistiPriemernyFitnes(generacia));

        }

        System.out.println("Cislo vitaznej generacie: " + cisloGeneracie);
        System.out.println("Vitazny jedinec:" + generacia.vyberNajlepsieho());
        for (int i = 0; i < riadky; i++) {
            for (int j = 0; j < stlpce; j++) {
                System.out.printf("\t" + generacia.vyberNajlepsieho().getMapa()[i][j]);
            }
            System.out.printf("\n");
        }
        System.out.printf("\n");


        // vytvorenie a zobrazenie - zdroj: https://knowm.org/open-source/xchart/xchart-example-code/
        org.knowm.xchart.XYChart chart = QuickChart.getChart("Vyvoj najlepsej fitnes", "Cislo generacie", "Najlepsia fites", "fitnes", cislaGeneracii, najlepsieFitnes);
        new SwingWrapper(chart).displayChart();
        org.knowm.xchart.XYChart chart2 = QuickChart.getChart("Vyvoj priemernej fitnes", "Cislo generacie", "Priemerna fites", "fitnes", cislaGeneracii, priemerneFitnes);
        new SwingWrapper(chart2).displayChart();

    }




}
