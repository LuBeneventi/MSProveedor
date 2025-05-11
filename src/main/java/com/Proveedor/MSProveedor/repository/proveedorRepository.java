package com.Proveedor.MSProveedor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Proveedor.MSProveedor.model.Proveedor;

@Repository
public interface proveedorRepository extends JpaRepository<Proveedor, Integer>{
    
}
