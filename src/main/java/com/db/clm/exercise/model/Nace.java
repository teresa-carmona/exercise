package com.db.clm.exercise.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "nace")
@Data
public class Nace {

    @Id
    @Column(name = "ORDER_ID")
    @JsonProperty("order")
    private int order;

    @Column(name = "LEVEL")
    @JsonProperty("level")
    @Nullable
    private int level;

    @Column(name = "CODE")
    @JsonProperty("code")
    @Nullable
    private String code;

    @Column(name = "PARENT")
    @JsonProperty("parent")
    @Nullable
    private String parent;

    @Column(name = "DESCR")
    @JsonProperty("description")
    @Nullable
    private String description;

    @Column(name = "INCL", columnDefinition = "TEXT")
    @JsonProperty("includes")
    @Nullable
    private String includes;

    @Column(name = "ALSO_INCL", columnDefinition = "TEXT")
    @JsonProperty("also-includes")
    @Nullable
    private String alsoIncludes;

    @Column(name = "EXCL", columnDefinition = "TEXT")
    @JsonProperty("excludes")
    @Nullable
    private String excludes;

    @Column(name = "RULINGS", columnDefinition = "TEXT")
    @JsonProperty("rulings")
    @Nullable
    private String rulings;

    @Column(name = "REF_ISIC")
    @JsonProperty("ref-isic-rev-4")
    @Nullable
    private String reference;

}
