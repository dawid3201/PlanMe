package COMP390.PlanMe.entity.UpdatedEntities;

public class TaskUpdateMessage {
    private Long taskId;
    private Long barId;
    private Long newPosition;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getBarId() {
        return barId;
    }

    public void setBarId(Long barId) {
        this.barId = barId;
    }

    public Long getNewPosition() {
        return newPosition;
    }

    public void setNewPosition(Long newPosition) {
        this.newPosition = newPosition;
    }
}
