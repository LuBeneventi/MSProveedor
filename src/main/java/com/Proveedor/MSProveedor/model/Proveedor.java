package com.Proveedor.MSProveedor.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor 

@Entity
public class Proveedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idProveedor;

    @Column(length = 50, nullable = false)
    private String nomProv;

    @Column(length = 250, nullable = false, unique = true)
    private String correoProv;

    @Column(length = 12, nullable = false)
    private String telProv;

    @Column(length = 50, nullable = false)
    private String dirProv;

}
