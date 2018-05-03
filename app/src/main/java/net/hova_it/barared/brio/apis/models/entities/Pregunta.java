package net.hova_it.barared.brio.apis.models.entities;

/**
 * Estructura del modelo Pregunta
 * Created by Alejandro Gomez on 06/01/2016.
 */
public class Pregunta {
    private int idPregunta;
    private String pregunta;

    public Pregunta(){}

    public Pregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public int getIdPregunta() {
        return idPregunta;
    }

    public void setIdPregunta(int idPregunta) {
        this.idPregunta = idPregunta;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    @Override
    public String toString() {
        return pregunta;
    }
}
