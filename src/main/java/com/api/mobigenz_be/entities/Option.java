package com.api.mobigenz_be.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "options")
public class Option{
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "option_name", nullable = false, length = 100)
    private String optionName;

    @Lob
    @Column(name = "note")
    private String note;

    @Column(name = "ctime", nullable = false)
    private LocalDateTime ctime;

    @Column(name = "mtime")
    private LocalDateTime mtime;

    @Column(name = "status")
    private Integer status;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id")
    private List<OptionsValue> optionsValues;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id")
    private List<ProductsOption> productsOptions;

}