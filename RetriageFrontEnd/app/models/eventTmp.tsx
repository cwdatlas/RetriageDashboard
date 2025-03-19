import {User} from "@/app/models/user";
import {PatientPoolTmp} from "@/app/models/patientPoolTmp";

export interface EventTmp{
    name: string;
    director: User;
    endTime: number;
    poolTmps: PatientPoolTmp[];
}