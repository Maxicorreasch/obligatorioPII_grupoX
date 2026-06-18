package uy.edu.um.doors;

public class ProcessManagerImpl implements ProcessManager{

    //EL DISEÑO DE LA ESTRUCTURA DE ALMACENAMIENTO DEBE IMPLEMENTARSE EN ESTA CLASE EN RELACIÓN CON LAS ENTIDADES QUE DEFINA

    private AdministradorProcesos administrador;

    public ProcessManagerImpl(){
        administrador = new AdministradorProcesos();
    }

    @Override
    public void loadProcessAndUserData(String processCsvPath, String usersCsvPath) {
        administrador.pload(processCsvPath, usersCsvPath);
    }

    @Override
    public void prepareProcesses() {
        administrador.prepararProcesos();
    }

    @Override
    public void executeNextProcess() {
        administrador.ejecutarProcesos();
    }

    @Override
    public void finishProcessOk() {
        administrador.finalizarProcesoOK();
    }

    @Override
    public void finishProcessError() {
        administrador.finalizarProcesoError();
    }

    @Override
    public void terminateProcess(int uid) {
        administrador.finalizarProcesoTerminado(uid);
    }

    @Override
    public void printStatus() {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void printStatusVerbose() {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void printStatusByUser(int uid) {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void printStatusByProcess(int pid) {
        System.out.println("IMPLEMENTAR");
    }
}
