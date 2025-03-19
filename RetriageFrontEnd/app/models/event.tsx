import {User} from "@/app/models/user";
import {PatientPool} from "@/app/models/patientPool";
import {Status} from "@/app/enumerations/status";

export interface Event{
    id?: number;
    name: string;
    director: User;
    startTime: number;
    duration: number;
    status: Status;
    pools: PatientPool[];
    nurses: User[];
}