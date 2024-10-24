package um.edu.ar.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import um.edu.ar.web.rest.TestUtil;

class PersonalizacionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PersonalizacionDTO.class);
        PersonalizacionDTO personalizacionDTO1 = new PersonalizacionDTO();
        personalizacionDTO1.setId(1L);
        PersonalizacionDTO personalizacionDTO2 = new PersonalizacionDTO();
        assertThat(personalizacionDTO1).isNotEqualTo(personalizacionDTO2);
        personalizacionDTO2.setId(personalizacionDTO1.getId());
        assertThat(personalizacionDTO1).isEqualTo(personalizacionDTO2);
        personalizacionDTO2.setId(2L);
        assertThat(personalizacionDTO1).isNotEqualTo(personalizacionDTO2);
        personalizacionDTO1.setId(null);
        assertThat(personalizacionDTO1).isNotEqualTo(personalizacionDTO2);
    }
}
