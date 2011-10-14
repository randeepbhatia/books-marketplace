package bourse.agent;

import static com.google.common.collect.Iterators.forArray;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.sql.ResultSet;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import bourse.agent.sdd.Action;
import bourse.agent.sdd.Etat;
import bourse.protocole.Categorie;

@RunWith(PowerMockRunner.class)
public class AgentTest {

    private Visualisation window;
    private RequetesAgent requetesAgent;

    @Before
    public void initMocks() throws Exception {
        window = mock(Visualisation.class);
        whenNew(Visualisation.class).withNoArguments().thenReturn(window);
        requetesAgent = mock(RequetesAgent.class);
        whenNew(RequetesAgent.class).withArguments(Boolean.TRUE).thenReturn(requetesAgent);
        whenNew(RequetesAgent.class).withArguments(Boolean.FALSE).thenReturn(requetesAgent);
    }

    @Test
    @PrepareForTest(Agent.class)
    public final void creates_new_agent_should_set_an_initial_state() throws Exception {
        Agent agent = new Agent("test");

        assertThat(agent.getEtat()).isEqualTo(Etat.initial);
        assertThat(agent.getCurrentPdm()).isNull();
        assertThat(agent.getWallet()).isEqualTo(0);
        assertThat(agent.getCategorie().getCode()).isEqualTo(new Categorie(Categorie.AUCUNE).getCode());
        assertThat(agent.getAction()).isEqualTo(Action.aucune);
        assertThat(agent.getEnvironnement()).isNotNull();
        assertThat(agent.getMemoire()).isNotNull();
        assertThat(agent.getNom()).isEqualTo("Groupe-E-test");
        assertThat(agent.getFenetre()).isSameAs(window);
        assertThat(agent.getDecision()).isNull();
    }

    @Test
    @PrepareForTest(Agent.class)
    public void agent_should_display_comprehensive_toString() throws Exception {
        Agent agent = new Agent("test");

        agent.setEtatSuivant(Etat.actionChoisie);
        agent.setHote("localhost");
        agent.setVerbose(false);

        final String[] agentString = agent.toString(1).split("\n");
        Iterator<String> lines = forArray(agentString);
        assertThat(lines.next()).isEqualTo(" Nom = Groupe-E-test");
        assertThat(lines.next()).isEqualTo(" Solde = 0.0");
        assertThat(lines.next()).isEqualTo(" Decision = null");
        assertThat(lines.next()).isEqualTo(" Catégorie = 5 (Aucune)");
        assertThat(lines.next()).isEqualTo(" Etat = 1 (initialisé)");
        assertThat(lines.next()).isEqualTo(" Action = aucune action séléctionnée");
        assertThat(lines.next()).isEqualTo(" Memoire =");
        assertThat(lines.next()).isEqualTo("  pdms :");
        assertThat(lines.next()).isEqualTo("");
        assertThat(lines.next()).isEqualTo("  agents :");
        assertThat(lines.next()).isEqualTo("");
        assertThat(lines.next()).isEqualTo("  possessions :");
        assertThat(lines.next()).isEqualTo("");
        assertThat(lines.next()).isEqualTo("  temps :");
        assertThat(lines.next()).isEqualTo("0");
        assertThat(lines.next()).isEqualTo(" Environnement =");
        assertThat(lines.next()).isEqualTo("  enchère =");
        assertThat(lines.next())
                .isEqualTo(
                        "   tour = 1, type = 0, temps = 0, pas = 0.0, enchérisseur = null, prix = 0.0, prix maximum = 0.0, livre = ");
        assertThat(lines.next()).startsWith("    null");
        assertThat(lines.next())
                .isEqualTo(
                        "  typeEnchèreDemandée = 0   nombreActions = 0   dateListeAgent = 9223372036854775807   dateListeProgramme = 9223372036854775807   enchèreInteressante = false");
        assertThat(lines.next()).isEqualTo(" Hote = localhost");
    }

    @Test
    @PrepareForTest(Agent.class)
    public void should_show_results() throws Exception {
        final ResultSet resultPerBook = mock(ResultSet.class);
        when(resultPerBook.next()).thenReturn(true, true, false);
        when(resultPerBook.getString("argent")).thenReturn("34.9");
        when(resultPerBook.getString("titre")).thenReturn("titre1", "titre2");
        when(resultPerBook.getString("id")).thenReturn("id1", "id2");
        when(resultPerBook.getFloat("points")).thenReturn(8.4f, 8.4f, 4f, 4f);
        when(resultPerBook.getString("categorie")).thenReturn("A", "B");
        when(requetesAgent.getResultPerBook("Groupe-E-nom")).thenReturn(resultPerBook);

        Agent agent = new Agent("nom");
        agent.setEtat(Etat.pret);

        agent.showResults();

        final StringBuilder output = new StringBuilder();
        output.append("Argent = 34.9\n");
        output.append("Titre = titre1 Id = id1 Points = 8.4 Catégorie = A\n");
        output.append("Titre = titre2 Id = id2 Points = 4.0 Catégorie = B\n");
        output.append("Total des points = 12.4");

        verify(window).setResultat(output.toString());
    }

}
