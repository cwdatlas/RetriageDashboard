package com.retriage.retriage.forms;

import com.retriage.retriage.enums.Status;
import com.retriage.retriage.models.Resource;
import com.retriage.retriage.models.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class EventForm {
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private User director;
    @Getter
    @Setter
    private int startTime;
    @Getter
    @Setter
    private int endTime;
    @Getter
    @Setter
    private Status status;
    @Getter
    @Setter
    private List<Resource> resources;
    @Getter
    @Setter
    private List<User> nurses;

    public EventForm() {
    }

}
