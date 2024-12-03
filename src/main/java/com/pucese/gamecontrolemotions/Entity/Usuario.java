package com.pucese.gamecontrolemotions.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "Usuario")
@NoArgsConstructor
@Getter
@Setter
public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombreUsuario;
    private String apellido;
    private String correo;
    private String contrasena;

}
