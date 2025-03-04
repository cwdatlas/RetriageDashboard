export const dynamic = 'force-static'
import {Patient} from "./../models/patient";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";
const endpoint = "/patients{"
/**
 * Fetch all patients from the backend
 */
export async function getAllPatients(): Promise<Patient[]> {
    const res = await fetch(`${API_BASE_URL}`+endpoint, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
        },
    });
    if (!res.ok) {
        throw new Error(`Failed to fetch patients: ${res.statusText}`);
    }
    return res.json();
}

/**
 * Create a new patient
 */
export async function createPatient(patient: Omit<Patient, "id">): Promise<Patient> {
    const res = await fetch(`${API_BASE_URL}`+endpoint, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(patient),
    });

    if (!res.ok) {
        throw new Error(`Failed to create patient: ${res.statusText}`);
    }
    return res.json();
}

/**
 * Optionally, get a single patient by ID
 */
export async function getPatientById(id: number): Promise<Patient> {
    const res = await fetch(`${API_BASE_URL}`+endpoint+`${id}`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
        },
    });
    if (!res.ok) {
        throw new Error(`Failed to fetch patient with ID ${id}`);
    }
    return res.json();
}

/**
 * Optionally, delete a patient
 */
export async function deletePatient(id: number): Promise<void> {
    const res = await fetch(`${API_BASE_URL}`+endpoint+`${id}`, {
        method: "DELETE",
    });
    if (!res.ok) {
        throw new Error(`Failed to delete patient with ID ${id}`);
    }
}

