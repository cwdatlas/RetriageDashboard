import {Condition} from "@/app/enumerations/condition"
import {User} from "@/app/models/user";

export interface Patient {
    id?: number,
    firstName: string;
    lastName: string;
    condition: Condition;
    nurse: User;
}