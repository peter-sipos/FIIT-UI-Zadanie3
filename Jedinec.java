import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Jedinec implements Comparable<Jedinec>{

    private List<Integer> geny;
    private int fitnes;
    private int [][] mapa;

    public Jedinec(){
        this.geny = new ArrayList<>();
        this.fitnes = 0;
    }

    public Jedinec(List<Integer> geny){
        this.geny = geny;
        this.fitnes = 0;
    }

    public Jedinec(int obvod){
        geny = vytvorGeny(obvod);
        fitnes = 0;
    }

    private List<Integer> vytvorGeny(int obvod){        // vygeneruje obvod/2 nahodnych cisel z rozsahu 0 az obvod-1
        List<Integer> nahodneGeny = new ArrayList<>();
        List<Integer> nahoda = new ArrayList<>();

        for (int i = 0; i < obvod; i++){
            nahoda.add(new Integer(i));
        }
        Collections.shuffle(nahoda);

        for (int i = 0; i < obvod/2; i++){
            nahodneGeny.add(nahoda.get(i));
        }

        return nahodneGeny;
    }

    public List<Integer> getGeny() {
        return geny;
    }

    public int getFitnes() {
        return fitnes;
    }

    public void setFitnes(int fitnes) {
        this.fitnes = fitnes;
    }

    public int[][] getMapa() {
        return mapa;
    }

    public void setMapa(int[][] mapa) {
        this.mapa = mapa;
    }

    @Override
    public int compareTo(Jedinec porovnavany) {         // sluzi na zoradenie jedincov podla velkosti fitnes vramci generacie
        int porovnajFitnes = ((Jedinec)porovnavany).getFitnes();
        return porovnajFitnes - this.fitnes;
    }

    @Override
    public String toString() {
        return "Jedinec{" +
                "geny=" + geny +
                ", fitnes=" + fitnes +
                '}';
    }
}
