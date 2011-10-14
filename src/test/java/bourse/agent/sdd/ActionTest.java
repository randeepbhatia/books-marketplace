package bourse.agent.sdd;

import static bourse.agent.sdd.Action.adversaires;
import static bourse.agent.sdd.Action.attenteEnchere;
import static bourse.agent.sdd.Action.aucune;
import static bourse.agent.sdd.Action.bilan;
import static bourse.agent.sdd.Action.migrer;
import static bourse.agent.sdd.Action.objectif;
import static bourse.agent.sdd.Action.programme;
import static bourse.agent.sdd.Action.vendre;
import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public class ActionTest {

    @Test
    public void should_print_action_name() {
        assertThat(aucune.toString()).isEqualTo("aucune action séléctionnée");
        assertThat(vendre.toString()).isEqualTo("vendre son bouquin");
        assertThat(migrer.toString()).isEqualTo("migration");
        assertThat(bilan.toString()).isEqualTo("effectuer son bilan");
        assertThat(programme.toString()).isEqualTo("demande de programme");
        assertThat(adversaires.toString()).isEqualTo("demande de la liste des agents présents");
        assertThat(attenteEnchere.toString()).isEqualTo("attente d'une proposition enchère");
        assertThat(objectif.toString()).isEqualTo("réaliser l'objectif");
    }

}
