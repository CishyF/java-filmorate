package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class FilmControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldReturn200IfFilmIsOk() throws Exception {
        mockMvc.perform(post("/films")
                .content(
            "{\"name\":\"nisieiusmod\",\"description\":\"adipisicing\",\"releaseDate\":\"1967-03-25\"," +
            "\"duration\":100,\"rate\":4,\"mpa\":{\"id\":1}}"
                ).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void shouldReturn400IfNameIsBlank() throws Exception {
        mockMvc.perform(post("/films")
                .content(
            "{\"name\":\"\",\"description\":\"Description\",\"releaseDate\":\"1900-03-25\",\"duration\":200}"
                ).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturn400IfDescriptionContainsMoreThan200Symbols() throws Exception {
        mockMvc.perform(post("/films")
                .content(
            "{\"name\":\"Filmname\",\"description\":\"Пятеродрузей(комик-группа«Шарло»),приезжаютвгородБризуль.Здесьо" +
            "нихотятразыскатьгосподинаОгюстаКуглова,которыйзадолжалимденьги,аименно20миллионов.оКуглов,которыйзавремя" +
            "«своегоотсутствия»,сталкандидатомКоломбани.\",\"releaseDate\":\"1900-03-25\",\"duration\":200}"
                ).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturn400IfReleaseDateBefore28December1895() throws Exception {
        mockMvc.perform(post("/films")
                .content(
            "{\"name\":\"Name\",\"description\":\"Description\",\"releaseDate\":\"1895-12-27\",\"duration\":200}"
                ).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturn200IfReleaseDateIs28December1895() throws Exception {
        mockMvc.perform(post("/films")
                .content(
            "{\"name\":\"Name\",\"description\":\"Description\",\"releaseDate\":\"1895-12-28\"," +
            "\"duration\":100,\"rate\":4,\"mpa\":{\"id\":1}}"
                ).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void shouldReturn400IfDurationIsNegative() throws Exception {
        mockMvc.perform(post("/films")
                .content(
            "{\"name\":\"Name\",\"description\":\"Descrition\",\"releaseDate\":\"1980-03-25\",\"duration\":-1}"
                ).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturn400IfDurationIsZero() throws Exception {
        mockMvc.perform(post("/films")
                .content(
            "{\"name\":\"Name\",\"description\":\"Descrition\",\"releaseDate\":\"1980-03-25\",\"duration\":0}"
                ).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturn200IfDurationIsPositive() throws Exception {
        mockMvc.perform(post("/films")
                .content(
            "{\"name\":\"Name\",\"description\":\"Descrition\",\"releaseDate\":\"1980-03-25\"," +
            "\"duration\":100,\"rate\":4,\"mpa\":{\"id\":1}}"
                ).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }
}
