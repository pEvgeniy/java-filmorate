package ru.yandex.practicum.filmorate.dao.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenreStorageImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class GenreStorageImplTest {

    private final GenreStorageImpl genreDbStorage;

    @Test
    public void findAll() {
        List<Genre> genres = genreDbStorage.findAll();
        assertThat(genres)
                .isNotEmpty()
                .hasSize(6);
    }

    @Test
    public void findGenreById1() {
        Optional<Genre> genre = Optional.of(genreDbStorage.findGenreById(1));
        assertThat(genre)
                .isPresent()
                .hasValueSatisfying(g ->
                    assertThat(g).hasFieldOrPropertyWithValue("name", "Комедия")
                );
    }

    @Test
    public void findGenreById6() {
        Optional<Genre> genre = Optional.of(genreDbStorage.findGenreById(6));
        assertThat(genre)
                .isPresent()
                .hasValueSatisfying(g ->
                        assertThat(g).hasFieldOrPropertyWithValue("name", "Боевик")
                );
    }
}
