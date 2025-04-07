export const dynamic = 'force-static'
import {PatientPoolTmp} from "../models/patientPoolTmp";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";
const ENDPOINT = "/api/pools/templates"

/**
 * Fetch all pools from the backend
 */
export async function getAllPoolTemplates(): Promise<PatientPoolTmp[]> {
    const res = await fetch(`${API_BASE_URL}` + ENDPOINT, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
        },
    });
    if (!res.ok) {
        throw new Error(`Failed to fetch pools: ${res.statusText}`);
    }
    console.log(res);
    return res.json();
}

/**
 * Create a new pool
 */
export async function createPoolTemplate(pool: PatientPoolTmp): Promise<PatientPoolTmp> {
    const res = await fetch(`${API_BASE_URL}` + ENDPOINT, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(pool),
    });

    if (!res.ok) {
        throw new Error(`Failed to create pool: ${res.statusText}`);
    }
    console.log(res);
    return res.json();
}

/**
 * Optionally, delete a event
 */
export async function deletePoolTemplate(id: number, setError: (error: string) => void): Promise<void> {
    const res = await fetch(`${API_BASE_URL}` + ENDPOINT + `${id}`, {
        method: "DELETE",
    });
    if (!res.ok) {
        setError(`Failed to delete pool template: ${res.statusText}`);
        return
    }
}
