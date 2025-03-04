import {Patient} from "@/app/models/patient";

export interface Resource {
    id?: number;
    name: string;
    processTime: number;
    patientQueue: Patient[]; /*This needs to be a queue, but there is not a native typescript queue*/
    active: boolean;
    usable: boolean;
}