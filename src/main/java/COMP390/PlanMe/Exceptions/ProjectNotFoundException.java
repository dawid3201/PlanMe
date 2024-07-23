package COMP390.PlanMe.Exceptions;

public class ProjectNotFoundException extends Exception {
    public ProjectNotFoundException(String message){
        super(message);
    }
    public ProjectNotFoundException(){
        super("Project is already assigned.");
    }
}
