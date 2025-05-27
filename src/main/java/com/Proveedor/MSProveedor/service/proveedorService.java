package com.Proveedor.MSProveedor.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.Proveedor.MSProveedor.model.Proveedor;
import com.Proveedor.MSProveedor.model.estadoProveedor;
import com.Proveedor.MSProveedor.repository.proveedorRepository;

@Service
public class proveedorService {
    
    @Autowired
    private proveedorRepository proveedorRepository;

    public Proveedor registrarProveedor(Proveedor proveedor) {
        Proveedor existente = proveedorRepository.findByCorreoProv(proveedor.getCorreoProv());
        if(existente != null){
           new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        proveedor.setEstado(estadoProveedor.ACTIVO);
        return proveedorRepository.save(proveedor);
    }

    public Proveedor actualizarInfo(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }

    public Proveedor activaProveedor(int id){
        Proveedor buscado = proveedorRepository.findById(id).orElseThrow();
        if(buscado.getEstado() == estadoProveedor.INACTIVO){
            buscado.setEstado(estadoProveedor.ACTIVO);
            return proveedorRepository.save(buscado);
        }
        throw new IllegalStateException("Es proveedor ya esta activo");
    }

    public Proveedor desactivaProveedor(int id){
        Proveedor buscado = proveedorRepository.findById(id).orElseThrow();
        if(buscado.getEstado() == estadoProveedor.ACTIVO){
            buscado.setEstado(estadoProveedor.INACTIVO);
            return proveedorRepository.save(buscado);
        }
        throw new IllegalStateException("Es proveedor ya esta inactivo");
    }

    public Optional<Proveedor> buscarProveedor(int id) {
        return proveedorRepository.findById(id);
    }

    public List<Proveedor> listarProveedores(){
        return proveedorRepository.findAll();
    }

    public boolean existePorId(int id) {
        return proveedorRepository.existsById(id);
    }
}
