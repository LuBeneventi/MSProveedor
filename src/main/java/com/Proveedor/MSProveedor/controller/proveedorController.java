package com.Proveedor.MSProveedor.controller;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import java.util.stream.Collectors;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Proveedor.MSProveedor.model.Proveedor;
import com.Proveedor.MSProveedor.model.estadoProveedor;
import com.Proveedor.MSProveedor.service.proveedorService;

@RestController
@RequestMapping("api/v1/proveedor")
public class proveedorController {

    @Autowired
    private proveedorService proveedorService;

    @PostMapping
    public ResponseEntity<Proveedor> registrar(@RequestBody Proveedor proveedor) {
        if (proveedor.getIdProveedor() != 0 && proveedorService.existePorId(proveedor.getIdProveedor())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        proveedor.setEstado(estadoProveedor.ACTIVO);
        return new ResponseEntity<>(proveedorService.registrarProveedor(proveedor), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Proveedor>>> listar() {
        List<Proveedor> proveedores = proveedorService.listarProveedores();
    if (proveedores.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    List<EntityModel<Proveedor>> proveedoresConLinks = proveedores.stream()
        .map(proveedor -> {
            EntityModel<Proveedor> recurso = EntityModel.of(proveedor);

            recurso.add(linkTo(methodOn(proveedorController.class).obtener(proveedor.getIdProveedor())).withSelfRel());
            return recurso;
        })
        .collect(Collectors.toList());

    CollectionModel<EntityModel<Proveedor>> collectionModel = CollectionModel.of(proveedoresConLinks);

    collectionModel.add(linkTo(methodOn(proveedorController.class).listar()).withSelfRel());

    return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Proveedor>> obtener(@PathVariable int id) {
        return proveedorService.buscarProveedor(id)
        .map(proveedor -> {
            EntityModel<Proveedor> recurso = EntityModel.of(proveedor);

            recurso.add(linkTo(methodOn(proveedorController.class).obtener(id)).withSelfRel());

            recurso.add(linkTo(methodOn(proveedorController.class).listar()).withRel("lista-proveedores"));

            recurso.add(linkTo(methodOn(proveedorController.class).activarProv(id)).withRel("activar"));
            recurso.add(linkTo(methodOn(proveedorController.class).desactivarProv(id)).withRel("desactivar"));

            return ResponseEntity.ok(recurso);
        })
        .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Proveedor> editarDatos(@RequestBody Proveedor proveedor, @PathVariable int id) {
        try {
            Proveedor actualizado = proveedorService.actualizarInfo(id, proveedor);
            if (actualizado != null) {
                return ResponseEntity.ok(actualizado);
            } else {
                return ResponseEntity.notFound().build();

            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }

    @PutMapping("/{id}/activar")
    public ResponseEntity<Proveedor> activarProv(@PathVariable int id) {
        try {
            return ResponseEntity.ok(proveedorService.activaProveedor(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<Proveedor> desactivarProv(@PathVariable int id) {
        try {
            return ResponseEntity.ok(proveedorService.desactivaProveedor(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
