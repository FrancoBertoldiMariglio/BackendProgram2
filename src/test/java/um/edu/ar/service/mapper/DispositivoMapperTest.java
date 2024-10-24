package um.edu.ar.service.mapper;

import static um.edu.ar.domain.DispositivoAsserts.*;
import static um.edu.ar.domain.DispositivoTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DispositivoMapperTest {

    private DispositivoMapper dispositivoMapper;

    @BeforeEach
    void setUp() {
        dispositivoMapper = new DispositivoMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDispositivoSample1();
        var actual = dispositivoMapper.toEntity(dispositivoMapper.toDto(expected));
        assertDispositivoAllPropertiesEquals(expected, actual);
    }
}
