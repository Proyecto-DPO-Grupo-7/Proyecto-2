package clubseneca.usuarios;

import java.util.ArrayList;
import java.util.List;
import clubseneca.torneos.BonoDescuento;

public class InvitadoTorneo extends Usuario {
    private String tipo;
    private String nivel;
    private List<BonoDescuento> bonos;

    public InvitadoTorneo(String login, String password, String tipo, String nivel) {
        super(login, password);
        this.tipo = tipo;
        this.nivel = nivel;
        this.bonos = new ArrayList<>();
    }
    public void agregarBono(BonoDescuento bono) {
        if (bono != null && bono.getSocio().getLogin().equals(getLogin())) bonos.add(bono);
    }
    public List<BonoDescuento> getBonos() { return new ArrayList<>(bonos); }
    public BonoDescuento buscarBonoDisponible(String torneo) {
        for (BonoDescuento bono : bonos) if (!bono.isUsado() && bono.getNombreTorneo().equalsIgnoreCase(torneo)) return bono;
        return null;
    }
    public boolean esInvitadoTorneo() { return true; }
    public String getTipo() { return tipo; }
    public String getNivel() { return nivel; }
    public boolean cambiarNivel(String nivel) {
        if (nivel == null) return false;
        if (!nivel.equalsIgnoreCase("Principiante") && !nivel.equalsIgnoreCase("Intermedio") && !nivel.equalsIgnoreCase("Avanzado")) return false;
        this.nivel = nivel;
        return true;
    }
}
