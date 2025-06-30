package com.Proveedor.MSProveedor.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.Proveedor.MSProveedor.controller.proveedorControllerV2;
import com.Proveedor.MSProveedor.model.Proveedor;

@Component
public class ProveedorModelAssembler implements RepresentationModelAssembler<Proveedor, EntityModel<Proveedor>> {

    @Override
    public EntityModel<Proveedor> toModel(Proveedor proveedor) {
        return EntityModel.of(proveedor,
                linkTo(methodOn(proveedorControllerV2.class).Obtener(proveedor.getIdProveedor())).withSelfRel(),
                linkTo(methodOn(proveedorControllerV2.class).listar()).withRel("proveedor"),
                linkTo(methodOn(proveedorControllerV2.class).editarDatos(proveedor, proveedor.getIdProveedor())).withRel("editar"),
                linkTo(methodOn(proveedorControllerV2.class).activarProv(proveedor.getIdProveedor())).withRel("activar"),
                linkTo(methodOn(proveedorControllerV2.class).desactivarProv(proveedor.getIdProveedor())).withRel("desactivar"));
    }

}
