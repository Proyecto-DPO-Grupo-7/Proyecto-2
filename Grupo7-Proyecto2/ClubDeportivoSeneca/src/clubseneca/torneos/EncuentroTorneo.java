package clubseneca.torneos;

import java.io.Serializable;
import java.util.Date;

public class EncuentroTorneo implements Serializable {
    private Date fecha;
    private InscripcionTorneo primero;
    private InscripcionTorneo segundo;
    private int marcadorPrimero;
    private int marcadorSegundo;
    private boolean finalizado;

    public EncuentroTorneo(Date fecha, InscripcionTorneo primero, InscripcionTorneo segundo) {
        this.fecha = fecha;
        this.primero = primero;
        this.segundo = segundo;
        this.finalizado = false;
    }
    public Date getFecha() { return fecha; }
    public InscripcionTorneo getPrimero() { return primero; }
    public InscripcionTorneo getSegundo() { return segundo; }
    public int getMarcadorPrimero() { return marcadorPrimero; }
    public int getMarcadorSegundo() { return marcadorSegundo; }
    public boolean isFinalizado() { return finalizado; }
    public boolean registrarMarcador(int primero, int segundo) {
        if (finalizado || primero < 0 || segundo < 0) return false;
        marcadorPrimero = primero;
        marcadorSegundo = segundo;
        finalizado = true;
        return true;
    }
}
