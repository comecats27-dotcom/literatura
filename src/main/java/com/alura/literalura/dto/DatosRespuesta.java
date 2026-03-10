package com.alura.literalura.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DatosRespuesta {

    @JsonAlias("results")
    private List<DatosLibro> resultados;

    public List<DatosLibro> getResultados() {
        return resultados;
    }

}