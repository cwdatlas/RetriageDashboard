import {PatientPool} from "@/app/models/patientPool";
import {Status} from "@/app/enumerations/status";

export interface Event {
    id?: number;
    name: string;
    startTime: number;
    duration: number;
    status: Status;
    pools: PatientPool[];
    remainingDuration: number;
    timeOfStatusChange: number;
}