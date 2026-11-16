package clubseneca.torneos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import clubseneca.deportes.Categoria;
import clubseneca.deportes.Deporte;
import clubseneca.usuarios.Socio;
import clubseneca.usuarios.Usuario;

public class EquipoInvitadoTorneo implements Serializable {
    private String nombre;
    private Deporte deporte;
    private Categoria categoria;
    private List<Usuario> jugadores;

    public EquipoInvitadoTorneo(String nombre, Deporte deporte, Categoria categoria) {
        this.nombre = nombre;
        this.deporte = deporte;
        this.categoria = categoria;
        this.jugadores = new ArrayList<>();
    }
    public String getNombre() { return nombre; }
    public Deporte getDeporte() { return deporte; }
    public Categoria getCategoria() { return categoria; }
    public List<Usuario> getJugadores() { return new ArrayList<>(jugadores); }
    public boolean agregarJugador(Usuario jugador) {
        if (jugador == null || jugador.esAdministrador() || jugador.esEmpleado()) return false;
        if (jugador.esSocio()) {
            Socio socio = (Socio)jugador;
            if (!socio.isActivo() || !categoria.aceptaEdad(socio.calcularEdad())) return false;
        }
        for (Usuario actual : jugadores) if (actual.getLogin().equals(jugador.getLogin())) return false;
        jugadores.add(jugador);
        return true;
    }
    public boolean tieneMinimo() { return jugadores.size() >= deporte.getMinJugadoresHabilitados(); }
}
