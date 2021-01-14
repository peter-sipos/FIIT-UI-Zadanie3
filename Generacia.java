import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Generacia {

    private List<Jedinec> jedinci;

    public Generacia(){     // vytvorenie "prazdnej" / novej generacie
        this.jedinci = new ArrayList<>();
    }

    public Generacia(int velkostGeneracie, int obvod){  // vytvorenie generacie rovno s jej inicializaciou
        this.jedinci = new ArrayList<>();
        for (int i = 0; i < velkostGeneracie; i++) {
            jedinci.add(new Jedinec(obvod));
        }
    }


    public Jedinec vyberNajlepsieho(){      // vyberie najlepsieho jedinca z generacie
        Collections.sort(jedinci);
        return jedinci.get(0);
    }

    public List<Jedinec> vyberXNajlepsich(int pocet){   // vyberie dany pocet najlepsich jedincov z generacie
        Collections.sort(jedinci);
        List<Jedinec> vybrati = new ArrayList<>();
        for (int i = 0; i < pocet; i++){
            vybrati.add(jedinci.get(i));
        }
        return vybrati;
    }

    @Override
    public String toString() {
        return "Generacia{" +
                "jedinci=" + jedinci +
                '}';
    }

    public List<Jedinec> getJedinci() {
        return jedinci;
    }
}
