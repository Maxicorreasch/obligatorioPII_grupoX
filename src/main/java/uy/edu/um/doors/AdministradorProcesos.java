package uy.edu.um.doors;

import lombok.AllArgsConstructor;
import lombok.Data;

import uy.edu.um.tad.hash.MyHash;
import uy.edu.um.tad.hash.MyHashImpl;
import uy.edu.um.tad.heap.EmptyHeapException;
import uy.edu.um.tad.heap.MyHeap;
import uy.edu.um.tad.heap.MyHeapImpl;
import uy.edu.um.tad.list.MyLinkedListImpl;
import uy.edu.um.tad.list.MyList;
import uy.edu.um.tad.queue.EmptyQueueException;
import uy.edu.um.tad.queue.MyQueue;
import uy.edu.um.tad.queue.MyQueueImpl;
import uy.edu.um.tad.stack.EmptyStackException;
import uy.edu.um.tad.stack.MyStack;
import uy.edu.um.tad.stack.MyStackImpl;
import uy.edu.um.tad.binarytree.MySearchBinaryTree;
import uy.edu.um.tad.binarytree.MySearchBinaryTreeImpl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


@AllArgsConstructor
@Data

public class AdministradorProcesos {

    private static final int MAX_PROCESOS_TERMINADOS = 3;

    private MyQueue<Proceso> procesosNew;

    private MyHeap<Proceso> procesosPending;

    private Proceso procesosRunning;

    private MyStack<Proceso> procesosTerminados;

    private MyHash<Integer, Usuario> usuarios;

    private Log log;

    private MySearchBinaryTree<Integer, Proceso> procesosPorPID;

    private void finalizarProcesos(String estado) {

        if (procesosRunning == null) {
            return;
        }

        if (procesosTerminados.size() == MAX_PROCESOS_TERMINADOS) {
            log.logStackOverflow(procesosTerminados);

            while (!procesosTerminados.isEmpty()) {
                try {
                    procesosTerminados.pop();

                } catch (EmptyStackException e) {
                    System.out.println("Error al terminar el proceso");
                }
            }
        }

        procesosRunning.setEstado(estado);
        log.logFinalizacion(procesosRunning, estado);
        System.out.println("Ending process: PID=" + procesosRunning.getPID() + " Stage: " + estado);

        procesosTerminados.push(procesosRunning);
        procesosRunning = null;

    }

    private void finalizarProcesos(String estado, Usuario usuario) {
        if (procesosRunning == null) {
            return;
        }

        if (procesosTerminados.size() == MAX_PROCESOS_TERMINADOS) {
            log.logStackOverflow(procesosTerminados);

            MyLinkedListImpl<Proceso> impl = (MyLinkedListImpl<Proceso>) procesosTerminados;
            for (int i = 0; i < impl.size(); i++) {
                procesosPorPID.remove(impl.get(i).getPID());
            }

            while (!procesosTerminados.isEmpty()) {
                try {
                    procesosTerminados.pop();
                } catch (EmptyStackException e) {
                    System.out.println("Error al terminar el proceso");
                }
            }
        }

        procesosRunning.setEstado(estado);
        log.logFinalizacionTerminado(procesosRunning, estado, usuario);
        System.out.println("Ending process: PID=" + procesosRunning.getPID() + " Stage: " + estado + " Usuario: " + usuario.getAlias() + " UID: " + usuario.getUID());

        procesosTerminados.push(procesosRunning);
        procesosRunning = null;
    }


    public AdministradorProcesos() {
        procesosNew = new MyQueueImpl<>();
        procesosPending = new MyHeapImpl<>(false);
        procesosTerminados = new MyStackImpl<>();
        usuarios = new MyHashImpl<>();
        log = new Log();
        procesosRunning = null;
        procesosPorPID = new MySearchBinaryTreeImpl<>();
    }

    public void prepararProcesos() {

        while (!procesosNew.isEmpty()) {
            try {


                Proceso proceso = procesosNew.dequeue();

                proceso.calcularPrioridad();
                proceso.setEstado("PENDING");
                procesosPending.insert(proceso);
                log.logNuevoPendiente(proceso);
            } catch (EmptyQueueException e) {
                System.out.println("Error: no hay procesos NEW");
            }
        }
    }

    public void ejecutarProcesos() throws EmptyHeapException {
        if (procesosRunning == null) {
            try {


                Proceso proceso = procesosPending.remove();

                proceso.setEstado("RUNNING");

                procesosRunning = proceso;
                log.logEjecucion(proceso);
            } catch (EmptyHeapException e) {
                System.out.println("Error: no hay procesos RUNNING");
            }
        }
    }

    public void finalizarProcesoOK() {
        finalizarProcesos("OK");
    }

    public void finalizarProcesoError() {
        finalizarProcesos("ERROR");
    }

    public void finalizarProcesoTerminado(int uid) {
        Usuario usuario = usuarios.get(uid);

        if (usuario == null) {
            System.out.println("Usuario " + uid + "no encontrado");
            return;
        }

        finalizarProcesos("TERMINADO", usuario);
    }


    public void agregarUsuario(Usuario usuario) {
        usuarios.put(usuario.getUID(), usuario);
    }

    private void cargarUsuarios(String pathUsuarios) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(pathUsuarios));

            String linea;

            br.readLine();

            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");

                int uid = Integer.parseInt(datos[0]);
                String alias = datos[1];
                String tipo = datos[2];

                Usuario usuario = new Usuario(uid, alias, tipo);

                agregarUsuario(usuario);

            }
            br.close();
        } catch (IOException e) {
            System.out.println("Error al cargar el usuario");
        }

    }

    private void cargarProcesos(String pathProcessos) {

        try {

            BufferedReader br = new BufferedReader(new FileReader(pathProcessos));

            String linea;

            br.readLine();


            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";", 4);

                int pid = Integer.parseInt(datos[0]);
                int uid = Integer.parseInt(datos[1]);
                String nombre = datos[2];
                String datosEventos = datos[3];

                Usuario propietario = usuarios.get(uid);

                MyList<Evento> eventos = new MyLinkedListImpl<>();

                datosEventos = datosEventos.substring(1, datosEventos.length() - 1);
                String[] eventosSeparados = datosEventos.split("#");

                for (String eventoTexto : eventosSeparados) {
                    String[] partesEvento = eventoTexto.split(":");
                    String tipoEvento = partesEvento[0].trim();

                    String instruccionesTexto = partesEvento[1].replace("[", "").replace("]", "");
                    String[] instruccionesArray = instruccionesTexto.split(",");

                    MyList<String> instrucciones = new MyLinkedListImpl<>();
                    for (String instruccion : instruccionesArray) {
                        instrucciones.add(instruccion.trim());
                    }

                    Evento evento = new Evento(tipoEvento, instrucciones);

                    eventos.add(evento);
                }


                Proceso proceso = new Proceso(pid, nombre, propietario, eventos);
                procesosPorPID.add(pid, proceso);
                procesosNew.enqueue(proceso);
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Error al cargar el proceso");
        }

    }

    public void pload(String pathProcesos, String pathUsuarios) {

        cargarUsuarios(pathUsuarios);

        cargarProcesos(pathProcesos);

    }

    private String formatearProceso(Proceso p) {
        return "PID=" + p.getPID()
                + " | " + p.getNombre()
                + " | USER:" + p.getPropietario().getAlias()
                + " UID:" + p.getPropietario().getUID()
                + " | P=" + p.getPrioridad();
    }

    private String formatearTerminado(Proceso p) {
        return "PID=" + p.getPID()
                + " " + p.getNombre()
                + " | STATE: " + p.getEstado()
                + " | USER:" + p.getPropietario().getAlias()
                + " UID:" + p.getPropietario().getUID();
    }

//  pstatus

    public void imprimirEstado() {
        System.out.println("PROCESS STATUS");

        System.out.println("NEW:");
        for (int i = 0; i < procesosNew.size(); i++) {
            System.out.println("  " + formatearProceso(procesosNew.get(i)));
        }

        System.out.println("PENDING:");
        MyList<Proceso> pendientes = ((MyHeapImpl<Proceso>) procesosPending).toList();
        for (int i = 0; i < pendientes.size(); i++) {
            System.out.println("  " + formatearProceso(pendientes.get(i)));
        }

        System.out.println("EXECUTING:");
        if (procesosRunning != null) {
            System.out.println("  " + formatearProceso(procesosRunning));
        } else {
            System.out.println("  No hay proceso en ejecucion.");
        }

        System.out.println("FINISHED:");
        MyLinkedListImpl<Proceso> pila = (MyLinkedListImpl<Proceso>) procesosTerminados;
        for (int i = pila.size() - 1; i >= 0; i--) {
            System.out.println("  " + formatearTerminado(pila.get(i)));
        }
    }

//  pstatus -verbose

    public void imprimirEstadoVerbose() {
        System.out.println("PROCESS STATUS VERBOSE");

        System.out.println("NEW:");
        for (int i = 0; i < procesosNew.size(); i++) {
            imprimirProcesoDetalle(procesosNew.get(i));
        }

        System.out.println("PENDING:");
        MyList<Proceso> pendientes = ((MyHeapImpl<Proceso>) procesosPending).toList();
        for (int i = 0; i < pendientes.size(); i++) {
            imprimirProcesoDetalle(pendientes.get(i));
        }

        System.out.println("EXECUTING:");
        if (procesosRunning != null) {
            imprimirProcesoDetalle(procesosRunning);
        } else {
            System.out.println("  No hay proceso en ejecucion.");
        }

        System.out.println("FINISHED:");
        MyLinkedListImpl<Proceso> pila = (MyLinkedListImpl<Proceso>) procesosTerminados;
        for (int i = pila.size() - 1; i >= 0; i--) {
            imprimirProcesoDetalle(pila.get(i));
        }
    }

//  pstatus -u [UID]


    public void imprimirEstadoPorUsuario(int uid) {
        Usuario usuario = usuarios.get(uid);
        if (usuario == null) {
            System.out.println("Usuario UID=" + uid + " no encontrado.");
            return;
        }

        System.out.println("PROCESS STATUS - USER:" + usuario.getAlias() + " UID:" + uid);

        System.out.println("NEW:");
        for (int i = 0; i < procesosNew.size(); i++) {
            Proceso p = procesosNew.get(i);
            if (p.getPropietario().getUID() == uid) {
                System.out.println("  " + formatearProceso(p));
            }
        }

        System.out.println("PENDING:");
        MyList<Proceso> pendientes = ((MyHeapImpl<Proceso>) procesosPending).toList();
        for (int i = 0; i < pendientes.size(); i++) {
            Proceso p = pendientes.get(i);
            if (p.getPropietario().getUID() == uid) {
                System.out.println("  " + formatearProceso(p));
            }
        }

        System.out.println("EXECUTING:");
        if (procesosRunning != null && procesosRunning.getPropietario().getUID() == uid) {
            System.out.println("  " + formatearProceso(procesosRunning));
        }

        System.out.println("FINISHED:");
        MyLinkedListImpl<Proceso> pila = (MyLinkedListImpl<Proceso>) procesosTerminados;
        for (int i = pila.size() - 1; i >= 0; i--) {
            Proceso p = pila.get(i);
            if (p.getPropietario().getUID() == uid) {
                System.out.println("  " + formatearTerminado(p));
            }
        }
    }


    //  pstatus -p [PID]
    public void imprimirEstadoPorProceso(int pid) {
        Proceso p = procesosPorPID.find(pid);
        if (p != null) {
            imprimirProcesoDetalle(p);
        } else {
            System.out.println("Proceso PID=" + pid + " no encontrado en memoria.");
        }
    }

    private void imprimirProcesoDetalle(Proceso p) {
        System.out.println("  " + formatearProceso(p));

        for (int j = 0; j < p.getEventos().size(); j++) {
            Evento ev = p.getEventos().get(j);
            StringBuilder sb = new StringBuilder("    EVENT: " + ev.getTipo() + " | Instructions [");

            for (int k = 0; k < ev.getInstrucciones().size(); k++) {
                sb.append(ev.getInstrucciones().get(k));

                if (k < ev.getInstrucciones().size() - 1) {
                    sb.append(", ");
                }
            }

            sb.append("]");
            System.out.println(sb);
        }
    }
    public void cerrarLog() {
        log.cerrar();
    }
}
/// test
