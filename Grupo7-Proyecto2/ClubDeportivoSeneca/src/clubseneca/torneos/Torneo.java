package clubseneca.torneos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import clubseneca.deportes.Categoria;
import clubseneca.deportes.Deporte;
import clubseneca.individual.DisciplinaIndividual;

public abstract class Torneo implements Serializable {
    private String nombre;
    private Date fecha;
    private Deporte deporte;
    private Categoria categoria;
    private DisciplinaIndividual disciplina;
    private String nivel;
    private boolean dobles;
    private int cupos;
    private boolean preferenciasLiberadas;
    private String estado;
    private List<String> destacados;
    private List<InscripcionTorneo> inscripciones;
    private List<EncuentroTorneo> encuentros;
    private InscripcionTorneo ganador;

    protected Torneo(String nombre, Date fecha, Deporte deporte, Categoria categoria, DisciplinaIndividual disciplina, String nivel, boolean dobles, int cupos) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.deporte = deporte;
        this.categoria = categoria;
        this.disciplina = disciplina;
        this.nivel = nivel;
        this.dobles = dobles;
        this.cupos = cupos;
        this.estado = "INSCRIPCIONES";
        this.destacados = new ArrayList<>();
        this.inscripciones = new ArrayList<>();
        this.encuentros = new ArrayList<>();
        this.preferenciasLiberadas = false;
    }
    public String getNombre() { return nombre; }
    public Date getFecha() { return fecha; }
    public Deporte getDeporte() { return deporte; }
    public Categoria getCategoria() { return categoria; }
    public DisciplinaIndividual getDisciplina() { return disciplina; }
    public String getNivel() { return nivel; }
    public boolean isDobles() { return dobles; }
    public int getCupos() { return cupos; }
    public String getEstado() { return estado; }
    public boolean isIndividual() { return disciplina != null; }
    public InscripcionTorneo getGanador() { return ganador; }
    public List<InscripcionTorneo> getInscripciones() { return new ArrayList<>(inscripciones); }
    public List<EncuentroTorneo> getEncuentros() { return new ArrayList<>(encuentros); }
    public int getCuposPreferenciales() { return (cupos + 4) / 5; }
    public int getCuposOcupados() { return inscripciones.size(); }
    public int getCuposDisponibles() { return cupos - inscripciones.size(); }
    public boolean isPreferenciasLiberadas() { return preferenciasLiberadas; }

    public boolean registrarDestacado(String identificador) {
        if (!estado.equals("INSCRIPCIONES") || identificador == null || identificador.trim().isEmpty()) return false;
        if (destacados.contains(identificador)) return false;
        destacados.add(identificador);
        return true;
    }
    public boolean esDestacado(String identificador) { return destacados.contains(identificador); }
    public int getPreferencialesOcupados() {
        int cantidad = 0;
        for (InscripcionTorneo actual : inscripciones) if (actual.isPreferencial()) cantidad++;
        return cantidad;
    }
    public boolean inscribir(InscripcionTorneo inscripcion) {
        if (inscripcion == null || !estado.equals("INSCRIPCIONES")) return false;
        if (inscripciones.size() >= cupos) return false;
        for (InscripcionTorneo actual : inscripciones) {
            if (actual.getIdentificador().equalsIgnoreCase(inscripcion.getIdentificador())) return false;
            for (clubseneca.usuarios.Usuario jugador : inscripcion.getJugadores()) {
                if (actual.contieneJugador(jugador)) return false;
            }
        }
        if (inscripcion.isPreferencial()) {
            if (getPreferencialesOcupados() >= getCuposPreferenciales()) return false;
            if (!esDestacado(inscripcion.getIdentificador())) return false;
        } else if (!preferenciasLiberadas) {
            int regulares = inscripciones.size() - getPreferencialesOcupados();
            if (regulares >= cupos - getCuposPreferenciales()) return false;
        }
        inscripciones.add(inscripcion);
        return true;
    }
    public boolean retirar(String identificador) {
        if (!estado.equals("INSCRIPCIONES")) return false;
        for (InscripcionTorneo inscripcion : inscripciones) {
            if (inscripcion.getIdentificador().equalsIgnoreCase(identificador)) {
                inscripciones.remove(inscripcion);
                return true;
            }
        }
        return false;
    }
    public boolean liberarPreferenciales() {
        if (!estado.equals("INSCRIPCIONES")) return false;
        preferenciasLiberadas = true;
        return true;
    }
    public boolean iniciar() {
        if (!estado.equals("INSCRIPCIONES") || inscripciones.size() < 2) return false;
        preferenciasLiberadas = true;
        estado = "EN_CURSO";
        return true;
    }
    public boolean registrarEncuentro(EncuentroTorneo encuentro) {
        if (!estado.equals("EN_CURSO") || encuentro == null) return false;
        if (!inscripciones.contains(encuentro.getPrimero()) || !inscripciones.contains(encuentro.getSegundo())) return false;
        if (encuentro.getPrimero() == encuentro.getSegundo()) return false;
        encuentros.add(encuentro);
        return true;
    }
    public boolean registrarPosicion(String identificador, int posicion) {
        if (!estado.equals("EN_CURSO") && !estado.equals("FINALIZADO")) return false;
        if (posicion < 1 || posicion > inscripciones.size()) return false;
        for (InscripcionTorneo inscripcion : inscripciones) {
            if (inscripcion.getPosicion() == posicion) return false;
        }
        for (InscripcionTorneo inscripcion : inscripciones) {
            if (inscripcion.getIdentificador().equalsIgnoreCase(identificador)) {
                inscripcion.setPosicion(posicion);
                return true;
            }
        }
        return false;
    }
    public boolean finalizar(InscripcionTorneo ganador) {
        if (!estado.equals("EN_CURSO") || !inscripciones.contains(ganador)) return false;
        if (encuentros.isEmpty()) return false;
        for (EncuentroTorneo encuentro : encuentros) if (!encuentro.isFinalizado()) return false;
        this.ganador = ganador;
        ganador.setPosicion(1);
        estado = "FINALIZADO";
        entregarPremio(ganador);
        return true;
    }
    public abstract double calcularTarifa(InscripcionTorneo inscripcion);
    public abstract String getTipo();
    protected abstract void entregarPremio(InscripcionTorneo ganador);
}
