package um.edu.ar.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static um.edu.ar.domain.DispositivoTestSamples.*;
import static um.edu.ar.domain.OpcionTestSamples.*;
import static um.edu.ar.domain.PersonalizacionTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import um.edu.ar.web.rest.TestUtil;

class PersonalizacionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Personalizacion.class);
        Personalizacion personalizacion1 = getPersonalizacionSample1();
        Personalizacion personalizacion2 = new Personalizacion();
        assertThat(personalizacion1).isNotEqualTo(personalizacion2);

        personalizacion2.setId(personalizacion1.getId());
        assertThat(personalizacion1).isEqualTo(personalizacion2);

        personalizacion2 = getPersonalizacionSample2();
        assertThat(personalizacion1).isNotEqualTo(personalizacion2);
    }

    @Test
    void opcionesTest() {
        Personalizacion personalizacion = getPersonalizacionRandomSampleGenerator();
        Opcion opcionBack = getOpcionRandomSampleGenerator();

        personalizacion.addOpciones(opcionBack);
        assertThat(personalizacion.getOpciones()).containsOnly(opcionBack);
        assertThat(opcionBack.getPersonalizacion()).isEqualTo(personalizacion);

        personalizacion.removeOpciones(opcionBack);
        assertThat(personalizacion.getOpciones()).doesNotContain(opcionBack);
        assertThat(opcionBack.getPersonalizacion()).isNull();

        personalizacion.opciones(new HashSet<>(Set.of(opcionBack)));
        assertThat(personalizacion.getOpciones()).containsOnly(opcionBack);
        assertThat(opcionBack.getPersonalizacion()).isEqualTo(personalizacion);

        personalizacion.setOpciones(new HashSet<>());
        assertThat(personalizacion.getOpciones()).doesNotContain(opcionBack);
        assertThat(opcionBack.getPersonalizacion()).isNull();
    }

    @Test
    void dispositivoTest() {
        Personalizacion personalizacion = getPersonalizacionRandomSampleGenerator();
        Dispositivo dispositivoBack = getDispositivoRandomSampleGenerator();

        personalizacion.setDispositivo(dispositivoBack);
        assertThat(personalizacion.getDispositivo()).isEqualTo(dispositivoBack);

        personalizacion.dispositivo(null);
        assertThat(personalizacion.getDispositivo()).isNull();
    }
}
