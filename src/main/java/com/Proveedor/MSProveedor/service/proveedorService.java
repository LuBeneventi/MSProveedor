package com.Proveedor.MSProveedor.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
        if (existente != null) {
            throw new IllegalStateException("Correo ya registrado");
        }
        proveedor.setEstado(estadoProveedor.ACTIVO);
        return proveedorRepository.save(proveedor);
    }

    public Proveedor actualizarInfo(int id, Proveedor nuevo) {
        Proveedor existente = proveedorRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Proveedor no encontrado"));

        Proveedor conCorreo = proveedorRepository.findByCorreoProv(nuevo.getCorreoProv());
        if (conCorreo != null && conCorreo.getIdProveedor() != id) {
            throw new IllegalStateException("El correo ya está registrado por otro proveedor.");
        }

        existente.setCorreoProv(nuevo.getCorreoProv());
        existente.setNomProv(nuevo.getNomProv());
        existente.setTelProv(nuevo.getTelProv());
        existente.setDirProv(nuevo.getDirProv());

        return proveedorRepository.save(existente);
    }

    public Proveedor activaProveedor(int id) {
        Proveedor buscado = proveedorRepository.findById(id).orElseThrow();
        if (buscado.getEstado() == estadoProveedor.INACTIVO) {
            buscado.setEstado(estadoProveedor.ACTIVO);
            return proveedorRepository.save(buscado);
        }
        throw new IllegalStateException("Este proveedor ya esta activo");
    }

    public Proveedor desactivaProveedor(int id) {
        Proveedor buscado = proveedorRepository.findById(id).orElseThrow();
        if (buscado.getEstado() == estadoProveedor.ACTIVO) {
            buscado.setEstado(estadoProveedor.INACTIVO);
            return proveedorRepository.save(buscado);
        }
        throw new IllegalStateException("Este proveedor ya esta inactivo");
    }

    public Optional<Proveedor> buscarProveedor(int id) {
        return proveedorRepository.findById(id);
    }

    public List<Proveedor> listarProveedores() {
        return proveedorRepository.findAll();
    }

    public boolean existePorId(int id) {
        return proveedorRepository.existsById(id);
    }
}
