package project.networking.utils;


import project.networking.protocol.ProjectClientWorker;
import project.services.IProjectServices;

import java.net.Socket;

public class ProjectConcurrentServer extends AbstractConcurrentServer{
    private IProjectServices projectServices;

    public ProjectConcurrentServer(int port, IProjectServices projectServices) {
        super(port);
        this.projectServices = projectServices;
        System.out.println("Created concurrent server");
    }

    @Override
    protected Thread createWorker(Socket client) {
        ProjectClientWorker worker = new ProjectClientWorker(projectServices, client);
        return new Thread(worker);
    }

    @Override
    public void stop() {
        System.out.println("Stopping concurrent server");
    }
}