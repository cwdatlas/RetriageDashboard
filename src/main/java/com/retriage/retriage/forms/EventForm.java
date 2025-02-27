package com.retriage.retriage.forms;

import com.retriage.retriage.enums.Status;
import com.retriage.retriage.models.Resource;
import com.retriage.retriage.models.User;
import lombok.Data;

import java.util.List;

@Data
public class EventForm {

    private String name;
    private User director;
    private int startTime;
    private int endTime;
    private Status status;
    private List<Resource> resources;
    private List<User> nurses;

    public EventForm() {
    }

}
