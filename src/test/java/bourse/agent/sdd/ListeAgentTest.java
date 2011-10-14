package bourse.agent.sdd;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class ListeAgentTest {

    private ListeAgent listeAgent;

    @Mock
    private Agent agent;

    @Before
    public void create_liste_agent() {
        listeAgent = new ListeAgent();
    }

    @Test
    public void should_create_liste_agent() {
        assertThat(listeAgent.toString(4)).isEqualTo("");
    }

    @Test
    public void should_add_and_retrieve_agent() {
        listeAgent.ajouter(agent);

        assertThat(listeAgent.valeurs()).containsOnly(agent);
    }

    @Test
    public void should_expose_as_simple_string() {
        Agent agent = new Agent("junit agent");

        listeAgent.ajouter(agent);

        assertThat(listeAgent.toString(3)).isEqualTo("   nom = junit agent");
    }

    @Test
    public void should_find_added_agent() {
        when(agent.getNom()).thenReturn("junit agent");
        listeAgent.ajouter(agent);

        assertThat(listeAgent.contient("junit agent")).isTrue();
    }

}
