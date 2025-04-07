import {PatientPoolTmp} from "@/app/models/patientPoolTmp";

export interface EventTmp {
    name: string;
    duration: number;
    poolTmps: PatientPoolTmp[];
}