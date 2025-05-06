package com.db.clm.exercise.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import com.opencsv.bean.CsvBindByName;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "nace")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Nace {

    @Id
    @CsvBindByName(column = "Order")
    @Column(name = "ORDER_ID")
    @JsonProperty("order")
    private String order;

    @CsvBindByName(column = "Level")
    @Column(name = "LEVEL")
    @JsonProperty("level")
    private int level;

    @CsvBindByName(column = "Code")
    @Column(name = "CODE")
    @JsonProperty("code")
    private String code;

    @CsvBindByName(column = "Parent")
    @Column(name = "PARENT")
    @JsonProperty("parent")
    private String parent;

    @CsvBindByName(column = "Description")
    @Column(name = "DESCR")
    @JsonProperty("description")
    private String description;

    @CsvBindByName(column = "This item includes")
    @Column(name = "INCL")
    @JsonProperty("includes")
    private String includes;

    @CsvBindByName(column = "This item also includes")
    @Column(name = "ALSO_INCL")
    @JsonProperty("also-includes")
    private String alsoIncludes;

    @CsvBindByName(column = "This item excludes")
    @Column(name = "EXCL")
    @JsonProperty("excludes")
    private String excludes;

    @CsvBindByName(column = "Rulings")
    @Column(name = "RULINGS")
    @JsonProperty("rulings")
    private String rulings;

    @CsvBindByName(column = "Reference to ISIC Rev. 4")
    @Column(name = "REF_ISIC")
    @JsonProperty("ref-isic-rev-4")
    private String reference;

}
