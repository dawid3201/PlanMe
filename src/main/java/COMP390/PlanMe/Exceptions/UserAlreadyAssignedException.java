package COMP390.PlanMe.Exceptions;

public class UserAlreadyAssignedException extends Exception{
    public UserAlreadyAssignedException(String message){
        super(message);
    }
    public UserAlreadyAssignedException(){
        super("User is already assigned.");
    }
}
