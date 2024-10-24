package um.edu.ar.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static um.edu.ar.domain.AdicionalTestSamples.*;
import static um.edu.ar.domain.CaracteristicaTestSamples.*;
import static um.edu.ar.domain.DispositivoTestSamples.*;
import static um.edu.ar.domain.PersonalizacionTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import um.edu.ar.web.rest.TestUtil;

class DispositivoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Dispositivo.class);
        Dispositivo dispositivo1 = getDispositivoSample1();
        Dispositivo dispositivo2 = new Dispositivo();
        assertThat(dispositivo1).isNotEqualTo(dispositivo2);

        dispositivo2.setId(dispositivo1.getId());
        assertThat(dispositivo1).isEqualTo(dispositivo2);

        dispositivo2 = getDispositivoSample2();
        assertThat(dispositivo1).isNotEqualTo(dispositivo2);
    }

    @Test
    void caracteristicasTest() {
        Dispositivo dispositivo = getDispositivoRandomSampleGenerator();
        Caracteristica caracteristicaBack = getCaracteristicaRandomSampleGenerator();

        dispositivo.addCaracteristicas(caracteristicaBack);
        assertThat(dispositivo.getCaracteristicas()).containsOnly(caracteristicaBack);
        assertThat(caracteristicaBack.getDispositivo()).isEqualTo(dispositivo);

        dispositivo.removeCaracteristicas(caracteristicaBack);
        assertThat(dispositivo.getCaracteristicas()).doesNotContain(caracteristicaBack);
        assertThat(caracteristicaBack.getDispositivo()).isNull();

        dispositivo.caracteristicas(new HashSet<>(Set.of(caracteristicaBack)));
        assertThat(dispositivo.getCaracteristicas()).containsOnly(caracteristicaBack);
        assertThat(caracteristicaBack.getDispositivo()).isEqualTo(dispositivo);

        dispositivo.setCaracteristicas(new HashSet<>());
        assertThat(dispositivo.getCaracteristicas()).doesNotContain(caracteristicaBack);
        assertThat(caracteristicaBack.getDispositivo()).isNull();
    }

    @Test
    void personalizacionesTest() {
        Dispositivo dispositivo = getDispositivoRandomSampleGenerator();
        Personalizacion personalizacionBack = getPersonalizacionRandomSampleGenerator();

        dispositivo.addPersonalizaciones(personalizacionBack);
        assertThat(dispositivo.getPersonalizaciones()).containsOnly(personalizacionBack);
        assertThat(personalizacionBack.getDispositivo()).isEqualTo(dispositivo);

        dispositivo.removePersonalizaciones(personalizacionBack);
        assertThat(dispositivo.getPersonalizaciones()).doesNotContain(personalizacionBack);
        assertThat(personalizacionBack.getDispositivo()).isNull();

        dispositivo.personalizaciones(new HashSet<>(Set.of(personalizacionBack)));
        assertThat(dispositivo.getPersonalizaciones()).containsOnly(personalizacionBack);
        assertThat(personalizacionBack.getDispositivo()).isEqualTo(dispositivo);

        dispositivo.setPersonalizaciones(new HashSet<>());
        assertThat(dispositivo.getPersonalizaciones()).doesNotContain(personalizacionBack);
        assertThat(personalizacionBack.getDispositivo()).isNull();
    }

    @Test
    void adicionalesTest() {
        Dispositivo dispositivo = getDispositivoRandomSampleGenerator();
        Adicional adicionalBack = getAdicionalRandomSampleGenerator();

        dispositivo.addAdicionales(adicionalBack);
        assertThat(dispositivo.getAdicionales()).containsOnly(adicionalBack);

        dispositivo.removeAdicionales(adicionalBack);
        assertThat(dispositivo.getAdicionales()).doesNotContain(adicionalBack);

        dispositivo.adicionales(new HashSet<>(Set.of(adicionalBack)));
        assertThat(dispositivo.getAdicionales()).containsOnly(adicionalBack);

        dispositivo.setAdicionales(new HashSet<>());
        assertThat(dispositivo.getAdicionales()).doesNotContain(adicionalBack);
    }
}
