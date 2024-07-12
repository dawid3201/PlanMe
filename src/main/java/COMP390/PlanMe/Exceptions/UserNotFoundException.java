package COMP390.PlanMe.Exceptions;

public class UserNotFoundException extends Exception{
    public UserNotFoundException(String message){
        super(message);
    }
    public UserNotFoundException(){
        super("User was not found");
    }
}
