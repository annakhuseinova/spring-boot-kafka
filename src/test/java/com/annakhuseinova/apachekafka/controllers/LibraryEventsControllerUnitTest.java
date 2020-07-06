package com.annakhuseinova.apachekafka.controllers;

import com.annakhuseinova.apachekafka.model.Book;
import com.annakhuseinova.apachekafka.model.LibraryEvent;
import com.annakhuseinova.apachekafka.producer.LibraryEventProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LibraryEventsController.class)
@AutoConfigureMockMvc
public class LibraryEventsControllerUnitTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();
    @MockBean
    private LibraryEventProducer libraryEventProducer;

    @AutoConfigureMockMvc
    public void setMockMvc(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void postLibraryEvent() throws Exception {
        // given
        Book book = Book.builder().bookId(123L).bookAuthor("SomeAuthor").bookName("SomeName").build();
        LibraryEvent libraryEvent = LibraryEvent.builder().libraryEventId(null).book(book).build();
        doNothing().when(libraryEventProducer).sendLibraryEventApproach2(isA(LibraryEvent.class));

        // when
        mockMvc.perform(post("/v1/libraryevent").content(objectMapper.writeValueAsString(libraryEvent))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());

    }

    @Test
    void postLibraryEvent_4xx() throws Exception {

        // given
        Book book = Book.builder()
                .bookId(null)
                .bookAuthor(null)
                .bookName("Kafka using Spring Boot")
                .build();
        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(null)
                .book(book)
                .build();

        String json = objectMapper.writeValueAsString(libraryEvent);
        doNothing().when(libraryEventProducer).sendLibraryEventApproach2(isA(LibraryEvent.class));

        // expect
        String expectedErrorMessage = "book.bookAuthor - must not be blank, book.bookId - must not be null";
        this.mockMvc.perform(post("/v1/libraryevent")
        .content(json)
        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is4xxClientError())
                // Полезный способ content().string(...) - проверить содержание строки в ответе. Альтернатива - jsonPath
                .andExpect(content().string(expectedErrorMessage));
    }
}
