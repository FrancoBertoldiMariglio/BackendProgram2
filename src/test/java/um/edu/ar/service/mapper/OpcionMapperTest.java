package um.edu.ar.service.mapper;

import static um.edu.ar.domain.OpcionAsserts.*;
import static um.edu.ar.domain.OpcionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OpcionMapperTest {

    private OpcionMapper opcionMapper;

    @BeforeEach
    void setUp() {
        opcionMapper = new OpcionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getOpcionSample1();
        var actual = opcionMapper.toEntity(opcionMapper.toDto(expected));
        assertOpcionAllPropertiesEquals(expected, actual);
    }
}
