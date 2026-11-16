package clubseneca.consola;

import java.util.List;
import clubseneca.turnos.SolicitudCambioTurno;
import clubseneca.turnos.TurnoSemanal;
import clubseneca.usuarios.Administrador;
import clubseneca.usuarios.Empleado;
import clubseneca.usuarios.Usuario;

public class AdministracionTurnos {
    private ConsolaAdministrador c;
    public AdministracionTurnos(ConsolaAdministrador c) { this.c=c; }
    public void menu() {
        System.out.println("1 Asignar turno, 2 Resolver solicitud, 3 Ver turnos");
        int opcion = c.leerEntero(1,3);
        if (opcion == 1) asignar();
        if (opcion == 2) resolver();
        if (opcion == 3) {
            for (TurnoSemanal turno : c.club.getGestionTurnos().getTurnos()) {
                System.out.println(turno.getEmpleado().getLogin()+" " + turno.getDia()+" "+turno.getHoraInicio()+" a "+turno.getHoraFin());
            }
        }
    }
    private void asignar() {
        System.out.println("Login empleado:");
        Usuario usuario = c.club.getGestionUsuarios().buscarUsuario(c.leerTexto());
        if (usuario == null || !usuario.esEmpleado()) { System.out.println("No es empleado."); return; }
        System.out.println("Dia (LUNES a DOMINGO):");
        String dia = c.leerDia();
        String inicio = c.leerHora();
        String fin = c.leerHora();
        if (fin.compareTo(inicio) <= 0) { System.out.println("Horario invalido."); return; }
        c.club.getGestionTurnos().asignarTurno(new TurnoSemanal(dia,inicio,fin,(Empleado)usuario));
        System.out.println("Turno asignado.");
    }
    private void resolver() {
        List<SolicitudCambioTurno> solicitudes = c.club.getGestionTurnos().getSolicitudes();
        if (solicitudes.isEmpty()) { System.out.println("Sin solicitudes."); return; }
        for (int i=0; i<solicitudes.size(); i++) {
            SolicitudCambioTurno solicitud = solicitudes.get(i);
            System.out.println((i+1)+". "+solicitud.getEmpleado().getLogin()+" - "+solicitud.getEstado());
        }
        SolicitudCambioTurno solicitud = solicitudes.get(c.leerEntero(1,solicitudes.size())-1);
        System.out.println("1 Aprobar, 2 Rechazar:");
        if (c.leerEntero(1,2) == 1) {
            if (c.club.getGestionTurnos().aprobarSolicitud(solicitud,(Administrador)c.usuario)) System.out.println("Cambio aprobado.");
            else System.out.println("Cambio rechazado por cobertura o estado.");
        } else {
            c.club.getGestionTurnos().rechazarSolicitud(solicitud,(Administrador)c.usuario);
            System.out.println("Solicitud rechazada.");
        }
    }
}
