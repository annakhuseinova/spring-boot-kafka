package com.annakhuseinova.apachekafka.producer;

import com.annakhuseinova.apachekafka.model.Book;
import com.annakhuseinova.apachekafka.model.LibraryEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.concurrent.SettableListenableFuture;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LibraryEventProducerTest {

    @Mock
    private KafkaTemplate<Integer, String> kafkaTemplate;

    @Spy
    ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private LibraryEventProducer libraryEventProducer;

    @Test
    void sendLibraryEvent_Approach2_failure() throws JsonProcessingException {
        // given
        Book book = Book.builder().bookId(123L).bookAuthor("SomeAuthor").bookName("SomeName").build();
        LibraryEvent libraryEvent = LibraryEvent.builder().libraryEventId(null).book(book).build();
        // Класс для создания
        SettableListenableFuture future = new SettableListenableFuture();
        future.setException(new RuntimeException("Exception Calling Kafka"));
        // when
        when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);
        assertThrows(Exception.class, ()-> {
            libraryEventProducer.sendLibraryEventApproach2(libraryEvent);
        });
    }
}