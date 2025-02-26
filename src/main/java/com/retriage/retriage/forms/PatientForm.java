package com.retriage.retriage.forms;

import com.retriage.retriage.enums.Condition;
import com.retriage.retriage.models.Resource;
import com.retriage.retriage.models.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class PatientForm {
    @Getter
    @Setter
    private String cardId;
    @Getter
    @Setter
    private String firstName;
    @Getter
    @Setter
    private String lastName;
    @Getter
    @Setter
    private Condition condition;
    @Getter
    @Setter
    private List<Resource> resourceList;
    @Getter
    @Setter
    private User retriageNurse;

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public List<Resource> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<Resource> resourceList) {
        this.resourceList = resourceList;
    }

    public User getRetriageNurse() {
        return retriageNurse;
    }

    public void setRetriageNurse(User retriageNurse) {
        this.retriageNurse = retriageNurse;
    }

    public PatientForm() {
    }
}
