package uy.edu.um.doors;

import lombok.Getter;
import uy.edu.um.tad.heap.MyHeap;
import uy.edu.um.tad.heap.MyHeapImpl;
import uy.edu.um.tad.queue.EmptyQueueException;
import uy.edu.um.tad.queue.MyQueue;
import uy.edu.um.tad.queue.MyQueueImpl;
import uy.edu.um.tad.stack.MyStack;
import uy.edu.um.tad.stack.MyStackImpl;


@Getter

public class AdministradorProcesos {

    private static final int MAX_PROCESOS_TERMINADOS = 10;

    private MyQueue<Proceso> procesosNew;

    private MyHeap<Proceso> procesosPending;

    private Proceso procesosRunning;

    private MyStack<Proceso> procesosTerminados;



    public AdministradorProcesos() {
        procesosNew = new MyQueueImpl<>();
        procesosPending = new MyHeapImpl<>(false);
        procesosTerminados = new MyStackImpl<>();
        procesosRunning = null;
    }

    public void pepararProcesos() throws EmptyQueueException {

        while(!procesosTerminados.isEmpty()){
            Proceso proceso = procesosNew.dequeue();

            proceso.calcularPrioridad();
            proceso.setEstado("PENDING");
            procesosPending.insert(proceso);
        }
    }







    /// test
}
