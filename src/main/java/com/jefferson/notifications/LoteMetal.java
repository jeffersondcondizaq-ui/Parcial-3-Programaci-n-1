package com.jefferson.notifications;

public class LoteMetal extends Lote {
    private static final double VALOR_ALUMINIO = 2200;
    private static final double VALOR_ACERO = 900;

    private TipoMetal tipo;

    public LoteMetal(String codigo, String descripcion, int pesoKg, TipoMetal tipo) {
        super(codigo, descripcion, pesoKg);
        this.tipo = tipo;
    }

    @Override
    public double calcularValor() {
        double valorPorKg = tipo == TipoMetal.ALUMINIO ? VALOR_ALUMINIO : VALOR_ACERO;
        return pesoKg * valorPorKg;
    }

    public TipoMetal getTipo() {
        return tipo;
    }

    public void setTipo(TipoMetal tipo) {
        this.tipo = tipo;
    }
}
