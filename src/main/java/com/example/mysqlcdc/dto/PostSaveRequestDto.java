package com.example.mysqlcdc.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author gunha
 * @version 0.1
 * @since 2022-12-19 오전 10:39
 */
@Getter
@NoArgsConstructor
public class PostSaveRequestDto {

    private Integer id;
    private String name;

    @Builder
    public PostSaveRequestDto(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
