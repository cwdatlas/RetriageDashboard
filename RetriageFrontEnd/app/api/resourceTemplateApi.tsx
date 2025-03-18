export const dynamic = 'force-static'
import {Resource} from "./../models/resource";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";
const endpoint = "/api/resources/templates"
/**
 * Fetch all resources from the backend
 */
export async function getAllResourceTemplates(): Promise<Resource[]> {
    const res = await fetch(`${API_BASE_URL}`+endpoint, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
        },
    });
    if (!res.ok) {
        throw new Error(`Failed to fetch resources: ${res.statusText}`);
    }
    console.log(res);
    return res.json();
}

/**
 * Create a new resource
 */
export async function createResourceTemplate(resource: Resource): Promise<Resource> {
    const res = await fetch(`${API_BASE_URL}`+endpoint, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(resource),
    });

    if (!res.ok) {
        throw new Error(`Failed to create resource: ${res.statusText}`);
    }
    console.log(res);
    return res.json();
}
