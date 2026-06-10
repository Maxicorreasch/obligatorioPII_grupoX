package uy.edu.um.doors;

import uy.edu.um.tad.heap.MyHeap;
import uy.edu.um.tad.queue.MyQueue;
import uy.edu.um.tad.stack.MyStack;

public class AdministradorProcesos {
    private MyQueue<Proceso> procesosNew;

    private MyHeap<Proceso> procesosPending;

    private Proceso procesosRunning;

    private MyStack<Proceso> procesosTerminados;

    /// test
}
