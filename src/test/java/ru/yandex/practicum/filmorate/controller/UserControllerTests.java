package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldReturn200IfUserIsOk() throws Exception {
        mockMvc.perform(post("/users")
                .content(
            "{\"login\":\"dolore\",\"name\":\"NickName\",\"email\":\"mail@mail.ru\",\"birthday\":\"1946-08-20\"}"
                ).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void shouldReturn400IfLoginContainsSpaceCharacters() throws Exception {
        mockMvc.perform(post("/users")
                .content(
            "{\"login\":\"doloreullamco\",\"email\":\"yandex@mail.ru\",\"birthday\":\"2446-08-20\"}"
                ).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturn400IfEmailIsIncorrect() throws Exception {
        mockMvc.perform(post("/users")
                .content(
            "{\"login\":\"doloreullamco\",\"name\":\"\",\"email\":\"mail.ru\",\"birthday\":\"1980-08-20\"}"
                ).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturn400IfBirthdayInFuture() throws Exception {
        mockMvc.perform(post("/users")
                .content(
            "{\"login\":\"dolore\",\"name\":\"\",\"email\":\"test@mail.ru\",\"birthday\":\"2446-08-20\"}"
                ).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturn200IfBirthdayIsToday() throws Exception {
        final LocalDate today = LocalDate.now();
        final String todayString = today.toString();

        mockMvc.perform(post("/users")
                .content(String.format(
            "{\"login\":\"dolore\",\"name\":\"\",\"email\":\"test@mail.ru\",\"birthday\":\"%s\"}", todayString
                )).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }
}
