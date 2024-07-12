package COMP390.PlanMe.Exceptions;

public class TaskNotFoundException extends Exception{
    public TaskNotFoundException(String message){
        super(message);
    }
    public TaskNotFoundException(){
        super("Task was not found");
    }
}
