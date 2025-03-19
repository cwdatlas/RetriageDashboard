import {PoolType} from "@/app/enumerations/poolType"

export interface PatientPoolTmp {
    name: string;
    processTime: number;
    usable: boolean;
    poolType: PoolType;
    poolNumber: number;
}