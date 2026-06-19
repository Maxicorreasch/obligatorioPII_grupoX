package uy.edu.um.doors;

import uy.edu.um.tad.list.MyLinkedListImpl;
import uy.edu.um.tad.stack.MyStack;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log {

    private static final DateTimeFormatter FORMATO_TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter FORMATO_FECHA     = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private BufferedWriter writer;

    public Log() {
        String nombreArchivo = "DOORS_PROCESS_LOG_" + LocalDate.now().format(FORMATO_FECHA);
        try {
            writer = new BufferedWriter(new FileWriter(nombreArchivo, true));
        } catch (IOException e) {
            System.out.println("Error al crear archivo de log: " + e.getMessage());
        }
    }

    public void logNuevoPendiente(Proceso proceso) {
        escribir(timestamp() + "NEW PENDING PROCESS: PID=" + proceso.getPID()
                + " | " + proceso.getNombre()
                + " | USER:" + proceso.getPropietario().getAlias()
                + " UID:" + proceso.getPropietario().getUID()
                + " | P=" + proceso.getPrioridad());
    }

    public void logEjecucion(Proceso proceso) {
        StringBuilder sb = new StringBuilder();
        sb.append(timestamp())
                .append("EXECUTING PROCESS: PID=").append(proceso.getPID())
                .append(" | USER:").append(proceso.getPropietario().getAlias())
                .append(" UID:").append(proceso.getPropietario().getUID());

        for (int i = 0; i < proceso.getEventos().size(); i++) {
            Evento ev = proceso.getEventos().get(i);
            sb.append("\n  EVENT: ").append(ev.getTipo()).append(" | Instructions [");
            for (int j = 0; j < ev.getInstrucciones().size(); j++) {
                sb.append(ev.getInstrucciones().get(j));
                if (j < ev.getInstrucciones().size() - 1) sb.append(", ");
            }
            sb.append("]");
        }
        escribir(sb.toString());
    }

    public void logFinalizacion(Proceso proceso, String estado) {
        escribir(timestamp() + "ENDING PROCESS: PID=" + proceso.getPID()
                + " | STATE: " + estado);
    }

    public void logFinalizacionTerminado(Proceso proceso, String estado, Usuario usuario) {
        escribir(timestamp() + "ENDING PROCESS: PID=" + proceso.getPID()
                + " | STATE: " + estado
                + " by USER:" + usuario.getAlias()
                + " UID:" + usuario.getUID());
    }

    public void logStackOverflow(MyStack<Proceso> pila) {
        StringBuilder sb = new StringBuilder();
        sb.append(timestamp()).append("Finished process stack overflow");
        MyLinkedListImpl<Proceso> impl = (MyLinkedListImpl<Proceso>) pila;
        for (int i = impl.size() - 1; i >= 0; i--) {
            Proceso p = impl.get(i);
            sb.append("\nPID=").append(p.getPID()).append(" ").append(p.getNombre())
                    .append(" | STATE: ").append(p.getEstado())
                    .append(" | USER:").append(p.getPropietario().getAlias())
                    .append(" UID:").append(p.getPropietario().getUID());
        }
        escribir(sb.toString());
    }

    public void cerrar() {
        try {
            if (writer != null) writer.close();
        } catch (IOException e) {
            System.out.println("Error al cerrar log: " + e.getMessage());
        }
    }

    private String timestamp() {
        return "[" + LocalDateTime.now().format(FORMATO_TIMESTAMP) + "]: ";
    }

    private void escribir(String mensaje) {
        try {
            writer.write(mensaje);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.out.println("Error al escribir en log: " + e.getMessage());
        }
    }
}