import {Role} from "@/app/enumerations/role"

export interface User {
    email: string;
    firstName: string;
    lastName: string;
    role: Role;
}