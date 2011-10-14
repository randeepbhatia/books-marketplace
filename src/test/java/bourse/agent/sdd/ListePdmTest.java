package bourse.agent.sdd;

import static com.google.common.collect.Iterators.forArray;
import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.MapAssert.entry;

import java.util.Iterator;
import java.util.Map;

import org.junit.Test;

import bourse.protocole.Protocole;
import bourse.protocole.ResultBye;

public class ListePdmTest {

    @Test
    public void should_get_pdm_list() {
        final ListePdm marketPlaces = new ListePdm();
        Pdm pdm = new Pdm("name", "address");
        marketPlaces.ajouter(pdm);
        Pdm pdm2 = new Pdm("nom", "adresse");
        marketPlaces.ajouter(pdm2);

        Map<String, Pdm> liste = marketPlaces.getListe();

        assertThat(liste).includes(entry("name", pdm), entry("nom", pdm2));
    }

    @Test
    public void should_remove_pdm_by_name() {
        final ListePdm marketPlaces = new ListePdm();
        Pdm pdm = new Pdm("name", "address");
        marketPlaces.ajouter(pdm);

        marketPlaces.supprimer(new Pdm("name", "empty address"));

        assertThat(marketPlaces.estVide()).isTrue();
    }

    @Test
    public void should_render_human_readable_string_of_herself_even_empty() {
        final ListePdm marketPlaces = new ListePdm();

        assertThat(marketPlaces.toString()).isEqualTo("[]");
    }

    @Test
    public void should_render_human_readable_string_of_herself_with_more_than_one_pdm() {
        final ListePdm marketPlaces = new ListePdm();
        marketPlaces.ajouter(new Pdm("name", "127.0.0.1:8080"));
        marketPlaces.ajouter(new Pdm("secondName", "192.168.0.1:21"));

        Iterator<String> lines = forArray(marketPlaces.toString().split("\n"));
        assertThat(lines.next()).isEqualTo("[nom = secondName, adresse = [ip = 192.168.0.1, port = 21];");
        assertThat(lines.next()).isEqualTo(" nom = name, adresse = [ip = 127.0.0.1, port = 8080];]");
        assertThat(lines.hasNext()).isFalse();
    }

    @Test
    public void should_initialize_a_Pdm_with_a_bye_message() {
        final ListePdm marketPlaces = new ListePdm(
                (ResultBye) Protocole
                        .newInstance("<MSG><RESULTBYE><PDM NOM=\"jhlfdhsl\" ADRESSE=\"192.168.1.1:24\"/></RESULTBYE></MSG>"));
        assertThat(marketPlaces.estVide()).isFalse();

        final Pdm marketPlace = marketPlaces.acceder("jhlfdhsl");

        assertThat(marketPlace).isNotNull();
        assertThat(marketPlace.getAdresse().ipToString()).isEqualTo("192.168.1.1");
        assertThat(marketPlace.getAdresse().getPort()).isEqualTo(24);
    }

}
