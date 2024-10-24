package um.edu.ar.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static um.edu.ar.domain.VentaTestSamples.*;

import org.junit.jupiter.api.Test;
import um.edu.ar.web.rest.TestUtil;

class VentaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Venta.class);
        Venta venta1 = getVentaSample1();
        Venta venta2 = new Venta();
        assertThat(venta1).isNotEqualTo(venta2);

        venta2.setId(venta1.getId());
        assertThat(venta1).isEqualTo(venta2);

        venta2 = getVentaSample2();
        assertThat(venta1).isNotEqualTo(venta2);
    }
}
