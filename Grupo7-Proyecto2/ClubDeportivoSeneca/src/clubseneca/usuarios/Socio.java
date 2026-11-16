package clubseneca.usuarios;

import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import clubseneca.torneos.BonoDescuento;

public class Socio extends Usuario {
    private Date fechaNacimiento;
    private double puntosFidelidad;
    private boolean activo;
    private boolean tieneBebidaCaliente;
    private List<BonoDescuento> bonos;

    public Socio(String login, String password, Date fechaNacimiento) {
        super(login, password);
        this.fechaNacimiento = fechaNacimiento;
        this.puntosFidelidad = 0;
        this.activo = true;
        this.tieneBebidaCaliente = false;
        this.bonos = new ArrayList<>();
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public int calcularEdad() {
        Calendar hoy = Calendar.getInstance();
        Calendar nacimiento = Calendar.getInstance();
        nacimiento.setTime(fechaNacimiento);
        int edad = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR);
        if (hoy.get(Calendar.DAY_OF_YEAR) < nacimiento.get(Calendar.DAY_OF_YEAR)) {
            edad--;
        }
        return edad;
    }

    public double getPuntosFidelidad() {
        return puntosFidelidad;
    }

    public void agregarPuntos(double puntos) {
        if (puntos > 0) {
            puntosFidelidad += puntos;
        }
    }

    public boolean redimirPuntos(double puntos) {
        if (puntos > 0 && puntos <= puntosFidelidad) {
            puntosFidelidad -= puntos;
            return true;
        }
        return false;
    }

    public void agregarBono(BonoDescuento bono) {
        if (bono != null && bono.getSocio().getLogin().equals(getLogin())) bonos.add(bono);
    }
    public List<BonoDescuento> getBonos() { return new ArrayList<>(bonos); }
    public BonoDescuento buscarBonoDisponible(String nombreTorneo) {
        for (BonoDescuento bono : bonos) {
            if (!bono.isUsado() && bono.getNombreTorneo().equalsIgnoreCase(nombreTorneo)) return bono;
        }
        return null;
    }
    public boolean esSocio() {
        return true;
    }

    public boolean isActivo() {
        return activo;
    }

    public void darDeBaja() {
        activo = false;
    }

    public boolean isTieneBebidaCaliente() {
        return tieneBebidaCaliente;
    }

    public void setTieneBebidaCaliente(boolean tieneBebidaCaliente) {
        this.tieneBebidaCaliente = tieneBebidaCaliente;
    }
}
