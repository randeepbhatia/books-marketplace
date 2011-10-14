package bourse.sdd;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public class PDMProTest {

    @Test
    public void should_create_a_default_PDMPro() {
        PDMPro pdmPro = new PDMPro();
        assertThat(pdmPro.getNom()).isNull();
        assertThat(pdmPro.getAdresse()).isNull();
    }

    @Test
    public void should_create_a_PDMPro() {
        PDMPro pdmPro = new PDMPro("myPDMPro", "myAddress");
        assertThat(pdmPro.getNom()).isEqualTo("myPDMPro");
        assertThat(pdmPro.getAdresse()).isEqualTo("myAddress");
    }

}
