package com.udea.laboratorio.domain;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A Carro.
 */
@Entity
@Table(name = "carro")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "carro")
public class Carro implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @NotNull
    @Column(name = "fabricante", nullable = false)
    private String fabricante;

    @NotNull
    @Column(name = "modelo", nullable = false)
    private String modelo;

    @NotNull
    @Column(name = "precio", precision = 21, scale = 2, nullable = false)
    private BigDecimal precio;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFabricante() {
        return fabricante;
    }

    public Carro fabricante(String fabricante) {
        this.fabricante = fabricante;
        return this;
    }

    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }

    public String getModelo() {
        return modelo;
    }

    public Carro modelo(String modelo) {
        this.modelo = modelo;
        return this;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public Carro precio(BigDecimal precio) {
        this.precio = precio;
        return this;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Carro)) {
            return false;
        }
        return id != null && id.equals(((Carro) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Carro{" +
            "id=" + getId() +
            ", fabricante='" + getFabricante() + "'" +
            ", modelo='" + getModelo() + "'" +
            ", precio=" + getPrecio() +
            "}";
    }
}
