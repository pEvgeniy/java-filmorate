package ru.yandex.practicum.filmorate.dao.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.MpaDbStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MpaDbStorageTest {

    private final MpaDbStorage mpaDbStorage;

    @Test
    public void findAll() {
        List<Mpa> mpa = mpaDbStorage.findAll();
        assertThat(mpa)
                .isNotEmpty()
                .hasSize(5);
    }

    @Test
    public void findMpaById1() {
        Optional<Mpa> mpa = Optional.of(mpaDbStorage.findMpaById(1));
        assertThat(mpa)
                .isPresent()
                .hasValueSatisfying(m ->
                    assertThat(m).hasFieldOrPropertyWithValue("name", "G")
                );
    }

    @Test
    public void findMpaById5() {
        Optional<Mpa> mpa = Optional.of(mpaDbStorage.findMpaById(5));
        assertThat(mpa)
                .isPresent()
                .hasValueSatisfying(m ->
                        assertThat(m).hasFieldOrPropertyWithValue("name", "NC-17")
                );
    }
}
