package clubseneca.torneos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import clubseneca.deportes.Equipo;
import clubseneca.usuarios.Usuario;

public class InscripcionTorneo implements Serializable {
    private String identificador;
    private Equipo equipo;
    private EquipoInvitadoTorneo equipoInvitado;
    private List<Usuario> jugadores;
    private boolean preferencial;
    private double tarifa;
    private int posicion;

    public InscripcionTorneo(Equipo equipo, boolean preferencial, double tarifa) {
        this.equipo = equipo;
        this.identificador = equipo.getNombre();
        this.jugadores = new ArrayList<>();
        this.preferencial = preferencial;
        this.tarifa = tarifa;
        posicion = 0;
    }
    public InscripcionTorneo(EquipoInvitadoTorneo equipo, boolean preferencial, double tarifa) {
        this.equipoInvitado = equipo;
        this.equipo = null;
        this.jugadores = equipo.getJugadores();
        this.identificador = equipo.getNombre();
        this.preferencial = preferencial;
        this.tarifa = tarifa;
        this.posicion = 0;
    }
    public InscripcionTorneo(List<Usuario> jugadores, boolean preferencial, double tarifa) {
        this.equipo = null;
        this.jugadores = new ArrayList<>(jugadores);
        this.identificador = jugadores.get(0).getLogin();
        if (jugadores.size() == 2) identificador += " + " + jugadores.get(1).getLogin();
        this.preferencial = preferencial;
        this.tarifa = tarifa;
        posicion = 0;
    }
    public String getIdentificador() { return identificador; }
    public Equipo getEquipo() { return equipo; }
    public EquipoInvitadoTorneo getEquipoInvitado() { return equipoInvitado; }
    public List<Usuario> getJugadores() { return new ArrayList<>(jugadores); }
    public boolean isPreferencial() { return preferencial; }
    public double getTarifa() { return tarifa; }
    public int getPosicion() { return posicion; }
    public void setPosicion(int posicion) { if (posicion > 0) this.posicion = posicion; }
    public boolean contieneJugador(Usuario jugador) {
        if (jugador == null) return false;
        for (Usuario actual : jugadores) {
            if (actual.getLogin().equals(jugador.getLogin())) return true;
        }
        return false;
    }
    public boolean incluyeEmpleado() {
        for (Usuario actual : jugadores) {
            if (actual.esEmpleado()) return true;
        }
        return false;
    }
}
