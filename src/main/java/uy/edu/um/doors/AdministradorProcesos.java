package uy.edu.um.doors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


@AllArgsConstructor
@Data

public class AdministradorProcesos {

    private static final int MAX_PROCESOS_TERMINADOS = 10;

    private MyQueue<Proceso> procesosNew;

    private MyHeap<Proceso> procesosPending;

    private Proceso procesosRunning;

    private MyStack<Proceso> procesosTerminados;

    private MyHash<Integer, Usuario> usuarios;

    private void finalizarProcesos(String estado) {

        if(procesosRunning == null) {
            return;
        }

        if(procesosTerminados.size() == MAX_PROCESOS_TERMINADOS) {
            System.out.println("Proceso terminado stack overflow");

            while (!procesosTerminados.isEmpty()) {
                try {
                    Proceso proceso = procesosTerminados.pop();
                    System.out.println("Proceso terminado: " + proceso);

                } catch (EmptyStackException e) {
                    System.out.println("Error al terminar el proceso");
                }
            }
        }

        procesosRunning.setEstado(estado);
        System.out.println("Ending process: PID=" + procesosRunning.getPID() + " Stage: " + estado);

        procesosTerminados.push(procesosRunning);
        procesosRunning = null;



    }

    private void finalizarProcesos(String estado, Usuario usuario){
        if(procesosRunning == null) {
            return;
        }

        if(procesosTerminados.size() == MAX_PROCESOS_TERMINADOS) {
            System.out.println("Proceso terminado stack overflow");

            while (!procesosTerminados.isEmpty()) {
                try {
                    Proceso proceso = procesosTerminados.pop();
                    System.out.println("Proceso terminado: " + proceso);
                }catch (EmptyStackException e) {
                    System.out.println("Error al terminar el proceso");
                }
            }
        }

        procesosRunning.setEstado(estado);
        System.out.println("Ending process: PID=" + procesosRunning.getPID() + " Stage: " + estado + " Usuario: " + usuario.getAlias() + " UID: " + usuario.getUID());

        procesosTerminados.push(procesosRunning);
        procesosRunning = null;
    }



    public AdministradorProcesos() {
        procesosNew = new MyQueueImpl<>();
        procesosPending = new MyHeapImpl<>(false);
        procesosTerminados = new MyStackImpl<>();
        usuarios = new MyHashImpl<>();
        procesosRunning = null;
    }

    public void prepararProcesos() {

        while(!procesosNew.isEmpty()){
            try {


                Proceso proceso = procesosNew.dequeue();

                proceso.calcularPrioridad();
                proceso.setEstado("PENDING");
                procesosPending.insert(proceso);
            } catch (EmptyQueueException e){
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
            } catch (EmptyHeapException e){
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

    public void finalizarProcesoTerminado(int uid){
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

    private void cargarUsuarios(String pathUsuarios){
        try{
            BufferedReader br = new BufferedReader(new FileReader(pathUsuarios));

            String linea;

            br.readLine();

            while ((linea = br.readLine()) != null){
                String[] datos = linea.split(";");

                int uid = Integer.parseInt(datos[0]);
                String alias = datos[1];
                String tipo = datos[2];

                Usuario usuario = new Usuario(uid, alias, tipo);

                agregarUsuario(usuario);

            }
            br.close();
        } catch (IOException e){
            System.out.println("Error al cargar el usuario");
        }

    }

    private void cargarProcesos(String pathProcessos){

        try{

            BufferedReader br = new BufferedReader(new FileReader(pathProcessos));

            String linea;

            while ((linea = br.readLine()) != null){
                String[] datos = linea.split(";", 4);

                int pid = Integer.parseInt(datos[0]);
                int uid = Integer.parseInt(datos[1]);
                String nombre = datos[2];
                String datosEventos = datos[3];

                Usuario propietario = usuarios.get(uid);

                MyList<Evento> eventos = new MyLinkedListImpl<>();

                datosEventos = datosEventos.substring(1,datosEventos.length()-1);
                String[] eventosSeparados = datosEventos.split("#");

                for(String eventoTexto: eventosSeparados){
                    String[] partesEvento = eventoTexto.split(":");
                    String tipoEvento = partesEvento[0].trim();

                    String instruccionesTexto = partesEvento[1].replace("[","").replace("]", "");
                    String[] instruccionesArray = instruccionesTexto.split(",");

                    MyList<String> instrucciones = new MyLinkedListImpl<>();
                    for(String instruccion: instruccionesArray){
                        instrucciones.add(instruccion.trim());
                    }

                    Evento evento = new Evento(tipoEvento, instrucciones);

                    eventos.add(evento);
                }




                Proceso proceso = new Proceso(pid, nombre, propietario, eventos);
                procesosNew.enqueue(proceso);
            }

            br.close();
        } catch (IOException e){
            System.out.println("Error al cargar el proceso");
        }

    }

    public void pload(String pathProcesos, String pathUsuarios){

        cargarUsuarios(pathUsuarios);

        cargarProcesos(pathProcesos);

    }







    /// test
}
