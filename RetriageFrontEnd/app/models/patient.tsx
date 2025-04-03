import {Condition} from "@/app/enumerations/condition"

export interface Patient {
    id?: number,
    cardId: number,
    condition: Condition;
    processed: boolean;
}