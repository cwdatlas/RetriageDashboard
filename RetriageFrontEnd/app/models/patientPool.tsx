import {Patient} from "@/app/models/patient";
import {PoolType} from "@/app/enumerations/poolType"

export interface PatientPool {
    id?: number;
    name: string;
    processTime: number;
    patients: Patient[];
    startedProcessingAt: number;
    autoDischarge: boolean;
    poolType: PoolType;
    queueSize: number;
    icon: string;
}