import {PoolType} from "@/app/enumerations/poolType"

export interface PatientPoolTmp {
    id?: number;
    name: string;
    processTime: number;
    autoDischarge: boolean;
    poolType: PoolType;
    poolNumber: number;
    queueSize: number;
    icon: string;
}