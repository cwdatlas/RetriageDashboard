import {UserDto} from "@/app/models/userDto";
import {User} from "./../models/user";

export const dynamic = 'force-static'

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";
const endpoint = "/api/users"

/**
 * The user API is very tentative. This api will change once okta integration is in place
 */
export async function getAllUsers(): Promise<User[]> {
    const res = await fetch(`${API_BASE_URL}` + endpoint, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
        },
    });
    if (!res.ok) {
        throw new Error(`Failed to fetch users: ${res.statusText}`);
    }
    return res.json();
}

/**
 * Create a new user
 */
export async function createUser(user: Omit<User, "id">): Promise<User> {
    const res = await fetch(`${API_BASE_URL}` + endpoint, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(user),
    });

    if (!res.ok) {
        throw new Error(`Failed to create user: ${res.statusText}`);
    }
    return res.json();
}

/**
 * Optionally, get a single user by ID
 */
export async function getUserByToken(
    setUser: (user: UserDto) => void,
    setError: (error: string) => void
): Promise<void> {
    try {
        const res = await fetch(`${API_BASE_URL}/api/users/me`, {
            method: "GET",
            credentials: "include", //  Crucial: sends token cookie
            headers: {
                "Content-Type": "application/json",
            },
        });

        if (res.ok) {
            const user: UserDto = await res.json();
            console.log("User received via token cookie:", user);
            setUser(user);
        } else if (res.status === 401) {
            setError("User token invalid or missing.");
        } else {
            setError(`Unexpected error (${res.status})`);
        }
    } catch (err: unknown) {
        console.error("Error fetching user via token cookie:", err);
        setError("Failed to fetch user credentials.");
    }
}


/**
 * Optionally, delete a user
 */
export async function deleteUser(id: number): Promise<void> {
    const res = await fetch(`${API_BASE_URL}` + endpoint + `${id}`, {
        method: "DELETE",
    });
    if (!res.ok) {
        throw new Error(`Failed to delete user with ID ${id}`);
    }
}

