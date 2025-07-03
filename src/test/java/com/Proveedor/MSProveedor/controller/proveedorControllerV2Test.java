package com.Proveedor.MSProveedor.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.containsString;
import com.Proveedor.MSProveedor.assembler.ProveedorModelAssembler;
import com.Proveedor.MSProveedor.model.Proveedor;
import com.Proveedor.MSProveedor.model.estadoProveedor;
import com.Proveedor.MSProveedor.service.proveedorService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(proveedorControllerV2.class)
@Import(ProveedorModelAssembler.class)
public class proveedorControllerV2Test {
        @Autowired
        private MockMvc mockMvc;

        @SuppressWarnings("removal")
        @MockBean
        private proveedorService pService;

        @Autowired
        private ProveedorModelAssembler assembler;

        @Autowired
        private ObjectMapper objectMapper;

        @Test // Listar Proveedores
        void ListarOKV2() throws Exception {
                Proveedor p1 = new Proveedor(10, "Carnes Mauricio", "carnesmau@gmail.com", estadoProveedor.ACTIVO,
                                "+56993326556", "Las Rosas 503");
                Proveedor p2 = new Proveedor(1, "Lacosta", "lacost@gmail.com", estadoProveedor.ACTIVO, "56952482123",
                                "Los Hulmos 785");

                when(pService.listarProveedores()).thenReturn(List.of(p1, p2));

                mockMvc.perform(get("/api/v2/proveedor")
                                .accept(MediaTypes.HAL_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON))
                                .andExpect(jsonPath("_embedded.proveedorList").exists())
                                .andExpect(jsonPath("_embedded.proveedorList.length()").value(2))
                                .andExpect(jsonPath("_embedded.proveedorList[0].idProveedor").value(10))
                                .andExpect(jsonPath("_embedded.proveedorList[0].nomProv").value("Carnes Mauricio"))
                                .andExpect(jsonPath("_embedded.proveedorList[1].idProveedor").value(1))
                                .andExpect(jsonPath("_embedded.proveedorList[1].nomProv").value("Lacosta"))
                                .andExpect(jsonPath("_links.self").exists())
                                .andExpect(jsonPath("_embedded.proveedorList[0]._links.self.href").exists())
                                .andExpect(jsonPath("_embedded.proveedorList[0]._links.proveedor.href").exists())
                                .andExpect(jsonPath("_embedded.proveedorList[0]._links.editar.href").exists())
                                .andExpect(jsonPath("_embedded.proveedorList[0]._links.activar.href").exists())
                                .andExpect(jsonPath("_embedded.proveedorList[0]._links.desactivar.href").exists())
                                .andExpect(jsonPath("_embedded.proveedorList[1]._links.self.href").exists())
                                .andExpect(jsonPath("_embedded.proveedorList[1]._links.proveedor.href").exists())
                                .andExpect(jsonPath("_embedded.proveedorList[1]._links.editar.href").exists())
                                .andExpect(jsonPath("_embedded.proveedorList[1]._links.activar.href").exists())
                                .andExpect(jsonPath("_embedded.proveedorList[1]._links.desactivar.href").exists());
        }

        @Test // Listar Proveedores - No hay proveedores registrados
        void ListarBADV2() throws Exception {
                when(pService.listarProveedores()).thenReturn(Collections.emptyList());

                mockMvc.perform(get("/api/v2/proveedor")
                                .accept(MediaTypes.HAL_JSON))
                                .andExpect(status().isNoContent());
        }

        @Test // Buscar Proveedor por ID
        void BuscarOKV2() throws Exception {
                Proveedor nuevo = new Proveedor(5, "Costa", "costa1998@gmail.com", estadoProveedor.ACTIVO,
                                "+56921546938", "Los Canarios 4578");

                when(pService.buscarProveedor(5)).thenReturn(Optional.of(nuevo));

                mockMvc.perform(get("/api/v2/proveedor/5")
                                .accept(MediaTypes.HAL_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON))
                                .andExpect(jsonPath("$.idProveedor").value(5))
                                .andExpect(jsonPath("$.nomProv").value("Costa"))
                                .andExpect(jsonPath("$.correoProv").value("costa1998@gmail.com"))
                                .andExpect(jsonPath("$.estado").value("ACTIVO"))
                                .andExpect(jsonPath("$.telProv").value("+56921546938"))
                                .andExpect(jsonPath("$.dirProv").value("Los Canarios 4578"))
                                .andExpect(jsonPath("$._links.self.href").exists())
                                .andExpect(jsonPath("$._links.self.href").value("http://localhost/api/v2/proveedor/5"))
                                .andExpect(jsonPath("$._links.proveedor.href").exists())
                                .andExpect(jsonPath("$._links.editar.href").exists())
                                .andExpect(jsonPath("$._links.activar.href").exists())
                                .andExpect(jsonPath("$._links.desactivar.href").exists());
        }

        @Test // Buscar Proveedor por ID - No hay proveedor asignado al ID
        void BuscarBADV2() throws Exception {
                when(pService.buscarProveedor(999)).thenReturn(Optional.empty());

                mockMvc.perform(get("/api/v2/proveedor/999")
                                .accept(MediaTypes.HAL_JSON))
                                .andExpect(status().isNotFound());
        }

        @Test // Registrar proveedores
        void RegistroOKV2() throws Exception {
                Proveedor nuevo = new Proveedor(0, "Lacosta", "lacost@gmail.com", null, "56952482123",
                                "Los Hulmos 785");
                Proveedor registrado = new Proveedor(5, "Lacosta", "lacost@gmail.com", estadoProveedor.ACTIVO,
                                "56952482123",
                                "Los Hulmos 785");

                when(pService.existePorId(0)).thenReturn(false);
                when(pService.registrarProveedor(any(Proveedor.class))).thenReturn(registrado);

                mockMvc.perform(post("/api/v2/proveedor")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaTypes.HAL_JSON)
                                .content(objectMapper.writeValueAsString(nuevo)))
                                .andExpect(status().isCreated())
                                .andExpect(header().string("Location", containsString("/api/v2/proveedor/5")))
                                .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON))
                                .andExpect(jsonPath("$.idProveedor").value(5))
                                .andExpect(jsonPath("$.nomProv").value("Lacosta"))
                                .andExpect(jsonPath("$.estado").value("ACTIVO"))
                                .andExpect(jsonPath("$.correoProv").value("lacost@gmail.com"))
                                .andExpect(jsonPath("$.telProv").value("56952482123"))
                                .andExpect(jsonPath("$.dirProv").value("Los Hulmos 785"))
                                .andExpect(jsonPath("$._links.self.href").exists())
                                .andExpect(jsonPath("$._links.proveedor.href").exists())
                                .andExpect(jsonPath("$._links.editar.href").exists())
                                .andExpect(jsonPath("$._links.activar.href").exists())
                                .andExpect(jsonPath("$._links.desactivar.href").exists());
        }

        @Test // Registrar Proveedor - El ID esta en uso
        void RegistroBADV2() throws Exception {
                Proveedor registrado = new Proveedor(5, "Lacosta", "lacost@gmail.com", estadoProveedor.ACTIVO,
                                "56952482123",
                                "Los Hulmos 785");

                when(pService.existePorId(5)).thenReturn(true);

                mockMvc.perform(post("/api/v2/proveedor")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaTypes.HAL_JSON)
                                .content(objectMapper.writeValueAsString(registrado)))
                                .andExpect(status().isBadRequest());
        }

        @Test // Actualizar información del proveedor
        void ActualizarOKV2() throws Exception {
                Proveedor original = new Proveedor(10, "Castle", "castle@gmail.com", estadoProveedor.ACTIVO,
                                "+56978124545", "Las amapolas 127");

                Proveedor actualizado = new Proveedor(10, "Marco Polo", "marcopolo@gmail.com", estadoProveedor.ACTIVO,
                                "+56997856323", "Las Rosas 7425");

                when(pService.actualizarInfo(eq(10), any(Proveedor.class))).thenReturn(actualizado);

                mockMvc.perform(put("/api/v2/proveedor/10")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaTypes.HAL_JSON)
                                .content(objectMapper.writeValueAsString(original)))
                                .andExpect(status().isOk())
                                .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON))
                                .andExpect(jsonPath("$.idProveedor").value(10))
                                .andExpect(jsonPath("$.nomProv").value("Marco Polo"))
                                .andExpect(jsonPath("$.estado").value("ACTIVO"))
                                .andExpect(jsonPath("$.correoProv").value("marcopolo@gmail.com"))
                                .andExpect(jsonPath("$.telProv").value("+56997856323"))
                                .andExpect(jsonPath("$.dirProv").value("Las Rosas 7425"))
                                .andExpect(jsonPath("$._links.self.href").exists())
                                .andExpect(jsonPath("$._links.self.href").value("http://localhost/api/v2/proveedor/10"))
                                .andExpect(jsonPath("$._links.proveedor.href").exists())
                                .andExpect(jsonPath("$._links.editar.href").exists())
                                .andExpect(jsonPath("$._links.activar.href").exists())
                                .andExpect(jsonPath("$._links.desactivar.href").exists());
        }

        @Test // Actualizar información del proveedor - Proveedor no encontrado
        void ActualizarNotFoundV2() throws Exception {
                Proveedor original = new Proveedor(999, "Castle", "castle@gmail.com", estadoProveedor.ACTIVO,
                                "+56978124545", "Las amapolas 127");

                when(pService.actualizarInfo(eq(999), any(Proveedor.class))).thenReturn(null);

                mockMvc.perform(put("/api/v2/proveedor/999")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaTypes.HAL_JSON)
                                .content(objectMapper.writeValueAsString(original)))
                                .andExpect(status().isNotFound());
        }

        @Test // Actualizar información del proveedor - Error al actualizar
        void ActualizarBADV2() throws Exception {
                Proveedor original = new Proveedor(20, "Castle", "castle@gmail.com", estadoProveedor.ACTIVO,
                                "+56978124545", "Las amapolas 127");

                when(pService.actualizarInfo(eq(20), any(Proveedor.class)))
                                .thenThrow(new RuntimeException("Error interno"));

                mockMvc.perform(put("/api/v2/proveedor/20")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaTypes.HAL_JSON)
                                .content(objectMapper.writeValueAsString(original)))
                                .andExpect(status().isBadRequest());
        }

        @Test // Activar Proveedor
        void ActivarOKV2() throws Exception {
                Proveedor p1 = new Proveedor(7, "Sis", "siscorreo@gmail.com", estadoProveedor.ACTIVO, "+56978451236",
                                "Si 111");

                when(pService.activaProveedor(7)).thenReturn(p1);

                mockMvc.perform(put("/api/v2/proveedor/activar/7")
                                .accept(MediaTypes.HAL_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON))
                                .andExpect(jsonPath("$.idProveedor").value(7))
                                .andExpect(jsonPath("$.nomProv").value("Sis"))
                                .andExpect(jsonPath("$.estado").value("ACTIVO"))
                                .andExpect(jsonPath("$.correoProv").value("siscorreo@gmail.com"))
                                .andExpect(jsonPath("$.telProv").value("+56978451236"))
                                .andExpect(jsonPath("$.dirProv").value("Si 111"))
                                .andExpect(jsonPath("$._links.self.href").exists())
                                .andExpect(jsonPath("$._links.self.href").value("http://localhost/api/v2/proveedor/7"))
                                .andExpect(jsonPath("$._links.proveedor.href").exists())
                                .andExpect(jsonPath("$._links.editar.href").exists())
                                .andExpect(jsonPath("$._links.activar.href").exists())
                                .andExpect(jsonPath("$._links.desactivar.href").exists());

        }

        @Test // Activar Proveedor - ID no encontrado
        void ActivarNotFoundV2() throws Exception {

                when(pService.activaProveedor(99)).thenThrow(new RuntimeException("Proveedor no encontrado"));

                mockMvc.perform(put("/api/v2/proveedor/activar/99")
                                .accept(MediaTypes.HAL_JSON))
                                .andExpect(status().isNotFound());
        }

        @Test // Desctivar Proveedor
        void DesactivarOKV2() throws Exception {
                Proveedor p1 = new Proveedor(7, "Sis", "siscorreo@gmail.com", estadoProveedor.INACTIVO, "+56978451236",
                                "Si 111");

                when(pService.desactivaProveedor(7)).thenReturn(p1);

                mockMvc.perform(put("/api/v2/proveedor/desactivar/7")
                                .accept(MediaTypes.HAL_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON))
                                .andExpect(jsonPath("$.idProveedor").value(7))
                                .andExpect(jsonPath("$.nomProv").value("Sis"))
                                .andExpect(jsonPath("$.estado").value("INACTIVO"))
                                .andExpect(jsonPath("$.correoProv").value("siscorreo@gmail.com"))
                                .andExpect(jsonPath("$.telProv").value("+56978451236"))
                                .andExpect(jsonPath("$.dirProv").value("Si 111"))
                                .andExpect(jsonPath("$._links.self.href").exists())
                                .andExpect(jsonPath("$._links.self.href").value("http://localhost/api/v2/proveedor/7"))
                                .andExpect(jsonPath("$._links.proveedor.href").exists())
                                .andExpect(jsonPath("$._links.editar.href").exists())
                                .andExpect(jsonPath("$._links.activar.href").exists())
                                .andExpect(jsonPath("$._links.desactivar.href").exists());
                ;

        }

        @Test // Desactivar Proveedor - ID no encontrado
        void DesactivarNotFoundV2() throws Exception {

                when(pService.desactivaProveedor(99)).thenThrow(new RuntimeException("Proveedor no encontrado"));

                mockMvc.perform(put("/api/v2/proveedor/desactivar/99")
                                .accept(MediaTypes.HAL_JSON))
                                .andExpect(status().isNotFound());
        }

}
