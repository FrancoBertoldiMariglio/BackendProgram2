package um.edu.ar.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import um.edu.ar.web.rest.TestUtil;

class CaracteristicaDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CaracteristicaDTO.class);
        CaracteristicaDTO caracteristicaDTO1 = new CaracteristicaDTO();
        caracteristicaDTO1.setId(1L);
        CaracteristicaDTO caracteristicaDTO2 = new CaracteristicaDTO();
        assertThat(caracteristicaDTO1).isNotEqualTo(caracteristicaDTO2);
        caracteristicaDTO2.setId(caracteristicaDTO1.getId());
        assertThat(caracteristicaDTO1).isEqualTo(caracteristicaDTO2);
        caracteristicaDTO2.setId(2L);
        assertThat(caracteristicaDTO1).isNotEqualTo(caracteristicaDTO2);
        caracteristicaDTO1.setId(null);
        assertThat(caracteristicaDTO1).isNotEqualTo(caracteristicaDTO2);
    }
}
