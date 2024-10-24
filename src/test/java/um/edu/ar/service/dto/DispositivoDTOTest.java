package um.edu.ar.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import um.edu.ar.web.rest.TestUtil;

class DispositivoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DispositivoDTO.class);
        DispositivoDTO dispositivoDTO1 = new DispositivoDTO();
        dispositivoDTO1.setId(1L);
        DispositivoDTO dispositivoDTO2 = new DispositivoDTO();
        assertThat(dispositivoDTO1).isNotEqualTo(dispositivoDTO2);
        dispositivoDTO2.setId(dispositivoDTO1.getId());
        assertThat(dispositivoDTO1).isEqualTo(dispositivoDTO2);
        dispositivoDTO2.setId(2L);
        assertThat(dispositivoDTO1).isNotEqualTo(dispositivoDTO2);
        dispositivoDTO1.setId(null);
        assertThat(dispositivoDTO1).isNotEqualTo(dispositivoDTO2);
    }
}
