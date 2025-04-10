import {Role} from "@/app/enumerations/role"

export interface UserDto {
    username: string;
    role: Role;
}