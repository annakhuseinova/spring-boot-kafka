package com.annakhuseinova.apachekafka.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Book {

    private Long bookId;
    @NotBlank
    private String bookName;
    @NotBlank
    private String bookAuthor;
}
