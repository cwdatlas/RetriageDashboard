import {Condition} from "@/app/enumerations/condition"
import {User} from "@/app/models/user";
import {Resource} from "@/app/models/resource";

export interface Patient {
    id?: number,
    firstName: string;
    lastName: string;
    condition: Condition;
    nurse: User;
    resources: Resource[];
}