package com.jefferson.notifications;

public class LoteElectronico extends Lote implements IEtiquetable {
    private static final double VALOR_KG = 1400;
    private static final double VALOR_EQUIPO = 800;

    private int equiposCompletos;

    public LoteElectronico(String codigo, String descripcion, int pesoKg, int equiposCompletos) {
        super(codigo, descripcion, pesoKg);
        this.equiposCompletos = equiposCompletos;
    }

    @Override
    public double calcularValor() {
        return (pesoKg * VALOR_KG) + (equiposCompletos * VALOR_EQUIPO);
    }

    public int getEquiposCompletos() {
        return equiposCompletos;
    }

    public void setEquiposCompletos(int equiposCompletos) {
        this.equiposCompletos = equiposCompletos;
    }

    @Override
    public String generarEtiqueta() {
        return "Lote: " + codigo + " | Equipos completos: " + equiposCompletos;
    }
}
