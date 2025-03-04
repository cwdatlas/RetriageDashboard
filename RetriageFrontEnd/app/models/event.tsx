import {User} from "@/app/models/user";
import {Resource} from "@/app/models/resource";
import {Status} from "@/app/enumerations/status";

export interface Event{
    id?: number;
    name: string;
    director: User;
    startTime: number;
    endTime: number;
    status: Status;
    resources: Resource[];
}