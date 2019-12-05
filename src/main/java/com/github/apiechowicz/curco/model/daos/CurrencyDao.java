package com.github.apiechowicz.curco.model.daos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = CurrencyDao.TABLE_NAME)
public class CurrencyDao implements Dao {

    public static final String TABLE_NAME = "currencies";

    @Id
    @GeneratedValue
    private Long id;

    @Size(min = 3, max = 3)
    @Column(unique = true, nullable = false)
    private String codeName;

    @Column(nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApiTable apiTable;

    public CurrencyDao(String codeName, String fullName, ApiTable apiTable) {
        this.codeName = codeName;
        this.fullName = fullName;
        this.apiTable = apiTable;
    }
}
