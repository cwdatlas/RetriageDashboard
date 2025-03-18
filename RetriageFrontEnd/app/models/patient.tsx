import {Condition} from "@/app/enumerations/condition"
import {User} from "@/app/models/user";
import {PatientPool} from "@/app/models/patientPool";

export interface Patient {
    id?: number,
    firstName: string;
    lastName: string;
    condition: Condition;
    nurse: User;
    resources: PatientPool[];
}