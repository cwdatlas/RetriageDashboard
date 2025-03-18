export const dynamic = 'force-static'
import {PatientPool} from "../models/patientPool";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";
const endpoint = "/api/pools"
/**
 * Fetch all pools from the backend
 */
export async function getAllPools(): Promise<PatientPool[]> {
    const res = await fetch(`${API_BASE_URL}`+endpoint, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
        },
    });
    if (!res.ok) {
        throw new Error(`Failed to fetch pools: ${res.statusText}`);
    }
    return res.json();
}

/**
 * Create a new pool
 */
export async function createPool(pool: { pool: PatientPool }): Promise<PatientPool> {
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
    return res.json();
}

/**
 * Optionally, get a single pool by ID
 */
export async function getPoolById(id: number): Promise<PatientPool> {
    const res = await fetch(`${API_BASE_URL}`+endpoint+`${id}`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
        },
    });
    if (!res.ok) {
        throw new Error(`Failed to fetch pool with ID ${id}`);
    }
    return res.json();
}

/**
 * Optionally, delete a pool
 */
export async function deletePool(id: number): Promise<void> {
    const res = await fetch(`${API_BASE_URL}`+endpoint+`${id}`, {
        method: "DELETE",
    });
    if (!res.ok) {
        throw new Error(`Failed to delete pool with ID ${id}`);
    }
}





