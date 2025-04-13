import {UserDto} from "@/app/models/userDto";

export const dynamic = 'force-static'

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";
const endpoint = "/api/users"

/**
 * Optionally, get a single user by ID
 */
export async function getUserByToken(
    setUser: (user: UserDto) => void,
    setError: (error: string) => void
): Promise<void> {
    try {
        const res = await fetch(`${API_BASE_URL}` + `${endpoint}` + `/me`, {
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

