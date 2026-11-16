package clubseneca.torneos;

import java.io.Serializable;
import clubseneca.usuarios.Socio;
import clubseneca.usuarios.Usuario;

public class BonoDescuento implements Serializable {
    private String nombreTorneo;
    private Usuario socio;
    private double valor;
    private boolean usado;

    public BonoDescuento(String nombreTorneo, Usuario socio, double valor) {
        this.nombreTorneo = nombreTorneo;
        this.socio = socio;
        this.valor = valor;
        this.usado = false;
    }
    public String getNombreTorneo() { return nombreTorneo; }
    public Usuario getSocio() { return socio; }
    public double getValor() { return valor; }
    public boolean isUsado() { return usado; }
    public boolean usar() {
        if (usado || valor <= 0) return false;
        usado = true;
        return true;
    }
}
