package com.annakhuseinova.apachekafka.producer;

import com.annakhuseinova.apachekafka.model.Book;
import com.annakhuseinova.apachekafka.model.LibraryEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.network.Send;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.refEq;
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

    @Test
    void sendLibraryEvent_Approach2_success() throws JsonProcessingException, ExecutionException, InterruptedException {
        // given
        Book book = Book.builder()
                .bookId(null)
                .bookName()
                .bookAuthor()
                .build();
        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(null)
                .book(book)
                .build();
        SettableListenableFuture future = new SettableListenableFuture();
        ProducerRecord<Integer, String> producerRecord = new ProducerRecord("libraryEvents",
                libraryEvent.getLibraryEventId(), libraryEvent.getBook());
        RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition("libraryEvents",
                1), 1,1,342,System.currentTimeMillis(), 1,
                2);
        SendResult<Integer, String> result = new SendResult<>(producerRecord, recordMetadata);
        future.set(result);
        when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);
        ListenableFuture<SendResult<Integer, String>> listenableFuture = libraryEventProducer.sendLibraryEventApproach2(libraryEvent);
        SendResult<Integer, String> sendResult = listenableFuture.get();
        assert sendResult.getRecordMetadata().partition() == 1;
    }
}