import {UserDto} from "@/app/models/userDto";
import Cookies from "js-cookie";

export const dynamic = 'force-static'

let API_BASE_URL = ""
const endpoint = "/api/users"

// Helper to resolve the domain safely
function GetDomain(): void {
    const domain = Cookies.get("domain") || "localhost"
    API_BASE_URL = "https://" + domain;
}
/**
 * Optionally, get a single user by ID
 */
export async function getUserByToken(
    setUser: (user: UserDto) => void,
    setError: (error: string) => void
): Promise<void> {
    GetDomain()
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

