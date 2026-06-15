package uy.edu.um.doors;

import lombok.Getter;
import uy.edu.um.tad.hash.MyHash;
import uy.edu.um.tad.hash.MyHashImpl;
import uy.edu.um.tad.heap.EmptyHeapException;
import uy.edu.um.tad.heap.MyHeap;
import uy.edu.um.tad.heap.MyHeapImpl;
import uy.edu.um.tad.queue.EmptyQueueException;
import uy.edu.um.tad.queue.MyQueue;
import uy.edu.um.tad.queue.MyQueueImpl;
import uy.edu.um.tad.stack.EmptyStackException;
import uy.edu.um.tad.stack.MyStack;
import uy.edu.um.tad.stack.MyStackImpl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


@Getter

public class AdministradorProcesos {

    private static final int MAX_PROCESOS_TERMINADOS = 10;

    private MyQueue<Proceso> procesosNew;

    private MyHeap<Proceso> procesosPending;

    private Proceso procesosRunning;

    private MyStack<Proceso> procesosTerminados;

    private MyHash<Integer, Usuario> usuarios;



    public AdministradorProcesos() {
        procesosNew = new MyQueueImpl<>();
        procesosPending = new MyHeapImpl<>(false);
        procesosTerminados = new MyStackImpl<>();
        usuarios = new MyHashImpl<>();
        procesosRunning = null;
    }

    public void prepararProcesos() throws EmptyQueueException {

        while(!procesosNew.isEmpty()){
            Proceso proceso = procesosNew.dequeue();

            proceso.calcularPrioridad();
            proceso.setEstado("PENDING");
            procesosPending.insert(proceso);
        }
    }

    public void ejecutarProcesos() throws EmptyHeapException {
        if (procesosRunning == null) {
            Proceso proceso = procesosPending.remove();

            proceso.setEstado("RUNNING");

            procesosRunning = proceso;
        }
    }

    public void finalizarProceso()throws EmptyStackException {



        if (procesosRunning == null) {
            return;
        }

        if(procesosTerminados.size() == MAX_PROCESOS_TERMINADOS){
            while(!procesosTerminados.isEmpty()){
                Proceso proceso = procesosTerminados.pop();
                System.out.println(proceso.toString());
            }
        }


        procesosRunning.setEstado("FINISHED");
        procesosTerminados.push(procesosRunning);

        procesosRunning = null;

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

    public void pload(String pathProcesos, String pathUsuarios){

        cargarUsuarios(pathUsuarios);

        cargarProcesos(pathProcesos);

    }







    /// test
}
