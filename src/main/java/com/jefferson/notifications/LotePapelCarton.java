package com.jefferson.notifications;

public class LotePapelCarton extends Lote implements IEtiquetable {
    private static final double VALOR_KG = 500;
    private static final double VALOR_EXTRA_KG = 100;

    private boolean clasificado;

    public LotePapelCarton(String codigo, String descripcion, int pesoKg, boolean clasificado) {
        super(codigo, descripcion, pesoKg);
        this.clasificado = clasificado;
    }

    @Override
    public double calcularValor() {
        double valorPorKg = VALOR_KG + (clasificado ? VALOR_EXTRA_KG : 0);
        return pesoKg * valorPorKg;
    }

    @Override
    public String generarEtiqueta() {
        return "PAPEL/CARTON - " + codigo + " - " + pesoKg + "kg" + (clasificado ? " (clasificado)" : "");
    }

    public boolean isClasificado() {
        return clasificado;
    }

    public void setClasificado(boolean clasificado) {
        this.clasificado = clasificado;
    }
}
