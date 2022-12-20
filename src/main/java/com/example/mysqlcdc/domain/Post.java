package com.example.mysqlcdc.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author gunha
 * @version 0.1
 * @since 2022-12-19 오전 10:31
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post", schema = "test")
@Entity
public class Post {

    private Integer id;
    private String name;

}
