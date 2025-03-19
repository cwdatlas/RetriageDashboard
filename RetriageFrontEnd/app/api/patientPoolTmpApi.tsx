export const dynamic = 'force-static'
import {PatientPoolTmp} from "../models/patientPoolTmp";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";
const endpoint = "/api/pools/templates"
/**
 * Fetch all pools from the backend
 */
export async function getAllPoolTemplates(): Promise<PatientPoolTmp[]> {
    const res = await fetch(`${API_BASE_URL}`+endpoint, {
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
    const res = await fetch(`${API_BASE_URL}`+endpoint, {
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
